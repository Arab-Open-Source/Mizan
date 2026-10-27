package com.eec.app.editor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.action_close
import com.eec.app.resources.action_retry
import com.eec.app.resources.action_save
import com.eec.app.resources.amount_label
import com.eec.app.resources.amount_positive_error
import com.eec.app.resources.currency_label
import com.eec.app.resources.editor_error_categories
import com.eec.app.resources.editor_loading_categories
import com.eec.app.resources.editor_title_edit
import com.eec.app.resources.editor_title_new
import com.eec.app.resources.saving
import com.eec.design.component.AmountField
import com.eec.design.component.ErrorState
import com.eec.design.component.FullWidthPrimaryButton
import com.eec.design.component.LoadingState
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun TransactionEditor(
    state: TransactionEditorState,
    onEvent: (TransactionEditorEvent) -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .widthIn(max = EecSizes.editorWidth)
                .heightIn(max = EecSizes.editorMaxHeight),
            shape = EecTheme.shapes.dialog,
            color = EecTheme.colors.surface,
            border = BorderStroke(1.dp, EecTheme.colors.outline),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(EecTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
            ) {
                EditorHeader(isEditing = state.isEditing, onClose = onClose)
                when {
                    state.loading -> LoadingState(message = appString(Res.string.editor_loading_categories))
                    state.categories.isEmpty() && state.error != null -> ErrorState(
                        title = appString(Res.string.editor_error_categories),
                        message = appString(state.error),
                        onRetry = { onEvent(TransactionEditorEvent.Retry) },
                        retryLabel = appString(Res.string.action_retry),
                    )

                    else -> EditorForm(state = state, onEvent = onEvent)
                }
            }
        }
    }
}

@Composable
private fun EditorHeader(isEditing: Boolean, onClose: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isEditing) appString(Res.string.editor_title_edit) else appString(Res.string.editor_title_new),
            style = EecTheme.typography.title,
            color = EecTheme.colors.onBackground,
        )
        IconButton(onClick = onClose) {
            Icon(
                imageVector = EecIcons.close,
                contentDescription = appString(Res.string.action_close),
                tint = EecTheme.colors.onBackground,
            )
        }
    }
}

@Composable
private fun EditorForm(
    state: TransactionEditorState,
    onEvent: (TransactionEditorEvent) -> Unit,
) {
    val amountError = state.amountText.isNotEmpty() && !state.amountValid
    AmountField(
        value = state.amountText,
        onValueChange = { onEvent(TransactionEditorEvent.AmountChanged(it)) },
        label = appString(Res.string.amount_label),
        currencyLabel = appString(Res.string.currency_label),
        isError = amountError,
        supportingText = if (amountError) appString(Res.string.amount_positive_error) else null,
    )
    AmountKeypad(
        onKey = { onEvent(TransactionEditorEvent.KeyPressed(it)) },
        onBackspace = { onEvent(TransactionEditorEvent.Backspace) },
    )
    KindToggle(
        kind = state.kind,
        onKind = { onEvent(TransactionEditorEvent.KindChanged(it)) },
    )
    CategorySection(
        categories = state.categories,
        selectedId = state.categoryId,
        onSelect = { onEvent(TransactionEditorEvent.CategoryChanged(it)) },
    )
    DetailsSection(state = state, onEvent = onEvent)
    if (state.error != null) {
        Text(
            text = appString(state.error),
            style = EecTheme.typography.caption,
            color = EecTheme.colors.danger,
        )
    }
    FullWidthPrimaryButton(
        text = if (state.saving) appString(Res.string.saving) else appString(Res.string.action_save),
        onClick = { onEvent(TransactionEditorEvent.Save) },
        enabled = state.canSave,
    )
}
