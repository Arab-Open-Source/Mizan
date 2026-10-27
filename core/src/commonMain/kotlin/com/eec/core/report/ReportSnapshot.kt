package com.eec.core.report

import com.eec.core.model.CategoryTotal
import com.eec.core.model.DailyTotal
import com.eec.core.model.Money
import com.eec.core.model.MonthPeriod
import com.eec.core.model.MonthTotals

data class CategoryShare(
    val categoryId: String,
    val name: String,
    val colorKey: String,
    val amount: Money,
    val sharePercent: Int,
)

data class ReportSnapshot(
    val period: MonthPeriod,
    val totals: MonthTotals,
    val previousTotals: MonthTotals,
    val daily: List<DailyTotal>,
    val byCategory: List<CategoryTotal>,
    val topCategories: List<CategoryShare>,
) {
    val net: Money get() = totals.net
    val previousNet: Money get() = previousTotals.net
    val expenseDelta: Money get() = totals.expense - previousTotals.expense
    val incomeDelta: Money get() = totals.income - previousTotals.income
    val averageDailyExpense: Money
        get() = Money.ofMinor(totals.expense.minor / period.numberOfDays)
}
