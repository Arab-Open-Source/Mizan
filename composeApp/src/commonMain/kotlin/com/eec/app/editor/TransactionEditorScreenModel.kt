package com.eec.app.editor

import com.eec.app.di.AppContainer
import com.eec.app.ui.toUserMessage
import com.eec.core.format.AmountFormatter
import com.eec.core.model.Money
import com.eec.core.model.NewTransaction
import com.eec.core.model.PaymentMethod
import com.eec.core.model.TransactionKind
import com.eec.core.model.TransactionListItem
import com.eec.core.repository.SettingKey
import com.eec.core.repository.WriteResult
import com.eec.core.time.atCairoStart
import com.eec.core.time.toCairoDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

const val NOTE_MAX_LENGTH: Int = 280

sealed interface EditorTarget {
    data object Add : EditorTarget

    data class Edit(val item: TransactionListItem) : EditorTarget
}

data class TransactionEditorState(
    val target: EditorTarget,
    val kind: TransactionKind,
    val amountText: String = "",
    val categoryId: String? = null,
    val date: LocalDate,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val note: String = "",
    val categories: List<com.eec.core.model.Category> = emptyList(),
    val loading: Boolean = true,
    val saving: Boolean = false,
    val error: String? = null,
) {
    val isEditing: Boolean get() = target is EditorTarget.Edit

    val amountMinor: Long? get() = Money.parse(amountText)?.minor

    val amountValid: Boolean get() = (amountMinor ?: 0L) > 0L

    val noteValid: Boolean get() = note.length <= NOTE_MAX_LENGTH

    val canSave: Boolean get() = amountValid && categoryId != null && noteValid && !saving
}

sealed interface TransactionEditorEvent {
    data class AmountChanged(val value: String) : TransactionEditorEvent

    data class KeyPressed(val key: String) : TransactionEditorEvent

    data object Backspace : TransactionEditorEvent

    data class KindChanged(val kind: TransactionKind) : TransactionEditorEvent

    data class CategoryChanged(val categoryId: String) : TransactionEditorEvent

    data class DateChanged(val date: LocalDate) : TransactionEditorEvent

    data class PaymentChanged(val paymentMethod: PaymentMethod) : TransactionEditorEvent

    data class NoteChanged(val note: String) : TransactionEditorEvent

    data object Save : TransactionEditorEvent

    data object Retry : TransactionEditorEvent
}

class TransactionEditorScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
    private val target: EditorTarget,
    private val onSaved: () -> Unit,
) {
    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<TransactionEditorState> = _state.asStateFlow()

    init {
        load()
    }

    fun onEvent(event: TransactionEditorEvent) {
        when (event) {
            is TransactionEditorEvent.AmountChanged -> _state.update {
                it.copy(amountText = event.value, error = null)
            }

            is TransactionEditorEvent.KeyPressed -> _state.update {
                it.copy(amountText = appendKey(it.amountText, event.key), error = null)
            }

            TransactionEditorEvent.Backspace -> _state.update {
                it.copy(amountText = it.amountText.dropLast(1), error = null)
            }

            is TransactionEditorEvent.KindChanged -> changeKind(event.kind)
            is TransactionEditorEvent.CategoryChanged -> _state.update {
                it.copy(categoryId = event.categoryId, error = null)
            }

            is TransactionEditorEvent.DateChanged -> _state.update { it.copy(date = event.date) }
            is TransactionEditorEvent.PaymentChanged -> _state.update {
                it.copy(paymentMethod = event.paymentMethod)
            }

            is TransactionEditorEvent.NoteChanged -> _state.update {
                it.copy(note = event.note, error = null)
            }

            TransactionEditorEvent.Save -> save()
            TransactionEditorEvent.Retry -> load()
        }
    }

    private fun initialState(): TransactionEditorState {
        val edited = (target as? EditorTarget.Edit)?.item
        return TransactionEditorState(
            target = target,
            kind = edited?.kind ?: TransactionKind.EXPENSE,
            amountText = edited?.let { AmountFormatter.formatCsv(it.amount.minor) }.orEmpty(),
            categoryId = edited?.categoryId,
            date = edited?.occurredAt?.toCairoDate() ?: container.clock.now().toCairoDate(),
            paymentMethod = edited?.paymentMethod ?: PaymentMethod.CASH,
            note = edited?.note.orEmpty(),
        )
    }

    private fun load() {
        val current = _state.value
        val kind = current.kind
        scope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val categories = container.categories.allOnce()
                val visible = categories.filter { category ->
                    category.kind == kind && (!category.archived || category.id == current.categoryId)
                }
                val payment = if (target is EditorTarget.Edit) {
                    current.paymentMethod
                } else {
                    loadDefaultPayment() ?: current.paymentMethod
                }
                _state.update {
                    if (it.kind != kind) {
                        it
                    } else {
                        it.copy(
                            categories = visible,
                            paymentMethod = payment,
                            loading = false,
                        )
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update {
                    it.copy(loading = false, error = error.message ?: error.toString())
                }
            }
        }
    }

    private suspend fun loadDefaultPayment(): PaymentMethod? {
        val stored = container.settings.get(SettingKey.DEFAULT_PAYMENT_METHOD) ?: return null
        return try {
            PaymentMethod.fromStorage(stored)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun changeKind(kind: TransactionKind) {
        if (_state.value.kind == kind) return
        _state.update {
            it.copy(kind = kind, categoryId = null, categories = emptyList(), error = null)
        }
        scope.launch {
            try {
                val categories = container.categories.allOnce().filter { it.kind == kind && !it.archived }
                _state.update { current ->
                    if (current.kind == kind) current.copy(categories = categories) else current
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update { it.copy(error = error.message ?: error.toString()) }
            }
        }
    }

    private fun save() {
        val current = _state.value
        val amount = current.amountMinor ?: return
        val categoryId = current.categoryId ?: return
        if (!current.canSave) return
        val draft = NewTransaction(
            kind = current.kind,
            amount = Money.ofMinor(amount),
            categoryId = categoryId,
            occurredAt = current.date.atCairoStart(),
            note = current.note.trim().ifEmpty { null },
            paymentMethod = current.paymentMethod,
        )
        _state.update { it.copy(saving = true, error = null) }
        scope.launch {
            try {
                val result = when (val target = current.target) {
                    is EditorTarget.Add -> container.transactions.add(draft)
                    is EditorTarget.Edit -> container.transactions.update(target.item.id, draft)
                }
                when (result) {
                    is WriteResult.Success -> {
                        _state.update { it.copy(saving = false) }
                        onSaved()
                    }

                    is WriteResult.Failure -> _state.update {
                        it.copy(saving = false, error = result.error.toUserMessage())
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                _state.update { it.copy(saving = false, error = error.message ?: error.toString()) }
            }
        }
    }

    private fun appendKey(current: String, key: String): String {
        if (key == KEY_DECIMAL) {
            if (current.contains('.')) return current
            return if (current.isEmpty()) "0." else current + "."
        }
        val digitCount = current.count { it.isDigit() }
        if (digitCount >= MAX_AMOUNT_DIGITS) return current
        val dotIndex = current.indexOf('.')
        if (dotIndex >= 0 && current.length - dotIndex - 1 >= 2) return current
        return when {
            current == "0" -> key
            current.isEmpty() && key == "0" -> "0"
            else -> current + key
        }
    }

    private companion object {
        const val KEY_DECIMAL: String = "."
        const val MAX_AMOUNT_DIGITS: Int = 9
    }
}
