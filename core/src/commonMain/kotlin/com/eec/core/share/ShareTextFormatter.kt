package com.eec.core.share

import com.eec.core.format.AmountFormatter
import com.eec.core.report.ReportSnapshot

class ShareTextFormatter(
    private val strings: ShareTextStrings,
) {
    fun format(snapshot: ReportSnapshot): String {
        val period = snapshot.period
        val categories = if (snapshot.topCategories.isEmpty()) {
            strings.noCategories
        } else {
            snapshot.topCategories.joinToString(separator = "${strings.listSeparator} ") { category ->
                "${category.name} ${category.sharePercent}%"
            }
        }
        return buildString {
            append(strings.title)
            append(' ')
            append(monthName(period.monthNumber))
            append(' ')
            append(period.year)
            append('\n')
            append(strings.periodLabel)
            append(' ')
            append(period.firstDay)
            append(' ')
            append(strings.rangeSeparator)
            append(' ')
            append(period.lastDay)
            append('\n')
            append(strings.expenseLabel)
            append(' ')
            append(AmountFormatter.format(snapshot.totals.expense.minor))
            append(' ')
            append(strings.currency)
            append('\n')
            append(strings.incomeLabel)
            append(' ')
            append(AmountFormatter.format(snapshot.totals.income.minor))
            append(' ')
            append(strings.currency)
            append('\n')
            append(strings.netLabel)
            append(' ')
            append(AmountFormatter.format(snapshot.net.minor))
            append(' ')
            append(strings.currency)
            append('\n')
            append(strings.topCategoriesLabel)
            append(' ')
            append(categories)
        }
    }

    fun subject(snapshot: ReportSnapshot): String =
        "${strings.title} ${monthName(snapshot.period.monthNumber)} ${snapshot.period.year}"

    private fun monthName(monthNumber: Int): String = strings.monthNames[monthNumber - 1]
}
