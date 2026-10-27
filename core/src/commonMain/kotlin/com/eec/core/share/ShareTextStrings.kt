package com.eec.core.share

/**
 * Locale-dependent pieces of the report share text.
 *
 * Resolved from Compose resources by composeApp so core stays free of UI dependencies.
 */
data class ShareTextStrings(
    val title: String,
    val periodLabel: String,
    val rangeSeparator: String,
    val expenseLabel: String,
    val incomeLabel: String,
    val netLabel: String,
    val topCategoriesLabel: String,
    val noCategories: String,
    val listSeparator: String,
    val currency: String,
    val monthNames: List<String>,
)
