package com.eec.app.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.app.ui.CategoryDonutChart
import com.eec.app.ui.CategoryShareRow
import com.eec.app.ui.MaxWidthContent
import com.eec.app.ui.MonthSwitcher
import com.eec.app.ui.SummaryStats
import com.eec.app.ui.moneyText
import com.eec.core.report.ReportSnapshot
import com.eec.design.component.EmptyState
import com.eec.design.component.ErrorState
import com.eec.design.component.LoadingState
import com.eec.design.component.PrimaryButton
import com.eec.design.component.SecondaryButton
import com.eec.design.component.SectionHeader
import com.eec.design.component.ShareSheet
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecTheme

@Composable
fun ReportsScreen(
    state: ReportsState,
    canShareFiles: Boolean,
    onEvent: (ReportsEvent) -> Unit,
    onAdd: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(ReportsEvent.MessageShown)
    }

    MaxWidthContent(modifier) {
        when {
            state.loading -> LoadingState(message = "جارٍ تجهيز التقرير")
            state.error != null -> ErrorState(
                title = "تعذر تجهيز التقرير",
                message = state.error,
                onRetry = { onEvent(ReportsEvent.Retry) },
            )

            state.isEmpty -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = EecTheme.spacing.screen, vertical = EecTheme.spacing.xs),
                verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
            ) {
                MonthSwitcher(
                    period = state.period,
                    onPrevious = { onEvent(ReportsEvent.PreviousMonth) },
                    onNext = { onEvent(ReportsEvent.NextMonth) },
                )
                EmptyState(
                    title = "لا توجد بيانات لهذا الشهر",
                    message = "أضف معاملات ثم عد لرؤية الملخص.",
                    icon = EecIcons.reports,
                    actionLabel = "إضافة معاملة",
                    onAction = onAdd,
                )
            }

            else -> state.snapshot?.let { snapshot ->
                ReportsContent(
                    state = state,
                    snapshot = snapshot,
                    onEvent = onEvent,
                )
            }
        }
    }

    ShareSheet(
        visible = state.shareVisible,
        onDismiss = { onEvent(ReportsEvent.CloseShare) },
        previewText = state.shareText,
        canShareFiles = canShareFiles,
        onCopy = { onEvent(ReportsEvent.CopyText) },
        onShareText = { onEvent(ReportsEvent.ShareText) },
        onExportCsv = { onEvent(ReportsEvent.ExportCsv) },
        onWhatsApp = { onEvent(ReportsEvent.ShareWhatsApp) },
        onTelegram = { onEvent(ReportsEvent.ShareTelegram) },
        onEmail = { onEvent(ReportsEvent.ShareEmail) },
    )
}

@Composable
private fun ReportsContent(
    state: ReportsState,
    snapshot: ReportSnapshot,
    onEvent: (ReportsEvent) -> Unit,
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
                onPrevious = { onEvent(ReportsEvent.PreviousMonth) },
                onNext = { onEvent(ReportsEvent.NextMonth) },
            )
        }
        item { SummaryStats(snapshot = snapshot) }
        item { AverageRow(snapshot = snapshot) }
        item { SectionHeader(title = "أعلى التصنيفات") }
        if (snapshot.topCategories.isEmpty()) {
            item {
                Text(
                    text = "لا توجد مصروفات مسجلة هذا الشهر.",
                    style = EecTheme.typography.body,
                    color = EecTheme.colors.onSurfaceVariant,
                )
            }
        } else {
            item { CategoryDonutChart(categories = snapshot.topCategories) }
            items(snapshot.topCategories.size, key = { snapshot.topCategories[it].categoryId }) { index ->
                CategoryShareRow(category = snapshot.topCategories[index])
            }
        }
        item { SectionHeader(title = "مشاركة التقرير") }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                PrimaryButton(
                    text = "معاينة ومشاركة",
                    onClick = { onEvent(ReportsEvent.OpenShare) },
                    modifier = Modifier.fillMaxWidth(),
                )
                SecondaryButton(
                    text = if (state.exporting) "جارٍ تجهيز CSV" else "تصدير الشهر CSV",
                    onClick = { onEvent(ReportsEvent.ExportCsv) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.exporting,
                )
            }
        }
    }
}

@Composable
private fun AverageRow(snapshot: ReportSnapshot) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = EecTheme.spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "معدل المصروف اليومي",
            style = EecTheme.typography.body,
            color = EecTheme.colors.onSurfaceVariant,
        )
        Text(
            text = moneyText(snapshot.averageDailyExpense.minor),
            style = EecTheme.typography.bodyStrong,
            color = EecTheme.colors.onBackground,
        )
    }
}
