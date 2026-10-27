package com.eec.core.db

import com.eec.core.model.Money
import com.eec.core.model.MonthPeriod
import com.eec.core.model.NewTransaction
import com.eec.core.model.PaymentMethod
import com.eec.core.model.Transaction
import com.eec.core.model.TransactionFilter
import com.eec.core.model.TransactionKind
import com.eec.core.repository.TransactionRepository
import com.eec.core.repository.WriteResult
import com.eec.core.repository.valueOrNull
import com.eec.core.time.CairoTimeZone
import com.eec.core.time.FixedClock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant

class DatabaseTest {
    private lateinit var database: AppDatabase

    @BeforeTest
    fun setUp() {
        database = createInMemoryDatabase()
    }

    @AfterTest
    fun tearDown() {
        database.close()
    }

    @Test
    fun seederIsIdempotentAndCreatesThirteenCategories() = runBlocking {
        val seeder = CategorySeeder(database)
        seeder.seed()
        seeder.seed()

        assertEquals(13, database.categoryDao().count())
        val categories = database.categoryDao().all()
        assertEquals(9, categories.count { it.kind == TransactionKind.EXPENSE.name })
        assertEquals(4, categories.count { it.kind == TransactionKind.INCOME.name })
        assertEquals(
            listOf("طعام", "مواصلات", "سكن", "فواتير", "صحة", "تعليم", "تسوق", "ترفيه", "أخرى"),
            categories.filter { it.kind == "EXPENSE" }.sortedBy { it.sortOrder }.map { it.name },
        )
        assertEquals(
            listOf("راتب", "عمل حر", "هدايا", "أخرى"),
            categories.filter { it.kind == "INCOME" }.sortedBy { it.sortOrder }.map { it.name },
        )
    }

    @Test
    fun aggregatesTotalsCategoriesAndDays() = runBlocking {
        CategorySeeder(database).seed()
        val repository = TransactionRepository(database, FixedClock(Instant.fromEpochMilliseconds(1_773_000_000_000L)))

        val first = add(repository, TransactionKind.EXPENSE, 15000, "food", 15, 12)
        val second = add(repository, TransactionKind.EXPENSE, 25000, "food", 20, 9)
        val income = add(repository, TransactionKind.INCOME, 500000, "salary", 1, 8)

        assertTrue(first is WriteResult.Success)
        assertTrue(second is WriteResult.Success)
        assertTrue(income is WriteResult.Success)

        val totals = repository.monthTotals(MonthPeriod(2026, 3))
        assertEquals(40000L, totals.expense.minor)
        assertEquals(500000L, totals.income.minor)
        assertEquals(460000L, totals.net.minor)

        val categories = repository.categoryTotals(MonthPeriod(2026, 3), TransactionKind.EXPENSE)
        assertEquals(1, categories.size)
        assertEquals("طعام", categories.single().name)
        assertEquals("teal", categories.single().colorKey)
        assertEquals(40000L, categories.single().amount.minor)

        val daily = repository.dailyExpenseTotals(MonthPeriod(2026, 3))
        assertEquals(31, daily.size)
        assertEquals(15000L, daily[14].amount.minor)
        assertEquals(25000L, daily[19].amount.minor)
        assertEquals(0L, daily[0].amount.minor)
        assertEquals(40000L, daily.sumOf { it.amount.minor })
    }

    @Test
    fun filtersByKindPaymentAndEscapedNote() = runBlocking {
        CategorySeeder(database).seed()
        val repository = TransactionRepository(database, FixedClock(Instant.fromEpochMilliseconds(1_773_000_000_000L)))

        add(repository, TransactionKind.EXPENSE, 1200, "food", 15, 12, note = "قهوة 100%")
        add(repository, TransactionKind.EXPENSE, 1500, "transport", 15, 12, note = "100x")
        add(repository, TransactionKind.INCOME, 300000, "salary", 1, 8, note = "شهر مارس")

        val percentMatches = repository.observe(TransactionFilter(noteQuery = "100%")).first()
        assertEquals(1, percentMatches.size)
        assertEquals("قهوة 100%", percentMatches.single().note)

        val incomeMatches = repository.observe(
            TransactionFilter(kind = TransactionKind.INCOME, paymentMethod = PaymentMethod.CASH),
        ).first()
        assertEquals(1, incomeMatches.size)
        assertEquals("راتب", incomeMatches.single().categoryName)

        val injectionAttempt = repository.observe(TransactionFilter(noteQuery = "' OR 1=1 --")).first()
        assertTrue(injectionAttempt.isEmpty())
    }

    @Test
    fun rejectsInvalidTransactions() = runBlocking {
        CategorySeeder(database).seed()
        val repository = TransactionRepository(database, FixedClock(Instant.fromEpochMilliseconds(1_773_000_000_000L)))
        val occurredAt = occurredAt(day = 15)

        val zero = repository.add(
            NewTransaction(TransactionKind.EXPENSE, Money.Zero, "food", occurredAt, null, PaymentMethod.CASH),
        )
        val longNote = repository.add(
            NewTransaction(
                TransactionKind.EXPENSE,
                Money.ofMinor(100),
                "food",
                occurredAt,
                "x".repeat(281),
                PaymentMethod.CASH,
            ),
        )
        val missingCategory = repository.add(
            NewTransaction(
                TransactionKind.EXPENSE,
                Money.ofMinor(100),
                "missing",
                occurredAt,
                null,
                PaymentMethod.CASH,
            ),
        )

        assertTrue(zero is WriteResult.Failure)
        assertTrue(longNote is WriteResult.Failure)
        assertTrue(missingCategory is WriteResult.Failure)
        assertEquals(13, database.categoryDao().count())
        assertTrue(repository.all.first().isEmpty())
    }

    @Test
    fun updatesAndDeletesTransactions() = runBlocking {
        CategorySeeder(database).seed()
        val repository = TransactionRepository(database, FixedClock(Instant.fromEpochMilliseconds(1_773_000_000_000L)))
        val transaction = repository.add(
            NewTransaction(
                TransactionKind.EXPENSE,
                Money.ofMinor(1000),
                "food",
                occurredAt(day = 15),
                "غداء",
                PaymentMethod.CARD,
            ),
        ).valueOrNull() ?: error("insert failed")

        val updated = repository.update(
            transaction.id,
            NewTransaction(
                TransactionKind.EXPENSE,
                Money.ofMinor(2500),
                "transport",
                occurredAt(day = 15),
                null,
                PaymentMethod.WALLET,
            ),
        )
        assertTrue(updated is WriteResult.Success)
        assertEquals(2500L, repository.all.first().single().amount.minor)
        assertEquals("مواصلات", repository.all.first().single().categoryName)

        assertTrue(repository.delete(transaction.id) is WriteResult.Success)
        assertTrue(repository.all.first().isEmpty())
        assertTrue(repository.delete(transaction.id) is WriteResult.Failure)
    }

    private suspend fun add(
        repository: TransactionRepository,
        kind: TransactionKind,
        minor: Long,
        categoryId: String,
        day: Int,
        hour: Int,
        note: String? = null,
    ): WriteResult<Transaction> = repository.add(
        NewTransaction(
            kind = kind,
            amount = Money.ofMinor(minor),
            categoryId = categoryId,
            occurredAt = occurredAt(day = day, hour = hour),
            note = note,
            paymentMethod = PaymentMethod.CASH,
        ),
    )

    private fun occurredAt(day: Int, hour: Int = 0): Instant =
        LocalDateTime(2026, 3, day, hour, 0).toInstant(CairoTimeZone)
}
