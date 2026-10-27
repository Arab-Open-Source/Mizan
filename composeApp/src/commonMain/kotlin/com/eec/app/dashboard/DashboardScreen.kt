package com.eec.app.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.app.ui.CategoryDonutChart
import com.eec.app.ui.CategoryShareRow
import com.eec.app.ui.MaxWidthContent
import com.eec.app.ui.MonthSwitcher
import com.eec.app.ui.SummaryStats
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
            state.loading -> LoadingState(message = "جارٍ تحميل الشهر")
            state.error != null -> ErrorState(
                title = "تعذر تحميل البيانات",
                message = state.error,
                onRetry = { onEvent(DashboardEvent.Retry) },
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
                    title = "لا توجد معاملات بعد",
                    message = "ابدأ بإضافة أول معاملة لهذا الشهر.",
                    icon = EecIcons.transactions,
                    actionLabel = "إضافة معاملة",
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
        item { SectionHeader(title = "المصروف اليومي") }
        item {
            BarChart(
                points = snapshot.daily.map { day -> BarChartPoint(day.date.day.toString(), day.amount.minor) },
                barColor = EecTheme.colors.tertiary,
                formatValue = { AmountFormatter.format(it) },
            )
        }
        item {
            SectionHeader(title = "أعلى التصنيفات")
        }
        if (snapshot.topCategories.isEmpty()) {
            item { MutedLine(text = "لا توجد مصروفات مسجلة هذا الشهر.") }
        } else {
            item { CategoryDonutChart(categories = snapshot.topCategories) }
            items(snapshot.topCategories, key = { it.categoryId }) { category ->
                CategoryShareRow(category = category)
            }
        }
        item {
            SectionHeader(title = "أحدث المعاملات")
        }
        if (state.recent.isEmpty()) {
            item { MutedLine(text = "لا توجد معاملات هذا الشهر.") }
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
