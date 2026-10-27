package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = EecSizes.minTouchTarget),
        enabled = enabled && !loading,
        shape = EecTheme.shapes.input,
        colors = ButtonDefaults.buttonColors(
            containerColor = EecTheme.colors.primary,
            contentColor = EecTheme.colors.onPrimary,
            disabledContainerColor = EecTheme.colors.surfaceVariant,
            disabledContentColor = EecTheme.colors.onSurfaceVariant,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = EecTheme.colors.onPrimary,
                )
            }
            Text(text = text, style = EecTheme.typography.bodyStrong)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = EecSizes.minTouchTarget),
        enabled = enabled,
        shape = EecTheme.shapes.input,
        border = BorderStroke(1.dp, EecTheme.colors.outline),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = EecTheme.colors.surface,
            contentColor = EecTheme.colors.onBackground,
            disabledContainerColor = EecTheme.colors.surfaceVariant,
            disabledContentColor = EecTheme.colors.onSurfaceVariant,
        ),
    ) {
        Text(text = text, style = EecTheme.typography.bodyStrong)
    }
}

@Composable
fun FullWidthPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    PrimaryButton(
        text = text,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
    )
}
