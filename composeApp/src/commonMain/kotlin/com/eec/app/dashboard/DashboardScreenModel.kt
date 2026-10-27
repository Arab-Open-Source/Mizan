package com.eec.app.dashboard

import com.eec.app.di.AppContainer
import com.eec.core.model.MonthPeriod
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.report.MonthlyReportBuilder
import com.eec.core.report.ReportSnapshot
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

data class DashboardState(
    val period: MonthPeriod,
    val loading: Boolean = true,
    val error: String? = null,
    val snapshot: ReportSnapshot? = null,
    val recent: List<TransactionListItem> = emptyList(),
) {
    val isEmpty: Boolean
        get() = snapshot != null &&
            recent.isEmpty() &&
            snapshot.totals.expense.isZero &&
            snapshot.totals.income.isZero
}

sealed interface DashboardEvent {
    data object PreviousMonth : DashboardEvent

    data object NextMonth : DashboardEvent

    data object Retry : DashboardEvent
}

class DashboardScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
) {
    private val period = MutableStateFlow(MonthPeriod.from(container.clock.now().toCairoDate()))
    private val retryToken = MutableStateFlow(0)

    private val _state = MutableStateFlow(DashboardState(period = period.value))
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        scope.launch {
            combine(period, retryToken, container.transactions.all) { selected, _, items ->
                selected to items
            }
                .collectLatest { (selected, items) -> load(selected, items) }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.PreviousMonth -> select(period.value.previous())
            DashboardEvent.NextMonth -> select(period.value.next())
            DashboardEvent.Retry -> {
                _state.update { it.copy(loading = true, error = null) }
                retryToken.value += 1
            }
        }
    }

    private fun select(selected: MonthPeriod) {
        period.value = selected
        _state.value = _state.value.copy(period = selected, loading = true, error = null)
    }

    private suspend fun load(selected: MonthPeriod, items: List<TransactionListItem>) {
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
            val start = selected.startInstant()
            val end = selected.endExclusiveInstant()
            val recent = items
                .filter { it.occurredAt >= start && it.occurredAt < end }
                .take(RECENT_LIMIT)
            _state.value = DashboardState(
                period = selected,
                loading = false,
                snapshot = snapshot,
                recent = recent,
            )
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            _state.value = _state.value.copy(
                period = selected,
                loading = false,
                error = error.message ?: error.toString(),
            )
        }
    }

    private companion object {
        const val RECENT_LIMIT: Int = 5
    }
}
