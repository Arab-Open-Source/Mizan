package com.eec.app.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.eec.design.component.EecTextField
import com.eec.design.component.PrimaryButton
import com.eec.design.component.SecondaryButton
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
internal fun LockSection(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
    ) {
        Column(
            modifier = Modifier.padding(EecTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            Text(
                text = if (state.lockEnabled) {
                    "قفل التطبيق مفعّل. يُطلب الرمز في كل مرة يُفتح فيها التطبيق."
                } else {
                    "قفل التطبيق متوقف. فعّله لحماية بياناتك برمز من 4 إلى 6 أرقام."
                },
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
            if (state.lockEnabled) {
                PrimaryButton(
                    text = "تغيير رمز القفل",
                    onClick = { onEvent(SettingsEvent.OpenLockDialog(LockMode.CHANGE)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.lockLoaded && !state.lockSaving,
                )
                SecondaryButton(
                    text = "إيقاف قفل التطبيق",
                    onClick = { onEvent(SettingsEvent.OpenLockDialog(LockMode.DISABLE)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.lockLoaded && !state.lockSaving,
                )
            } else {
                PrimaryButton(
                    text = "تفعيل قفل التطبيق",
                    onClick = { onEvent(SettingsEvent.OpenLockDialog(LockMode.ENABLE)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.lockLoaded && !state.lockSaving,
                )
            }
        }
    }
}

@Composable
internal fun LockDialog(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit,
) {
    val draft = state.lockDraft ?: return
    AlertDialog(
        onDismissRequest = { onEvent(SettingsEvent.DismissLockDialog) },
        title = {
            Text(
                text = lockTitle(draft.mode),
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                if (draft.mode != LockMode.ENABLE) {
                    PinField(
                        value = draft.currentPin,
                        label = "رمز القفل الحالي",
                        onValueChange = { onEvent(SettingsEvent.CurrentPinChanged(it)) },
                    )
                }
                if (draft.mode != LockMode.DISABLE) {
                    PinField(
                        value = draft.newPin,
                        label = "رمز القفل الجديد",
                        onValueChange = { onEvent(SettingsEvent.NewPinChanged(it)) },
                    )
                    PinField(
                        value = draft.confirmPin,
                        label = "تأكيد رمز القفل",
                        onValueChange = { onEvent(SettingsEvent.ConfirmPinChanged(it)) },
                    )
                }
                draft.error?.let { message ->
                    Text(
                        text = message,
                        style = EecTheme.typography.caption,
                        color = EecTheme.colors.danger,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onEvent(SettingsEvent.SaveLock) },
                enabled = state.canSaveLock,
            ) {
                Text(
                    text = if (state.lockSaving) "جارٍ الحفظ" else lockConfirmLabel(draft.mode),
                    style = EecTheme.typography.bodyStrong,
                    color = EecTheme.colors.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(SettingsEvent.DismissLockDialog) }) {
                Text(
                    text = "إلغاء",
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

@Composable
private fun PinField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
) {
    EecTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        visualTransformation = PasswordVisualTransformation(),
    )
}

private fun lockTitle(mode: LockMode): String = when (mode) {
    LockMode.ENABLE -> "تفعيل قفل التطبيق"
    LockMode.CHANGE -> "تغيير رمز القفل"
    LockMode.DISABLE -> "إيقاف قفل التطبيق"
}

private fun lockConfirmLabel(mode: LockMode): String = when (mode) {
    LockMode.ENABLE -> "تفعيل"
    LockMode.CHANGE -> "حفظ"
    LockMode.DISABLE -> "إيقاف"
}
