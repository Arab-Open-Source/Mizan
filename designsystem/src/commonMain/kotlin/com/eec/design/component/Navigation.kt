package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

data class NavigationItem(
    val key: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun BottomBar(
    items: List<NavigationItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = EecTheme.spacing.md, vertical = EecTheme.spacing.xs),
        shape = EecTheme.shapes.chip,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(EecTheme.spacing.xxs),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xxs),
        ) {
            items.forEach { item ->
                NavigationPill(
                    item = item,
                    selected = item.key == selectedKey,
                    onSelect = onSelect,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun NavigationRail(
    items: List<NavigationItem>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.padding(EecTheme.spacing.xs),
        shape = EecTheme.shapes.sheet,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
        shadowElevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(EecTheme.spacing.xxs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xxs),
        ) {
            header?.invoke(this)
            items.forEach { item ->
                NavigationPill(
                    item = item,
                    selected = item.key == selectedKey,
                    onSelect = onSelect,
                    modifier = Modifier.widthIn(min = 76.dp),
                )
            }
        }
    }
}

@Composable
private fun NavigationPill(
    item: NavigationItem,
    selected: Boolean,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (selected) EecTheme.colors.onPrimary else EecTheme.colors.onSurfaceVariant
    Surface(
        modifier = modifier.heightIn(min = EecSizes.minTouchTarget),
        onClick = { onSelect(item.key) },
        shape = EecTheme.shapes.chip,
        color = if (selected) EecTheme.colors.primary else Color.Transparent,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = EecTheme.spacing.xxs, vertical = EecTheme.spacing.xxs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp),
                tint = contentColor,
            )
            Text(
                text = item.label,
                style = EecTheme.typography.caption,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
