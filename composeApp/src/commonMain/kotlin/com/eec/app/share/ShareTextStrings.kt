package com.eec.app.share

import androidx.compose.runtime.Composable
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.share_currency
import com.eec.app.resources.share_expense_label
import com.eec.app.resources.share_income_label
import com.eec.app.resources.share_list_separator
import com.eec.app.resources.share_net_label
import com.eec.app.resources.share_no_categories
import com.eec.app.resources.share_period_label
import com.eec.app.resources.share_range_separator
import com.eec.app.resources.share_report_title
import com.eec.app.resources.share_top_categories_label
import com.eec.app.ui.monthResources
import com.eec.core.share.ShareTextStrings

@Composable
fun shareTextStrings(): ShareTextStrings = ShareTextStrings(
    title = appString(Res.string.share_report_title),
    periodLabel = appString(Res.string.share_period_label),
    rangeSeparator = appString(Res.string.share_range_separator),
    expenseLabel = appString(Res.string.share_expense_label),
    incomeLabel = appString(Res.string.share_income_label),
    netLabel = appString(Res.string.share_net_label),
    topCategoriesLabel = appString(Res.string.share_top_categories_label),
    noCategories = appString(Res.string.share_no_categories),
    listSeparator = appString(Res.string.share_list_separator),
    currency = appString(Res.string.share_currency),
    monthNames = monthResources.map { appString(it) },
)
