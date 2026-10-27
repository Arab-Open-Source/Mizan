package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun EecTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector? = null,
    leadingIconDescription: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = EecSizes.minTouchTarget),
        enabled = enabled,
        singleLine = singleLine,
        isError = isError,
        label = { Text(text = label, style = EecTheme.typography.label) },
        placeholder = placeholder?.let { text ->
            { Text(text = text, style = EecTheme.typography.body, color = EecTheme.colors.onSurfaceVariant) }
        },
        supportingText = supportingText?.let { text ->
            { Text(text = text, style = EecTheme.typography.caption) }
        },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = leadingIconDescription,
                    tint = EecTheme.colors.onSurfaceVariant,
                )
            }
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        textStyle = EecTheme.typography.body,
        shape = EecTheme.shapes.input,
        colors = fieldColors(),
    )
}

@Composable
fun AmountField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "المبلغ",
    currencyLabel: String = "ج.م",
    isError: Boolean = false,
    supportingText: String? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp),
        singleLine = true,
        isError = isError,
        label = { Text(text = label, style = EecTheme.typography.label) },
        supportingText = supportingText?.let { text ->
            { Text(text = text, style = EecTheme.typography.caption) }
        },
        trailingIcon = {
            Text(
                text = currencyLabel,
                style = EecTheme.typography.bodyStrong,
                color = EecTheme.colors.onSurfaceVariant,
            )
        },
        textStyle = EecTheme.typography.display,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = EecTheme.shapes.input,
        colors = fieldColors(),
    )
}

@Composable
fun DateField(
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "التاريخ",
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = EecSizes.minTouchTarget),
        onClick = onClick,
        shape = EecTheme.shapes.input,
        color = EecTheme.colors.surface,
        border = BorderStroke(1.dp, EecTheme.colors.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = EecTheme.spacing.sm, vertical = EecTheme.spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = EecIcons.calendar,
                contentDescription = null,
                modifier = Modifier.size(EecSizes.icon),
                tint = EecTheme.colors.onSurfaceVariant,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = EecTheme.typography.caption,
                    color = EecTheme.colors.onSurfaceVariant,
                )
                Text(
                    text = value,
                    style = EecTheme.typography.body,
                    color = EecTheme.colors.onBackground,
                )
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EecTheme.colors.primary,
    unfocusedBorderColor = EecTheme.colors.outline,
    focusedLabelColor = EecTheme.colors.primary,
    unfocusedLabelColor = EecTheme.colors.onSurfaceVariant,
    cursorColor = EecTheme.colors.primary,
    focusedTextColor = EecTheme.colors.onBackground,
    unfocusedTextColor = EecTheme.colors.onBackground,
    errorBorderColor = EecTheme.colors.danger,
    errorLabelColor = EecTheme.colors.danger,
    focusedContainerColor = EecTheme.colors.surface,
    unfocusedContainerColor = EecTheme.colors.surface,
    errorContainerColor = EecTheme.colors.surface,
)
