package com.eec.core.share

import com.eec.core.model.CategoryTotal
import com.eec.core.model.Money
import com.eec.core.model.MonthPeriod
import com.eec.core.model.MonthTotals
import com.eec.core.report.MonthlyReportBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class ShareTextFormatterTest {
    private val period = MonthPeriod(year = 2026, monthNumber = 3)

    @Test
    fun formatsArabicSummaryTemplate() {
        val snapshot = MonthlyReportBuilder.build(
            period = period,
            totals = MonthTotals(expense = Money.ofMinor(300000), income = Money.ofMinor(1000000)),
            previousTotals = MonthTotals(expense = Money.Zero, income = Money.Zero),
            daily = emptyList(),
            byCategory = listOf(
                category("food", "طعام", "teal", 150000),
                category("transport", "مواصلات", "gold", 90000),
                category("bills", "فواتير", "green", 40000),
                category("shopping", "تسوق", "plum", 20000),
            ),
        )

        val expected = listOf(
            "EEC — تقرير مارس 2026",
            "الفترة: 2026-03-01 إلى 2026-03-31",
            "المصروف: 3,000.00 ج.م",
            "الدخل: 10,000.00 ج.م",
            "الصافي: 7,000.00 ج.م",
            "أعلى التصنيفات: طعام 50%، مواصلات 30%، فواتير 13%",
        ).joinToString("\n")

        assertEquals(expected, ShareTextFormatter.format(snapshot))
        assertEquals("EEC — تقرير مارس 2026", ShareTextFormatter.subject(snapshot))
    }

    @Test
    fun handlesMissingCategories() {
        val snapshot = MonthlyReportBuilder.empty(period)
        val text = ShareTextFormatter.format(snapshot)
        assertEquals("أعلى التصنيفات: لا توجد", text.lines().last())
    }

    @Test
    fun formatsNegativeNet() {
        val snapshot = MonthlyReportBuilder.build(
            period = period,
            totals = MonthTotals(expense = Money.ofMinor(5000), income = Money.ofMinor(1000)),
            previousTotals = MonthTotals(expense = Money.Zero, income = Money.Zero),
            daily = emptyList(),
            byCategory = emptyList(),
        )
        assertEquals("الصافي: -40.00 ج.م", ShareTextFormatter.format(snapshot).lines()[4])
    }

    private fun category(id: String, name: String, colorKey: String, minor: Long) = CategoryTotal(
        categoryId = id,
        name = name,
        colorKey = colorKey,
        iconKey = "cash",
        amount = Money.ofMinor(minor),
    )
}
