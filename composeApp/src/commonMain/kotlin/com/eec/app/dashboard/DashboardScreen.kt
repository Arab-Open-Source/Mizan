package com.eec.app.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.action_add_transaction
import com.eec.app.resources.action_retry
import com.eec.app.resources.chart_daily_description
import com.eec.app.resources.chart_daily_total
import com.eec.app.resources.dashboard_empty_message
import com.eec.app.resources.dashboard_empty_title
import com.eec.app.resources.dashboard_error_title
import com.eec.app.resources.dashboard_loading
import com.eec.app.resources.no_expenses_this_month
import com.eec.app.resources.no_transactions_this_month
import com.eec.app.resources.section_daily_spend
import com.eec.app.resources.section_recent_transactions
import com.eec.app.resources.section_top_categories
import com.eec.app.ui.CategoryDonutChart
import com.eec.app.ui.CategoryShareRow
import com.eec.app.ui.MaxWidthContent
import com.eec.app.ui.MonthSwitcher
import com.eec.app.ui.SummaryStats
import com.eec.app.ui.paymentMethodIcon
import com.eec.app.ui.transactionSubtitle
import com.eec.core.format.AmountFormatter
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.report.ReportSnapshot
import com.eec.design.component.AmountSign
import com.eec.design.component.BarChart
import com.eec.design.component.BarChartPoint
import com.eec.design.component.EmptyState
import com.eec.design.component.ErrorState
import com.eec.design.component.LoadingState
import com.eec.design.component.SectionHeader
import com.eec.design.component.TransactionRow
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecTheme

@Composable
fun DashboardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    onAdd: () -> Unit,
    onEdit: (TransactionListItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    MaxWidthContent(modifier) {
        when {
            state.loading -> LoadingState(message = appString(Res.string.dashboard_loading))
            state.error != null -> ErrorState(
                title = appString(Res.string.dashboard_error_title),
                message = state.error,
                onRetry = { onEvent(DashboardEvent.Retry) },
                retryLabel = appString(Res.string.action_retry),
            )

            state.isEmpty -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = EecTheme.spacing.screen, vertical = EecTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
            ) {
                MonthSwitcher(
                    period = state.period,
                    onPrevious = { onEvent(DashboardEvent.PreviousMonth) },
                    onNext = { onEvent(DashboardEvent.NextMonth) },
                )
                EmptyState(
                    title = appString(Res.string.dashboard_empty_title),
                    message = appString(Res.string.dashboard_empty_message),
                    icon = EecIcons.transactions,
                    actionLabel = appString(Res.string.action_add_transaction),
                    onAction = onAdd,
                )
            }

            else -> state.snapshot?.let { snapshot ->
                DashboardContent(
                    state = state,
                    snapshot = snapshot,
                    onEvent = onEvent,
                    onEdit = onEdit,
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardState,
    snapshot: ReportSnapshot,
    onEvent: (DashboardEvent) -> Unit,
    onEdit: (TransactionListItem) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = EecTheme.spacing.screen,
            end = EecTheme.spacing.screen,
            top = EecTheme.spacing.xs,
            bottom = 96.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
    ) {
        item {
            MonthSwitcher(
                period = state.period,
                onPrevious = { onEvent(DashboardEvent.PreviousMonth) },
                onNext = { onEvent(DashboardEvent.NextMonth) },
            )
        }
        item { SummaryStats(snapshot = snapshot) }
        item { SectionHeader(title = appString(Res.string.section_daily_spend)) }
        item {
            val total = snapshot.daily.sumOf { it.amount.minor }
            BarChart(
                points = snapshot.daily.map { day -> BarChartPoint(day.date.day.toString(), day.amount.minor) },
                totalDescription = appString(
                    Res.string.chart_daily_total,
                    appString(Res.string.chart_daily_description),
                    AmountFormatter.format(total),
                ),
                barColor = EecTheme.colors.tertiary,
                formatValue = { AmountFormatter.format(it) },
            )
        }
        item {
            SectionHeader(title = appString(Res.string.section_top_categories))
        }
        if (snapshot.topCategories.isEmpty()) {
            item { MutedLine(text = appString(Res.string.no_expenses_this_month)) }
        } else {
            item { CategoryDonutChart(categories = snapshot.topCategories) }
            items(snapshot.topCategories, key = { it.categoryId }) { category ->
                CategoryShareRow(category = category)
            }
        }
        item {
            SectionHeader(title = appString(Res.string.section_recent_transactions))
        }
        if (state.recent.isEmpty()) {
            item { MutedLine(text = appString(Res.string.no_transactions_this_month)) }
        } else {
            items(state.recent, key = { it.id }) { item ->
                TransactionRow(
                    title = item.categoryName,
                    subtitle = transactionSubtitle(item),
                    amountText = AmountFormatter.format(item.amount.minor),
                    amountSign = when (item.kind) {
                        TransactionKind.EXPENSE -> AmountSign.Negative
                        TransactionKind.INCOME -> AmountSign.Positive
                    },
                    note = item.note,
                    leading = { TransactionLeadingIcon(item) },
                    onClick = { onEdit(item) },
                )
            }
        }
    }
}

@Composable
private fun MutedLine(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = EecTheme.typography.body,
        color = EecTheme.colors.onSurfaceVariant,
    )
}

@Composable
private fun TransactionLeadingIcon(item: TransactionListItem) {
    val color = EecTheme.colors.categoryPalette.colorFor(item.categoryColorKey)
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color = color.copy(alpha = 0.16f), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = paymentMethodIcon(item.paymentMethod),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = color,
        )
    }
}
