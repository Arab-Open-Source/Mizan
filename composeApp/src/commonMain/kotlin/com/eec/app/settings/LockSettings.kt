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
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.action_cancel
import com.eec.app.resources.action_save
import com.eec.app.resources.lock_action_disable
import com.eec.app.resources.lock_action_enable
import com.eec.app.resources.lock_change
import com.eec.app.resources.lock_confirm_pin
import com.eec.app.resources.lock_current_pin
import com.eec.app.resources.lock_disable
import com.eec.app.resources.lock_disabled_hint
import com.eec.app.resources.lock_enable
import com.eec.app.resources.lock_enabled_hint
import com.eec.app.resources.lock_new_pin
import com.eec.app.resources.saving
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
                    appString(Res.string.lock_enabled_hint)
                } else {
                    appString(Res.string.lock_disabled_hint)
                },
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
            if (state.lockEnabled) {
                PrimaryButton(
                    text = appString(Res.string.lock_change),
                    onClick = { onEvent(SettingsEvent.OpenLockDialog(LockMode.CHANGE)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.lockLoaded && !state.lockSaving,
                )
                SecondaryButton(
                    text = appString(Res.string.lock_disable),
                    onClick = { onEvent(SettingsEvent.OpenLockDialog(LockMode.DISABLE)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.lockLoaded && !state.lockSaving,
                )
            } else {
                PrimaryButton(
                    text = appString(Res.string.lock_enable),
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
                        label = appString(Res.string.lock_current_pin),
                        onValueChange = { onEvent(SettingsEvent.CurrentPinChanged(it)) },
                    )
                }
                if (draft.mode != LockMode.DISABLE) {
                    PinField(
                        value = draft.newPin,
                        label = appString(Res.string.lock_new_pin),
                        onValueChange = { onEvent(SettingsEvent.NewPinChanged(it)) },
                    )
                    PinField(
                        value = draft.confirmPin,
                        label = appString(Res.string.lock_confirm_pin),
                        onValueChange = { onEvent(SettingsEvent.ConfirmPinChanged(it)) },
                    )
                }
                draft.error?.let { message ->
                    Text(
                        text = appString(message),
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
                    text = if (state.lockSaving) appString(Res.string.saving) else lockConfirmLabel(draft.mode),
                    style = EecTheme.typography.bodyStrong,
                    color = EecTheme.colors.primary,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(SettingsEvent.DismissLockDialog) }) {
                Text(
                    text = appString(Res.string.action_cancel),
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

@Composable
private fun lockTitle(mode: LockMode): String = appString(
    when (mode) {
        LockMode.ENABLE -> Res.string.lock_enable
        LockMode.CHANGE -> Res.string.lock_change
        LockMode.DISABLE -> Res.string.lock_disable
    },
)

@Composable
private fun lockConfirmLabel(mode: LockMode): String = appString(
    when (mode) {
        LockMode.ENABLE -> Res.string.lock_action_enable
        LockMode.CHANGE -> Res.string.action_save
        LockMode.DISABLE -> Res.string.lock_action_disable
    },
)
