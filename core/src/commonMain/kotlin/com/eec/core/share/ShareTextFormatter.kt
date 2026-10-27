package com.eec.core.share

import com.eec.core.format.AmountFormatter
import com.eec.core.report.ReportSnapshot

object ShareTextFormatter {
    private val monthNames: List<String> = listOf(
        "يناير",
        "فبراير",
        "مارس",
        "أبريل",
        "مايو",
        "يونيو",
        "يوليو",
        "أغسطس",
        "سبتمبر",
        "أكتوبر",
        "نوفمبر",
        "ديسمبر",
    )

    fun format(snapshot: ReportSnapshot): String {
        val period = snapshot.period
        val categories = if (snapshot.topCategories.isEmpty()) {
            "لا توجد"
        } else {
            snapshot.topCategories.joinToString(separator = "، ") { category ->
                "${category.name} ${category.sharePercent}%"
            }
        }
        return buildString {
            append("EEC — تقرير ")
            append(monthNames[period.monthNumber - 1])
            append(' ')
            append(period.year)
            append('\n')
            append("الفترة: ")
            append(period.firstDay)
            append(" إلى ")
            append(period.lastDay)
            append('\n')
            append("المصروف: ")
            append(AmountFormatter.format(snapshot.totals.expense.minor))
            append(" ج.م")
            append('\n')
            append("الدخل: ")
            append(AmountFormatter.format(snapshot.totals.income.minor))
            append(" ج.م")
            append('\n')
            append("الصافي: ")
            append(AmountFormatter.format(snapshot.net.minor))
            append(" ج.م")
            append('\n')
            append("أعلى التصنيفات: ")
            append(categories)
        }
    }

    fun subject(snapshot: ReportSnapshot): String =
        "EEC — تقرير ${monthNames[snapshot.period.monthNumber - 1]} ${snapshot.period.year}"
}
