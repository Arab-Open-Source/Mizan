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

@Composable
fun CategoryManagerScreen(
    state: CategoryManagerState,
    onEvent: (CategoryManagerEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(CategoryManagerEvent.MessageShown)
    }

    MaxWidthContent(modifier) {
        when {
            state.loading -> LoadingState(message = "جارٍ تحميل التصنيفات")
            state.error != null -> ErrorState(
                title = "تعذر تحميل التصنيفات",
                message = state.error,
                onRetry = { onEvent(CategoryManagerEvent.Retry) },
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
            text = "تصنيف جديد",
            onClick = { onEvent(CategoryManagerEvent.NewCategory) },
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.visible.isEmpty()) {
            EmptyState(
                title = "لا توجد تصنيفات",
                message = "أضف تصنيفًا لتنظيم معاملاتك.",
                icon = EecIcons.transactions,
                actionLabel = "تصنيف جديد",
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
                    text = if (category.archived) "مؤرشف" else transactionKindLabel(category.kind),
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
                text = "تعديل",
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
    error: String?,
    onEvent: (CategoryManagerEvent) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onEvent(CategoryManagerEvent.DismissEditor) },
        title = {
            Text(
                text = if (draft.id == null) "تصنيف جديد" else "تعديل التصنيف",
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
                    text = if (saving) "جارٍ الحفظ" else "حفظ",
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
                            text = if (draft.archived) "استعادة" else "أرشفة",
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
                        text = "إلغاء",
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
    error: String?,
    onEvent: (CategoryManagerEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm)) {
        EecTextField(
            value = draft.name,
            onValueChange = { onEvent(CategoryManagerEvent.NameChanged(it)) },
            label = "اسم التصنيف",
            isError = error != null,
            supportingText = error ?: "${draft.name.length}/${CategoryRepository.NAME_MAX_LENGTH}",
        )
        Text(
            text = "اللون",
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
        )
    }
}

@Composable
private fun ColorSwatch(
    key: CategoryColorKey,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Surface(
        onClick = onSelect,
        modifier = Modifier
            .size(EecSizes.minTouchTarget)
            .semantics {
                contentDescription = colorLabel(key)
                if (selected) stateDescription = "محدد"
            },
        shape = EecTheme.shapes.chip,
        color = EecTheme.colors.categoryPalette.colorFor(key),
        border = BorderStroke(
            width = if (selected) EecSizes.selectedBorder else EecSizes.border,
            color = if (selected) EecTheme.colors.onBackground else EecTheme.colors.outline,
        ),
    ) {}
}

private fun colorLabel(key: CategoryColorKey): String = when (key) {
    CategoryColorKey.TEAL -> "فيروزي"
    CategoryColorKey.GOLD -> "ذهبي"
    CategoryColorKey.RUST -> "صدئ"
    CategoryColorKey.GREEN -> "أخضر"
    CategoryColorKey.RED -> "أحمر"
    CategoryColorKey.SLATE -> "رمادي"
    CategoryColorKey.PLUM -> "برقوقي"
    CategoryColorKey.OLIVE -> "زيتوني"
}
