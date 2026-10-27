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

    private val arabic = ShareTextFormatter(
        ShareTextStrings(
            title = "Mizan — تقرير",
            periodLabel = "الفترة:",
            rangeSeparator = "إلى",
            expenseLabel = "المصروف:",
            incomeLabel = "الدخل:",
            netLabel = "الصافي:",
            topCategoriesLabel = "أعلى التصنيفات:",
            noCategories = "لا توجد",
            listSeparator = "،",
            currency = "ج.م",
            monthNames = listOf(
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
            ),
        ),
    )

    private val english = ShareTextFormatter(
        ShareTextStrings(
            title = "Mizan — Report",
            periodLabel = "Period:",
            rangeSeparator = "to",
            expenseLabel = "Expense:",
            incomeLabel = "Income:",
            netLabel = "Net:",
            topCategoriesLabel = "Top categories:",
            noCategories = "None",
            listSeparator = ",",
            currency = "EGP",
            monthNames = listOf(
                "January",
                "February",
                "March",
                "April",
                "May",
                "June",
                "July",
                "August",
                "September",
                "October",
                "November",
                "December",
            ),
        ),
    )

    @Test
    fun formatsArabicSummaryTemplate() {
        val snapshot = snapshot()

        val expected = listOf(
            "Mizan — تقرير مارس 2026",
            "الفترة: 2026-03-01 إلى 2026-03-31",
            "المصروف: 3,000.00 ج.م",
            "الدخل: 10,000.00 ج.م",
            "الصافي: 7,000.00 ج.م",
            "أعلى التصنيفات: طعام 50%، مواصلات 30%، فواتير 13%",
        ).joinToString("\n")

        assertEquals(expected, arabic.format(snapshot))
        assertEquals("Mizan — تقرير مارس 2026", arabic.subject(snapshot))
    }

    @Test
    fun formatsEnglishSummaryTemplate() {
        val snapshot = snapshot()

        val expected = listOf(
            "Mizan — Report March 2026",
            "Period: 2026-03-01 to 2026-03-31",
            "Expense: 3,000.00 EGP",
            "Income: 10,000.00 EGP",
            "Net: 7,000.00 EGP",
            "Top categories: طعام 50%, مواصلات 30%, فواتير 13%",
        ).joinToString("\n")

        assertEquals(expected, english.format(snapshot))
        assertEquals("Mizan — Report March 2026", english.subject(snapshot))
    }

    @Test
    fun handlesMissingCategories() {
        val snapshot = MonthlyReportBuilder.empty(period)
        val text = arabic.format(snapshot)
        assertEquals("أعلى التصنيفات: لا توجد", text.lines().last())
        assertEquals("Top categories: None", english.format(snapshot).lines().last())
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
        assertEquals("الصافي: -40.00 ج.م", arabic.format(snapshot).lines()[4])
        assertEquals("Net: -40.00 EGP", english.format(snapshot).lines()[4])
    }

    private fun snapshot() = MonthlyReportBuilder.build(
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

    private fun category(id: String, name: String, colorKey: String, minor: Long) = CategoryTotal(
        categoryId = id,
        name = name,
        colorKey = colorKey,
        iconKey = "cash",
        amount = Money.ofMinor(minor),
    )
}
