package com.eec.app.transactions

import com.eec.app.di.AppContainer
import com.eec.app.resources.Res
import com.eec.app.resources.error_invalid_data
import com.eec.app.resources.error_not_found
import com.eec.app.resources.error_storage
import com.eec.app.resources.transaction_deleted
import com.eec.core.model.Category
import com.eec.core.model.Money
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionFilter
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.repository.RepositoryError
import com.eec.core.repository.WriteResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

data class TransactionsState(
    val search: String = "",
    val kind: TransactionKind? = null,
    val categoryId: String? = null,
    val paymentMethod: PaymentMethod? = null,
    val filtersVisible: Boolean = false,
    val loading: Boolean = true,
    val error: String? = null,
    val items: List<TransactionListItem> = emptyList(),
    val categories: List<Category> = emptyList(),
    val pendingDelete: TransactionListItem? = null,
    val message: StringResource? = null,
) {
    val hasFilters: Boolean
        get() = search.isNotBlank() || kind != null || categoryId != null || paymentMethod != null

    val expenseTotal: Money
        get() = Money.total(items.filter { it.kind == TransactionKind.EXPENSE }.map { it.amount })

    val incomeTotal: Money
        get() = Money.total(items.filter { it.kind == TransactionKind.INCOME }.map { it.amount })
}

sealed interface TransactionsEvent {
    data class SearchChanged(val query: String) : TransactionsEvent

    data object ShowFilters : TransactionsEvent

    data object HideFilters : TransactionsEvent

    data class KindChanged(val kind: TransactionKind?) : TransactionsEvent

    data class CategoryChanged(val categoryId: String?) : TransactionsEvent

    data class PaymentChanged(val paymentMethod: PaymentMethod?) : TransactionsEvent

    data object ClearFilters : TransactionsEvent

    data class RequestDelete(val item: TransactionListItem) : TransactionsEvent

    data object CancelDelete : TransactionsEvent

    data object ConfirmDelete : TransactionsEvent

    data object MessageShown : TransactionsEvent

    data object Retry : TransactionsEvent
}

class TransactionsScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
) {
    private val query = MutableStateFlow(Query())

    private val _state = MutableStateFlow(TransactionsState())
    val state: StateFlow<TransactionsState> = _state.asStateFlow()

    init {
        observeTransactions()
        scope.launch {
            container.categories.all.collect { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    fun onEvent(event: TransactionsEvent) {
        when (event) {
            is TransactionsEvent.SearchChanged -> updateQuery { it.copy(search = event.query) }
            TransactionsEvent.ShowFilters -> _state.update { it.copy(filtersVisible = true) }
            TransactionsEvent.HideFilters -> _state.update { it.copy(filtersVisible = false) }
            is TransactionsEvent.KindChanged -> updateQuery { query ->
                query.copy(kind = event.kind, categoryId = null)
            }

            is TransactionsEvent.CategoryChanged -> updateQuery { it.copy(categoryId = event.categoryId) }
            is TransactionsEvent.PaymentChanged -> updateQuery { it.copy(paymentMethod = event.paymentMethod) }
            TransactionsEvent.ClearFilters -> {
                _state.update { it.copy(filtersVisible = false) }
                updateQuery { Query(retryToken = it.retryToken + 1) }
            }
            is TransactionsEvent.RequestDelete -> _state.update { it.copy(pendingDelete = event.item) }
            TransactionsEvent.CancelDelete -> _state.update { it.copy(pendingDelete = null) }
            TransactionsEvent.ConfirmDelete -> confirmDelete()
            TransactionsEvent.MessageShown -> _state.update { it.copy(message = null) }
            TransactionsEvent.Retry -> updateQuery { it.copy(retryToken = it.retryToken + 1) }
        }
    }

    private fun updateQuery(transform: (Query) -> Query) {
        val updated = transform(query.value)
        query.value = updated
        _state.update {
            it.copy(
                search = updated.search,
                kind = updated.kind,
                categoryId = updated.categoryId,
                paymentMethod = updated.paymentMethod,
                error = null,
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeTransactions() {
        scope.launch {
            query
                .debounce(SEARCH_DEBOUNCE_MILLIS)
                .flatMapLatest { current ->
                    container.transactions.observe(current.toFilter())
                        .map<List<TransactionListItem>, Observed> { Observed.Ready(it) }
                        .onStart { emit(Observed.Loading) }
                        .catch { emit(Observed.Failed(it.message ?: it.toString())) }
                }
                .collect { observed ->
                    _state.update { current ->
                        when (observed) {
                            Observed.Loading -> current.copy(loading = true, error = null)
                            is Observed.Ready -> current.copy(
                                loading = false,
                                error = null,
                                items = observed.items,
                            )

                            is Observed.Failed -> current.copy(loading = false, error = observed.message)
                        }
                    }
                }
        }
    }

    private fun confirmDelete() {
        val target = _state.value.pendingDelete ?: return
        _state.value = _state.value.copy(pendingDelete = null)
        scope.launch {
            try {
                when (val result = container.transactions.delete(target.id)) {
                    is WriteResult.Success -> _state.update {
                        it.copy(message = Res.string.transaction_deleted)
                    }

                    is WriteResult.Failure -> _state.update {
                        it.copy(
                            message = when (result.error) {
                                is RepositoryError.Validation -> Res.string.error_invalid_data
                                is RepositoryError.NotFound -> Res.string.error_not_found
                                is RepositoryError.Storage -> Res.string.error_storage
                            },
                        )
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                _state.update { it.copy(message = Res.string.error_storage) }
            }
        }
    }

    private data class Query(
        val search: String = "",
        val kind: TransactionKind? = null,
        val categoryId: String? = null,
        val paymentMethod: PaymentMethod? = null,
        val retryToken: Int = 0,
    ) {
        fun toFilter(): TransactionFilter = TransactionFilter(
            kind = kind,
            categoryId = categoryId,
            paymentMethod = paymentMethod,
            noteQuery = search,
        )
    }

    private sealed interface Observed {
        data object Loading : Observed

        data class Ready(val items: List<TransactionListItem>) : Observed

        data class Failed(val message: String) : Observed
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS: Long = 150L
    }
}
