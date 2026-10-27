package com.eec.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    previewText: String,
    canShareFiles: Boolean,
    onCopy: () -> Unit,
    onShareText: () -> Unit,
    onExportCsv: () -> Unit,
    title: String,
    copyLabel: String,
    shareTextLabel: String,
    exportCsvLabel: String,
    closeLabel: String,
    whatsAppLabel: String,
    telegramLabel: String,
    emailLabel: String,
    emailNote: String,
    filesUnavailableMessage: String,
    modifier: Modifier = Modifier,
    onWhatsApp: (() -> Unit)? = null,
    onTelegram: (() -> Unit)? = null,
    onEmail: (() -> Unit)? = null,
) {
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = EecTheme.shapes.sheet,
        containerColor = EecTheme.colors.surface,
        contentColor = EecTheme.colors.onBackground,
        tonalElevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EecTheme.spacing.md)
                .padding(bottom = EecTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
        ) {
            Text(
                text = title,
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
            )
            SharePreview(text = previewText)
            PrimaryButton(text = copyLabel, onClick = onCopy, modifier = Modifier.fillMaxWidth())
            SecondaryButton(text = shareTextLabel, onClick = onShareText, modifier = Modifier.fillMaxWidth())
            if (onWhatsApp != null || onTelegram != null || onEmail != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
                ) {
                    if (onWhatsApp != null) {
                        SecondaryButton(
                            text = whatsAppLabel,
                            onClick = onWhatsApp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (onTelegram != null) {
                        SecondaryButton(
                            text = telegramLabel,
                            onClick = onTelegram,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    if (onEmail != null) {
                        SecondaryButton(
                            text = emailLabel,
                            onClick = onEmail,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (onEmail != null) {
                    Text(
                        text = emailNote,
                        style = EecTheme.typography.caption,
                        color = EecTheme.colors.onSurfaceVariant,
                    )
                }
            }
            SecondaryButton(
                text = exportCsvLabel,
                onClick = onExportCsv,
                modifier = Modifier.fillMaxWidth(),
                enabled = canShareFiles,
            )
            if (!canShareFiles) {
                Text(
                    text = filesUnavailableMessage,
                    style = EecTheme.typography.caption,
                    color = EecTheme.colors.onSurfaceVariant,
                )
            }
            SecondaryButton(text = closeLabel, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SharePreview(text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 180.dp),
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surfaceVariant,
    ) {
        SelectionContainer {
            Text(
                text = text,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(EecTheme.spacing.sm),
                style = EecTheme.typography.body,
                color = EecTheme.colors.onBackground,
            )
        }
    }
}
