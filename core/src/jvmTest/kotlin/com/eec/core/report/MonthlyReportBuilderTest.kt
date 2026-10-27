package com.eec.core.report

import com.eec.core.model.CategoryTotal
import com.eec.core.model.DailyTotal
import com.eec.core.model.Money
import com.eec.core.model.MonthPeriod
import com.eec.core.model.MonthTotals
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

class MonthlyReportBuilderTest {
    private val period = MonthPeriod(year = 2026, monthNumber = 3)

    @Test
    fun buildsDeterministicSnapshotFromRows() {
        val snapshot = MonthlyReportBuilder.build(
            period = period,
            totals = MonthTotals(expense = Money.ofMinor(300000), income = Money.ofMinor(1000000)),
            previousTotals = MonthTotals(expense = Money.ofMinor(250000), income = Money.ofMinor(900000)),
            daily = listOf(
                DailyTotal(date = LocalDate(2026, 3, 1), amount = Money.ofMinor(100000)),
                DailyTotal(date = LocalDate(2026, 3, 2), amount = Money.ofMinor(50000)),
            ),
            byCategory = listOf(
                category("shopping", "تسوق", "plum", 20000),
                category("food", "طعام", "teal", 150000),
                category("bills", "فواتير", "green", 40000),
                category("transport", "مواصلات", "gold", 90000),
            ),
        )

        assertEquals(31, snapshot.daily.size)
        assertEquals(Money.ofMinor(100000), snapshot.daily.first().amount)
        assertEquals(Money.Zero, snapshot.daily[2].amount)
        assertEquals(150000L, snapshot.daily.sumOf { it.amount.minor })

        assertEquals(
            listOf("food", "transport", "bills", "shopping"),
            snapshot.byCategory.map { it.categoryId },
        )
        assertEquals(listOf("food", "transport", "bills"), snapshot.topCategories.map { it.categoryId })
        assertEquals(listOf(50, 30, 13), snapshot.topCategories.map { it.sharePercent })
        assertEquals(20000L, snapshot.byCategory.last().amount.minor)

        assertEquals(700000L, snapshot.net.minor)
        assertEquals(650000L, snapshot.previousNet.minor)
        assertEquals(50000L, snapshot.expenseDelta.minor)
        assertEquals(100000L, snapshot.incomeDelta.minor)
        assertEquals(9677L, snapshot.averageDailyExpense.minor)
    }

    @Test
    fun emptySnapshotZeroFillsEveryDay() {
        val snapshot = MonthlyReportBuilder.empty(period)
        assertEquals(31, snapshot.daily.size)
        assertTrue(snapshot.daily.all { it.amount.isZero })
        assertTrue(snapshot.byCategory.isEmpty())
        assertTrue(snapshot.topCategories.isEmpty())
        assertEquals(Money.Zero, snapshot.net)
    }

    @Test
    fun tiesBreakByName() {
        val snapshot = MonthlyReportBuilder.build(
            period = period,
            totals = MonthTotals(expense = Money.ofMinor(20000), income = Money.Zero),
            previousTotals = MonthTotals(expense = Money.Zero, income = Money.Zero),
            daily = emptyList(),
            byCategory = listOf(
                category("b", "ب", "gold", 10000),
                category("a", "أ", "teal", 10000),
            ),
        )
        assertEquals(listOf("a", "b"), snapshot.byCategory.map { it.categoryId })
    }

    private fun category(id: String, name: String, colorKey: String, minor: Long) = CategoryTotal(
        categoryId = id,
        name = name,
        colorKey = colorKey,
        iconKey = "cash",
        amount = Money.ofMinor(minor),
    )
}
