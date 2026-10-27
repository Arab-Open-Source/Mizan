package com.eec.app.categories

import com.eec.app.di.AppContainer
import com.eec.app.resources.Res
import com.eec.app.resources.category_archived_message
import com.eec.app.resources.category_restored_message
import com.eec.app.resources.category_saved
import com.eec.app.ui.toUserMessage
import com.eec.core.model.Category
import com.eec.core.model.TransactionKind
import com.eec.core.repository.WriteResult
import com.eec.design.theme.CategoryColorKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource

data class CategoryDraft(
    val id: String? = null,
    val kind: TransactionKind,
    val name: String = "",
    val colorKey: String = CategoryColorKey.TEAL.key,
    val archived: Boolean = false,
)

data class CategoryManagerState(
    val kind: TransactionKind = TransactionKind.EXPENSE,
    val categories: List<Category> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val draft: CategoryDraft? = null,
    val saving: Boolean = false,
    val editorError: StringResource? = null,
    val message: StringResource? = null,
) {
    val visible: List<Category>
        get() = categories.filter { it.kind == kind }.sortedBy { it.archived }

    val canSave: Boolean
        get() = draft != null && draft.name.isNotBlank() && !saving
}

sealed interface CategoryManagerEvent {
    data class KindChanged(val kind: TransactionKind) : CategoryManagerEvent

    data object NewCategory : CategoryManagerEvent

    data class Edit(val id: String) : CategoryManagerEvent

    data object DismissEditor : CategoryManagerEvent

    data class NameChanged(val name: String) : CategoryManagerEvent

    data class ColorChanged(val colorKey: String) : CategoryManagerEvent

    data object Save : CategoryManagerEvent

    data class SetArchived(val id: String, val archived: Boolean) : CategoryManagerEvent

    data object Retry : CategoryManagerEvent

    data object MessageShown : CategoryManagerEvent
}

class CategoryManagerScreenModel(
    private val container: AppContainer,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(CategoryManagerState())
    val state: StateFlow<CategoryManagerState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        observe()
    }

    fun onEvent(event: CategoryManagerEvent) {
        when (event) {
            is CategoryManagerEvent.KindChanged -> _state.update {
                it.copy(kind = event.kind, draft = null, editorError = null)
            }

            CategoryManagerEvent.NewCategory -> newCategory()
            is CategoryManagerEvent.Edit -> edit(event.id)
            CategoryManagerEvent.DismissEditor -> _state.update {
                it.copy(draft = null, editorError = null, saving = false)
            }

            is CategoryManagerEvent.NameChanged -> updateDraft { it.copy(name = event.name) }
            is CategoryManagerEvent.ColorChanged -> updateDraft { it.copy(colorKey = event.colorKey) }
            CategoryManagerEvent.Save -> save()
            is CategoryManagerEvent.SetArchived -> setArchived(event.id, event.archived)
            CategoryManagerEvent.Retry -> observe()
            CategoryManagerEvent.MessageShown -> _state.update { it.copy(message = null) }
        }
    }

    private fun observe() {
        observeJob?.cancel()
        observeJob = scope.launch {
            _state.update { it.copy(loading = true, error = null) }
            container.categories.all
                .catch { error ->
                    _state.update {
                        it.copy(loading = false, error = error.message ?: error.toString())
                    }
                }
                .collect { categories ->
                    _state.update { it.copy(loading = false, error = null, categories = categories) }
                }
        }
    }

    private fun newCategory() {
        val current = _state.value
        _state.update {
            it.copy(
                draft = CategoryDraft(
                    kind = current.kind,
                    colorKey = nextColorKey(current.categories, current.kind),
                ),
                editorError = null,
            )
        }
    }

    private fun edit(id: String) {
        val category = _state.value.categories.firstOrNull { it.id == id } ?: return
        _state.update {
            it.copy(
                draft = CategoryDraft(
                    id = category.id,
                    kind = category.kind,
                    name = category.name,
                    colorKey = category.colorKey,
                    archived = category.archived,
                ),
                editorError = null,
            )
        }
    }

    private fun updateDraft(transform: (CategoryDraft) -> CategoryDraft) {
        _state.update { state ->
            val draft = state.draft ?: return@update state
            state.copy(draft = transform(draft), editorError = null)
        }
    }

    private fun save() {
        val draft = _state.value.draft ?: return
        if (_state.value.saving) return
        _state.update { it.copy(saving = true, editorError = null) }
        scope.launch {
            val result = if (draft.id == null) {
                container.categories.create(
                    name = draft.name,
                    kind = draft.kind,
                    colorKey = draft.colorKey,
                )
            } else {
                container.categories.update(
                    id = draft.id,
                    name = draft.name,
                    colorKey = draft.colorKey,
                )
            }
            when (result) {
                is WriteResult.Success -> _state.update {
                    it.copy(saving = false, draft = null, message = Res.string.category_saved)
                }

                is WriteResult.Failure -> _state.update {
                    it.copy(saving = false, editorError = result.error.toUserMessage())
                }
            }
        }
    }

    private fun setArchived(id: String, archived: Boolean) {
        scope.launch {
            when (val result = container.categories.setArchived(id = id, archived = archived)) {
                is WriteResult.Success -> _state.update { state ->
                    state.copy(
                        draft = state.draft?.takeUnless { it.id == id },
                        message = if (archived) {
                            Res.string.category_archived_message
                        } else {
                            Res.string.category_restored_message
                        },
                    )
                }

                is WriteResult.Failure -> _state.update {
                    it.copy(message = result.error.toUserMessage())
                }
            }
        }
    }

    private fun nextColorKey(categories: List<Category>, kind: TransactionKind): String {
        val used = categories
            .filter { it.kind == kind && !it.archived }
            .map { it.colorKey }
            .toSet()
        return CategoryColorKey.entries.firstOrNull { it.key !in used }?.key
            ?: CategoryColorKey.TEAL.key
    }
}
