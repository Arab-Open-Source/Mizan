package com.eec.design.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eec.design.theme.EecTheme

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
            )
        },
        text = {
            Text(
                text = message,
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmLabel,
                    style = EecTheme.typography.bodyStrong,
                    color = if (destructive) EecTheme.colors.danger else EecTheme.colors.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissLabel,
                    style = EecTheme.typography.bodyStrong,
                    color = EecTheme.colors.onSurfaceVariant,
                )
            }
        },
        shape = EecTheme.shapes.dialog,
        containerColor = EecTheme.colors.surface,
        titleContentColor = EecTheme.colors.onBackground,
        textContentColor = EecTheme.colors.onSurfaceVariant,
    )
}
