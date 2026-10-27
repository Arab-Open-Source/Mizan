package com.eec.core.report

import com.eec.core.model.CategoryTotal
import com.eec.core.model.DailyTotal
import com.eec.core.model.Money
import com.eec.core.model.MonthPeriod
import com.eec.core.model.MonthTotals
import com.eec.core.time.days

object MonthlyReportBuilder {
    fun build(
        period: MonthPeriod,
        totals: MonthTotals,
        previousTotals: MonthTotals,
        daily: List<DailyTotal>,
        byCategory: List<CategoryTotal>,
        topCount: Int = 3,
    ): ReportSnapshot {
        val sortedCategories = byCategory.sortedWith(
            compareByDescending<CategoryTotal> { it.amount.minor }.thenBy { it.name },
        )
        val totalExpense = totals.expense.minor
        val topCategories = sortedCategories
            .take(topCount)
            .map { category ->
                CategoryShare(
                    categoryId = category.categoryId,
                    name = category.name,
                    colorKey = category.colorKey,
                    amount = category.amount,
                    sharePercent = percentage(category.amount.minor, totalExpense),
                )
            }

        return ReportSnapshot(
            period = period,
            totals = totals,
            previousTotals = previousTotals,
            daily = fillDaily(period, daily),
            byCategory = sortedCategories,
            topCategories = topCategories,
        )
    }

    fun empty(period: MonthPeriod): ReportSnapshot = build(
        period = period,
        totals = MonthTotals(expense = Money.Zero, income = Money.Zero),
        previousTotals = MonthTotals(expense = Money.Zero, income = Money.Zero),
        daily = emptyList(),
        byCategory = emptyList(),
    )

    private fun fillDaily(period: MonthPeriod, daily: List<DailyTotal>): List<DailyTotal> {
        val byDate = daily.associateBy { it.date }
        return period.days().map { date ->
            byDate[date] ?: DailyTotal(date = date, amount = Money.Zero)
        }
    }

    private fun percentage(part: Long, whole: Long): Int {
        if (whole <= 0L || part <= 0L) return 0
        val numerator = part.toULong() * 100uL + whole.toULong() / 2uL
        return (numerator / whole.toULong()).toInt()
    }
}
