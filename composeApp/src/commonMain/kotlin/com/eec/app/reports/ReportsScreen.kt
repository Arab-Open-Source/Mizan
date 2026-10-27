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
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.action_add_transaction
import com.eec.app.resources.action_close
import com.eec.app.resources.action_export_month_csv
import com.eec.app.resources.action_preview_share
import com.eec.app.resources.action_retry
import com.eec.app.resources.average_daily_spend
import com.eec.app.resources.exporting_csv
import com.eec.app.resources.no_expenses_this_month
import com.eec.app.resources.reports_empty_message
import com.eec.app.resources.reports_empty_title
import com.eec.app.resources.reports_error_title
import com.eec.app.resources.reports_loading
import com.eec.app.resources.section_share_report
import com.eec.app.resources.section_top_categories
import com.eec.app.resources.share_copy
import com.eec.app.resources.share_email
import com.eec.app.resources.share_email_note
import com.eec.app.resources.share_export_csv
import com.eec.app.resources.share_files_unavailable
import com.eec.app.resources.share_telegram
import com.eec.app.resources.share_text
import com.eec.app.resources.share_title
import com.eec.app.resources.share_whatsapp
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
    val message = state.message?.let { appString(it) }
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(ReportsEvent.MessageShown)
    }

    MaxWidthContent(modifier) {
        when {
            state.loading -> LoadingState(message = appString(Res.string.reports_loading))
            state.error != null -> ErrorState(
                title = appString(Res.string.reports_error_title),
                message = state.error,
                onRetry = { onEvent(ReportsEvent.Retry) },
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
                    onPrevious = { onEvent(ReportsEvent.PreviousMonth) },
                    onNext = { onEvent(ReportsEvent.NextMonth) },
                )
                EmptyState(
                    title = appString(Res.string.reports_empty_title),
                    message = appString(Res.string.reports_empty_message),
                    icon = EecIcons.reports,
                    actionLabel = appString(Res.string.action_add_transaction),
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
        title = appString(Res.string.share_title),
        copyLabel = appString(Res.string.share_copy),
        shareTextLabel = appString(Res.string.share_text),
        exportCsvLabel = appString(Res.string.share_export_csv),
        closeLabel = appString(Res.string.action_close),
        whatsAppLabel = appString(Res.string.share_whatsapp),
        telegramLabel = appString(Res.string.share_telegram),
        emailLabel = appString(Res.string.share_email),
        emailNote = appString(Res.string.share_email_note),
        filesUnavailableMessage = appString(Res.string.share_files_unavailable),
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
        item { SectionHeader(title = appString(Res.string.section_top_categories)) }
        if (snapshot.topCategories.isEmpty()) {
            item {
                Text(
                    text = appString(Res.string.no_expenses_this_month),
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
        item { SectionHeader(title = appString(Res.string.section_share_report)) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                PrimaryButton(
                    text = appString(Res.string.action_preview_share),
                    onClick = { onEvent(ReportsEvent.OpenShare) },
                    modifier = Modifier.fillMaxWidth(),
                )
                SecondaryButton(
                    text = if (state.exporting) {
                        appString(Res.string.exporting_csv)
                    } else {
                        appString(Res.string.action_export_month_csv)
                    },
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
            text = appString(Res.string.average_daily_spend),
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
