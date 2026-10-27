package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    accent: Color = EecTheme.colors.primary,
    containerColor: Color = EecTheme.colors.surface,
    valueColor: Color = EecTheme.colors.onBackground,
    supportingColor: Color = EecTheme.colors.onSurfaceVariant,
) {
    Surface(
        modifier = modifier,
        shape = EecTheme.shapes.card,
        color = containerColor,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
    ) {
        Column(
            modifier = Modifier.padding(EecTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            Box(
                modifier = Modifier
                    .width(EecTheme.spacing.xl)
                    .height(EecTheme.spacing.xxs)
                    .background(accent, EecTheme.shapes.chip),
            )
            Text(
                text = label,
                style = EecTheme.typography.label,
                color = EecTheme.colors.onSurfaceVariant,
            )
            Text(
                text = value,
                style = EecTheme.typography.headline,
                color = valueColor,
                maxLines = 1,
            )
            if (supporting != null) {
                Text(
                    text = supporting,
                    modifier = Modifier.fillMaxWidth(),
                    style = EecTheme.typography.caption,
                    color = supportingColor,
                    maxLines = 1,
                )
            }
        }
    }
}
