package com.eec.app.reports

import com.eec.app.di.AppContainer
import com.eec.app.share.toCsvBytes
import com.eec.core.model.MonthPeriod
import com.eec.core.model.TransactionKind
import com.eec.core.report.CsvExporter
import com.eec.core.report.MonthlyReportBuilder
import com.eec.core.report.ReportSnapshot
import com.eec.core.share.ShareLinks
import com.eec.core.share.ShareTextFormatter
import com.eec.core.time.endExclusiveInstant
import com.eec.core.time.startInstant
import com.eec.core.time.toCairoDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReportsState(
    val period: MonthPeriod,
    val loading: Boolean = true,
    val error: String? = null,
    val snapshot: ReportSnapshot? = null,
    val shareVisible: Boolean = false,
    val shareText: String = "",
    val exporting: Boolean = false,
    val message: String? = null,
) {
    val isEmpty: Boolean
        get() = snapshot != null && snapshot.totals.expense.isZero && snapshot.totals.income.isZero
}

sealed interface ReportsEvent {
    data object PreviousMonth : ReportsEvent

    data object NextMonth : ReportsEvent

    data object Retry : ReportsEvent

    data object OpenShare : ReportsEvent

    data object CloseShare : ReportsEvent

    data object CopyText : ReportsEvent

    data object ShareText : ReportsEvent

    data object ShareWhatsApp : ReportsEvent

    data object ShareTelegram : ReportsEvent

    data object ShareEmail : ReportsEvent

    data object ExportCsv : ReportsEvent

    data object MessageShown : ReportsEvent
}

class ReportsScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
) {
    private val period = MutableStateFlow(MonthPeriod.from(container.clock.now().toCairoDate()))
    private val retryToken = MutableStateFlow(0)

    private val _state = MutableStateFlow(ReportsState(period = period.value))
    val state: StateFlow<ReportsState> = _state.asStateFlow()

    init {
        scope.launch {
            combine(period, retryToken, container.transactions.all) { selected, _, _ -> selected }
                .collectLatest { selected -> load(selected) }
        }
    }

    fun onEvent(event: ReportsEvent) {
        when (event) {
            ReportsEvent.PreviousMonth -> moveTo(period.value.previous())
            ReportsEvent.NextMonth -> moveTo(period.value.next())
            ReportsEvent.Retry -> {
                _state.update { it.copy(loading = true, error = null) }
                retryToken.value += 1
            }
            ReportsEvent.OpenShare -> _state.update { it.copy(shareVisible = true) }
            ReportsEvent.CloseShare -> _state.update { it.copy(shareVisible = false) }
            ReportsEvent.CopyText -> copyText()
            ReportsEvent.ShareText -> shareText()
            ReportsEvent.ShareWhatsApp -> open(ShareLinks.whatsApp(_state.value.shareText))
            ReportsEvent.ShareTelegram -> open(ShareLinks.telegram(_state.value.shareText))
            ReportsEvent.ShareEmail -> open(
                ShareLinks.email(shareSubject(), _state.value.shareText),
            )

            ReportsEvent.ExportCsv -> exportCsv()
            ReportsEvent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    private fun moveTo(selected: MonthPeriod) {
        period.value = selected
        _state.update { it.copy(period = selected, loading = true, error = null) }
    }

    private suspend fun load(selected: MonthPeriod) {
        try {
            val totals = container.transactions.monthTotals(selected)
            val previousTotals = container.transactions.monthTotals(selected.previous())
            val daily = container.transactions.dailyExpenseTotals(selected)
            val byCategory = container.transactions.categoryTotals(selected, TransactionKind.EXPENSE)
            val snapshot = MonthlyReportBuilder.build(
                period = selected,
                totals = totals,
                previousTotals = previousTotals,
                daily = daily,
                byCategory = byCategory,
            )
            _state.update {
                it.copy(
                    period = selected,
                    loading = false,
                    error = null,
                    snapshot = snapshot,
                    shareText = ShareTextFormatter.format(snapshot),
                )
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            _state.update {
                it.copy(period = selected, loading = false, error = error.message ?: error.toString())
            }
        }
    }

    private fun shareSubject(): String {
        val snapshot = _state.value.snapshot ?: return "EEC"
        return ShareTextFormatter.subject(snapshot)
    }

    private fun copyText() {
        val text = _state.value.shareText
        if (text.isEmpty()) return
        scope.launch {
            try {
                container.shareService.copyText(text)
                _state.update { it.copy(message = "تم نسخ نص التقرير") }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update { it.copy(message = error.message ?: error.toString()) }
            }
        }
    }

    private fun shareText() {
        val state = _state.value
        if (state.shareText.isEmpty()) return
        scope.launch {
            try {
                container.shareService.shareText(state.shareText, shareSubject())
                _state.update { it.copy(message = "تم تجهيز المشاركة") }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update { it.copy(message = error.message ?: error.toString()) }
            }
        }
    }

    private fun open(url: String) {
        scope.launch {
            try {
                container.shareService.openLink(url)
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update { it.copy(message = error.message ?: error.toString()) }
            }
        }
    }

    private fun exportCsv() {
        val state = _state.value
        if (state.snapshot == null || state.exporting) return
        scope.launch {
            _state.update { it.copy(exporting = true, message = null) }
            try {
                val rows = container.transactions.exportRows(
                    startInclusive = state.period.startInstant(),
                    endExclusive = state.period.endExclusiveInstant(),
                )
                val bytes = rows.toCsvBytes()
                val fileName = CsvExporter.fileName(container.clock.now().toCairoDate())
                container.shareService.shareFile(bytes, fileName, CSV_MIME_TYPE)
                _state.update { it.copy(exporting = false, message = "تم تجهيز ملف CSV") }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update {
                    it.copy(exporting = false, message = error.message ?: error.toString())
                }
            }
        }
    }

    private companion object {
        const val CSV_MIME_TYPE: String = "text/csv"
    }
}
