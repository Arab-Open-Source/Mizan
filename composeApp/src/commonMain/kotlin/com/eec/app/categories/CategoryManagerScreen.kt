package com.eec.app.categories

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.action_archive
import com.eec.app.resources.action_cancel
import com.eec.app.resources.action_edit
import com.eec.app.resources.action_new_category
import com.eec.app.resources.action_restore
import com.eec.app.resources.action_retry
import com.eec.app.resources.action_save
import com.eec.app.resources.categories_empty_message
import com.eec.app.resources.categories_empty_title
import com.eec.app.resources.categories_error_title
import com.eec.app.resources.categories_loading
import com.eec.app.resources.category_archived
import com.eec.app.resources.category_color_label
import com.eec.app.resources.category_edit_title
import com.eec.app.resources.category_name_label
import com.eec.app.resources.category_new_title
import com.eec.app.resources.category_selected
import com.eec.app.resources.color_brightness
import com.eec.app.resources.color_gold
import com.eec.app.resources.color_green
import com.eec.app.resources.color_hex_label
import com.eec.app.resources.color_hex_support
import com.eec.app.resources.color_hue
import com.eec.app.resources.color_olive
import com.eec.app.resources.color_plum
import com.eec.app.resources.color_preview
import com.eec.app.resources.color_red
import com.eec.app.resources.color_rust
import com.eec.app.resources.color_saturation
import com.eec.app.resources.color_slate
import com.eec.app.resources.color_teal
import com.eec.app.resources.saving
import com.eec.app.ui.MaxWidthContent
import com.eec.app.ui.transactionKindLabel
import com.eec.core.model.Category
import com.eec.core.model.TransactionKind
import com.eec.core.repository.CategoryRepository
import com.eec.design.component.CategoryChip
import com.eec.design.component.ColorPicker
import com.eec.design.component.EecTextField
import com.eec.design.component.EmptyState
import com.eec.design.component.ErrorState
import com.eec.design.component.LoadingState
import com.eec.design.component.PrimaryButton
import com.eec.design.icon.EecIcons
import com.eec.design.theme.CategoryColorKey
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme
import org.jetbrains.compose.resources.StringResource

@Composable
fun CategoryManagerScreen(
    state: CategoryManagerState,
    onEvent: (CategoryManagerEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val message = state.message?.let { appString(it) }
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(CategoryManagerEvent.MessageShown)
    }

    MaxWidthContent(modifier) {
        when {
            state.loading -> LoadingState(message = appString(Res.string.categories_loading))
            state.error != null -> ErrorState(
                title = appString(Res.string.categories_error_title),
                message = state.error,
                onRetry = { onEvent(CategoryManagerEvent.Retry) },
                retryLabel = appString(Res.string.action_retry),
            )

            else -> CategoryManagerContent(state = state, onEvent = onEvent)
        }
        state.draft?.let { draft ->
            CategoryEditorDialog(
                draft = draft,
                saving = state.saving,
                error = state.editorError,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
private fun CategoryManagerContent(
    state: CategoryManagerState,
    onEvent: (CategoryManagerEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = EecTheme.spacing.screen),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
    ) {
        KindSelector(kind = state.kind, onEvent = onEvent)
        PrimaryButton(
            text = appString(Res.string.action_new_category),
            onClick = { onEvent(CategoryManagerEvent.NewCategory) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.visible.isEmpty()) {
            EmptyState(
                title = appString(Res.string.categories_empty_title),
                message = appString(Res.string.categories_empty_message),
                icon = EecIcons.transactions,
                actionLabel = appString(Res.string.action_new_category),
                onAction = { onEvent(CategoryManagerEvent.NewCategory) },
            )
        } else {
            CategoryList(categories = state.visible, onEvent = onEvent)
        }
    }
}

@Composable
private fun KindSelector(kind: TransactionKind, onEvent: (CategoryManagerEvent) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
        TransactionKind.entries.forEach { entry ->
            CategoryChip(
                label = transactionKindLabel(entry),
                color = EecTheme.colors.primary,
                selected = kind == entry,
                onClick = { onEvent(CategoryManagerEvent.KindChanged(entry)) },
            )
        }
    }
}

@Composable
private fun CategoryList(categories: List<Category>, onEvent: (CategoryManagerEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = EecTheme.spacing.xxl),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryRow(
                category = category,
                onClick = { onEvent(CategoryManagerEvent.Edit(category.id)) },
            )
        }
    }
}

@Composable
private fun CategoryRow(category: Category, onClick: () -> Unit) {
    val color = EecTheme.colors.categoryPalette.colorFor(category.colorKey)
    Surface(
        onClick = onClick,
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EecTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(EecTheme.spacing.md)
                    .background(color, EecTheme.shapes.chip),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = EecTheme.typography.title,
                    color = EecTheme.colors.onBackground,
                    maxLines = 1,
                )
                Text(
                    text = if (category.archived) {
                        appString(Res.string.category_archived)
                    } else {
                        transactionKindLabel(category.kind)
                    },
                    style = EecTheme.typography.caption,
                    color = EecTheme.colors.onSurfaceVariant,
                )
            }
            Icon(
                imageVector = EecIcons.categoryIcon(category.iconKey),
                contentDescription = null,
                modifier = Modifier.size(EecSizes.icon),
                tint = color,
            )
            Text(
                text = appString(Res.string.action_edit),
                style = EecTheme.typography.label,
                color = EecTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun CategoryEditorDialog(
    draft: CategoryDraft,
    saving: Boolean,
    error: StringResource?,
    onEvent: (CategoryManagerEvent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onEvent(CategoryManagerEvent.DismissEditor) },
        title = {
            Text(
                text = if (draft.id == null) {
                    appString(Res.string.category_new_title)
                } else {
                    appString(Res.string.category_edit_title)
                },
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
            )
        },
        text = {
            CategoryEditorFields(draft = draft, error = error, onEvent = onEvent)
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(CategoryManagerEvent.Save) },
                enabled = draft.name.isNotBlank() && !saving,
            ) {
                Text(
                    text = if (saving) appString(Res.string.saving) else appString(Res.string.action_save),
                    style = EecTheme.typography.bodyStrong,
                    color = EecTheme.colors.primary,
                )
            }
        },
        dismissButton = {
            Row {
                if (draft.id != null) {
                    TextButton(
                        onClick = {
                            onEvent(CategoryManagerEvent.SetArchived(draft.id, !draft.archived))
                        },
                    ) {
                        Text(
                            text = if (draft.archived) {
                                appString(Res.string.action_restore)
                            } else {
                                appString(Res.string.action_archive)
                            },
                            style = EecTheme.typography.bodyStrong,
                            color = if (draft.archived) {
                                EecTheme.colors.success
                            } else {
                                EecTheme.colors.danger
                            },
                        )
                    }
                }
                TextButton(onClick = { onEvent(CategoryManagerEvent.DismissEditor) }) {
                    Text(
                        text = appString(Res.string.action_cancel),
                        style = EecTheme.typography.bodyStrong,
                        color = EecTheme.colors.onSurfaceVariant,
                    )
                }
            }
        },
        shape = EecTheme.shapes.dialog,
        containerColor = EecTheme.colors.surface,
        titleContentColor = EecTheme.colors.onBackground,
        textContentColor = EecTheme.colors.onSurfaceVariant,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryEditorFields(
    draft: CategoryDraft,
    error: StringResource?,
    onEvent: (CategoryManagerEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm)) {
        EecTextField(
            value = draft.name,
            onValueChange = { onEvent(CategoryManagerEvent.NameChanged(it)) },
            label = appString(Res.string.category_name_label),
            isError = error != null,
            supportingText = error?.let { appString(it) }
                ?: "${draft.name.length}/${CategoryRepository.NAME_MAX_LENGTH}",
        )
        Text(
            text = appString(Res.string.category_color_label),
            style = EecTheme.typography.label,
            color = EecTheme.colors.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            CategoryColorKey.entries.forEach { key ->
                ColorSwatch(
                    key = key,
                    selected = key.key == draft.colorKey,
                    onSelect = { onEvent(CategoryManagerEvent.ColorChanged(key.key)) },
                )
            }
        }
        ColorPicker(
            color = EecTheme.colors.categoryPalette.colorFor(draft.colorKey),
            onColorSelected = { onEvent(CategoryManagerEvent.ColorChanged(it)) },
            hueLabel = appString(Res.string.color_hue),
            saturationLabel = appString(Res.string.color_saturation),
            brightnessLabel = appString(Res.string.color_brightness),
            hexLabel = appString(Res.string.color_hex_label),
            hexSupport = appString(Res.string.color_hex_support),
            previewLabel = appString(Res.string.color_preview),
        )
    }
}

@Composable
private fun ColorSwatch(
    key: CategoryColorKey,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val label = colorLabel(key)
    val selection = appString(Res.string.category_selected)
    Surface(
        onClick = onSelect,
        modifier = Modifier
            .size(EecSizes.minTouchTarget)
            .semantics {
                contentDescription = label
                if (selected) stateDescription = selection
            },
        shape = EecTheme.shapes.chip,
        color = EecTheme.colors.categoryPalette.colorFor(key),
        border = BorderStroke(
            width = if (selected) EecSizes.selectedBorder else EecSizes.border,
            color = if (selected) EecTheme.colors.onBackground else EecTheme.colors.outline,
        ),
    ) {}
}

@Composable
private fun colorLabel(key: CategoryColorKey): String = appString(
    when (key) {
        CategoryColorKey.TEAL -> Res.string.color_teal
        CategoryColorKey.GOLD -> Res.string.color_gold
        CategoryColorKey.RUST -> Res.string.color_rust
        CategoryColorKey.GREEN -> Res.string.color_green
        CategoryColorKey.RED -> Res.string.color_red
        CategoryColorKey.SLATE -> Res.string.color_slate
        CategoryColorKey.PLUM -> Res.string.color_plum
        CategoryColorKey.OLIVE -> Res.string.color_olive
    },
)
