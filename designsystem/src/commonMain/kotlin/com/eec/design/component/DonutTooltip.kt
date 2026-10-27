package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
internal fun ChartBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = EecTheme.shapes.input,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
        shadowElevation = 2.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = EecTheme.spacing.xs, vertical = EecTheme.spacing.xxs),
            style = EecTheme.typography.caption,
            color = EecTheme.colors.onBackground,
            maxLines = 1,
        )
    }
}

@Composable
fun DonutTooltip(
    label: String,
    value: String,
    sharePercent: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = EecTheme.shapes.input,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = EecTheme.spacing.sm, vertical = EecTheme.spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape),
            )
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = EecTheme.typography.bodyStrong,
                color = EecTheme.colors.onBackground,
                maxLines = 1,
            )
            Text(
                text = "$value · $sharePercent%",
                style = EecTheme.typography.label,
                color = EecTheme.colors.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}
