package com.eec.design.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eec.design.theme.EecSpacing
import com.eec.design.theme.EecTheme

@Composable
fun DesignSystemShowcase(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(EecSpacing.md),
        verticalArrangement = Arrangement.spacedBy(EecSpacing.md),
    ) {
        Text(
            text = "Mizan design system",
            style = EecTheme.typography.pageTitle,
            color = EecTheme.colors.onBackground,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(EecSpacing.xs)) {
            listOf(
                EecTheme.colors.primary,
                EecTheme.colors.secondary,
                EecTheme.colors.tertiary,
                EecTheme.colors.success,
                EecTheme.colors.warning,
                EecTheme.colors.danger,
                EecTheme.colors.info,
            ).forEach { color ->
                Box(
                    modifier = Modifier
                        .width(EecSpacing.lg)
                        .height(EecSpacing.lg)
                        .background(color),
                )
            }
        }
        Text(
            text = "123,456.78",
            style = EecTheme.typography.primaryBalance,
            color = EecTheme.colors.onBackground,
        )
    }
}