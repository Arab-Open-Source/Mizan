package com.eec.app.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.eec.app.AppInfo
import com.eec.core.security.PinLock
import com.eec.design.component.EecTextField
import com.eec.design.component.PrimaryButton
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun LockScreen(
    error: String?,
    onUnlock: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pin by remember { mutableStateOf("") }
    var editing by remember { mutableStateOf(false) }
    val visibleError = error?.takeUnless { editing }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(EecTheme.spacing.screen),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = EecSizes.editorWidth),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
        ) {
            Text(
                text = AppInfo.NAME,
                style = EecTheme.typography.title,
                color = EecTheme.colors.primary,
            )
            Text(
                text = "قفل التطبيق",
                style = EecTheme.typography.headline,
                color = EecTheme.colors.onBackground,
            )
            Text(
                text = "أدخل رمز القفل للمتابعة.",
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
            EecTextField(
                value = pin,
                onValueChange = { raw ->
                    pin = raw.filter { it.isDigit() }.take(PinLock.MAX_LENGTH)
                    editing = true
                },
                label = "رمز القفل",
                isError = visibleError != null,
                supportingText = visibleError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
            )
            PrimaryButton(
                text = "فتح",
                onClick = {
                    onUnlock(pin)
                    pin = ""
                    editing = false
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = pin.length >= PinLock.MIN_LENGTH,
            )
        }
    }
}
