package com.eec.core.repository

import com.eec.core.db.AppDatabase
import com.eec.core.db.TransactionEntity
import com.eec.core.model.CategoryTotal
import com.eec.core.model.Currency
import com.eec.core.model.DailyTotal
import com.eec.core.model.Money
import com.eec.core.model.MonthPeriod
import com.eec.core.model.MonthTotals
import com.eec.core.model.NewTransaction
import com.eec.core.model.Transaction
import com.eec.core.model.TransactionFilter
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.time.Clock
import com.eec.core.time.dayEndExclusive
import com.eec.core.time.dayStart
import com.eec.core.time.toCairoDate
import kotlin.time.Instant
import com.eec.core.time.days
import com.eec.core.time.endExclusiveInstant
import com.eec.core.time.startInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val database: AppDatabase,
    private val clock: Clock,
) {
    private val transactionDao = database.transactionDao()
    private val categoryDao = database.categoryDao()

    val all: Flow<List<TransactionListItem>> =
        transactionDao.observeAll().map { rows -> rows.map { it.toDomain() } }

    fun observe(filter: TransactionFilter): Flow<List<TransactionListItem>> {
        val pattern = filter.noteQuery
            ?.takeIf { it.isNotBlank() }
            ?.let(LikePattern::contains)
        return transactionDao.observeFiltered(
            startInclusive = filter.startInclusive?.toEpochMilliseconds(),
            endExclusive = filter.endExclusive?.toEpochMilliseconds(),
            kind = filter.kind?.name,
            categoryId = filter.categoryId,
            paymentMethod = filter.paymentMethod?.name,
            notePattern = pattern,
        ).map { rows -> rows.map { it.toDomain() } }
    }

    suspend fun add(draft: NewTransaction): WriteResult<Transaction> {
        val note = draft.note?.trim()?.ifEmpty { null }
        val validation = validate(draft, note)
        if (validation != null) return WriteResult.Failure(validation)
        return try {
            if (categoryDao.byId(draft.categoryId) == null) {
                return WriteResult.Failure(
                    RepositoryError.Validation(ValidationField.CATEGORY, ValidationReason.NOT_FOUND),
                )
            }
            val now = clock.now().toEpochMilliseconds()
            val entity = draft.toEntity(id = newId(), createdAt = now, updatedAt = now, note = note)
            transactionDao.insert(entity)
            WriteResult.Success(entity.toDomain())
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            WriteResult.Failure(RepositoryError.Storage(error))
        }
    }

    suspend fun update(id: String, draft: NewTransaction): WriteResult<Transaction> {
        val note = draft.note?.trim()?.ifEmpty { null }
        val validation = validate(draft, note)
        if (validation != null) return WriteResult.Failure(validation)
        return try {
            val existing = transactionDao.byId(id)
                ?: return WriteResult.Failure(RepositoryError.NotFound(id))
            if (categoryDao.byId(draft.categoryId) == null) {
                return WriteResult.Failure(
                    RepositoryError.Validation(ValidationField.CATEGORY, ValidationReason.NOT_FOUND),
                )
            }
            val entity = draft.toEntity(
                id = existing.id,
                createdAt = existing.createdAt,
                updatedAt = clock.now().toEpochMilliseconds(),
                note = note,
            )
            val updated = transactionDao.update(entity)
            if (updated == 0) {
                WriteResult.Failure(RepositoryError.NotFound(id))
            } else {
                WriteResult.Success(entity.toDomain())
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            WriteResult.Failure(RepositoryError.Storage(error))
        }
    }

    suspend fun delete(id: String): WriteResult<Unit> = try {
        if (transactionDao.deleteById(id) == 0) {
            WriteResult.Failure(RepositoryError.NotFound(id))
        } else {
            WriteResult.Success(Unit)
        }
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        WriteResult.Failure(RepositoryError.Storage(error))
    }

    suspend fun monthTotals(period: MonthPeriod): MonthTotals {
        val rows = transactionDao.totalsByKind(
            period.startInstant().toEpochMilliseconds(),
            period.endExclusiveInstant().toEpochMilliseconds(),
        )
        var expense = 0L
        var income = 0L
        rows.forEach { row ->
            when (row.kind) {
                TransactionKind.EXPENSE.name -> expense += row.totalMinor
                TransactionKind.INCOME.name -> income += row.totalMinor
            }
        }
        return MonthTotals(expense = Money.ofMinor(expense), income = Money.ofMinor(income))
    }

    suspend fun categoryTotals(period: MonthPeriod, kind: TransactionKind): List<CategoryTotal> {
        val rows = transactionDao.categoryTotals(
            kind = kind.name,
            startInclusive = period.startInstant().toEpochMilliseconds(),
            endExclusive = period.endExclusiveInstant().toEpochMilliseconds(),
        )
        return rows.map { it.toDomain() }
    }

    suspend fun dailyExpenseTotals(period: MonthPeriod): List<DailyTotal> {
        val rows = transactionDao.dailyTotals(
            kind = TransactionKind.EXPENSE.name,
            startInclusive = period.startInstant().toEpochMilliseconds(),
            endExclusive = period.endExclusiveInstant().toEpochMilliseconds(),
        )
        val byDate: Map<kotlinx.datetime.LocalDate, Long> = rows.associate { row ->
            kotlin.time.Instant.fromEpochMilliseconds(row.dayBucket * 86400000L - 7200000L).toCairoDate() to row.totalMinor
        }
        return period.days().map { date ->
            DailyTotal(date = date, amount = Money.ofMinor(byDate[date] ?: 0L))
        }
    }

    suspend fun exportRows(
        startInclusive: kotlin.time.Instant? = null,
        endExclusive: kotlin.time.Instant? = null,
    ): List<TransactionListItem> = transactionDao.exportRows(
        startInclusive = startInclusive?.toEpochMilliseconds(),
        endExclusive = endExclusive?.toEpochMilliseconds(),
    ).map { it.toDomain() }

    private fun validate(draft: NewTransaction, note: String?): RepositoryError? = when {
        draft.amount <= Money.Zero ->
            RepositoryError.Validation(ValidationField.AMOUNT, ValidationReason.MUST_BE_POSITIVE)

        note != null && note.length > MAX_NOTE_LENGTH ->
            RepositoryError.Validation(ValidationField.NOTE, ValidationReason.TOO_LONG)

        draft.categoryId.isBlank() ->
            RepositoryError.Validation(ValidationField.CATEGORY, ValidationReason.REQUIRED)

        else -> null
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun newId(): String = Uuid.random().toString()

    private fun NewTransaction.toEntity(
        id: String,
        createdAt: Long,
        updatedAt: Long,
        note: String?,
    ): TransactionEntity = TransactionEntity(
        id = id,
        kind = kind.name,
        amountMinor = amount.minor,
        currency = Currency.EGP,
        categoryId = categoryId,
        occurredAt = occurredAt.toEpochMilliseconds(),
        note = note,
        paymentMethod = paymentMethod.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private companion object {
        const val MAX_NOTE_LENGTH: Int = 280
    }
}
