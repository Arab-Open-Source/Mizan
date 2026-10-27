package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun CategoryChip(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Surface(
        modifier = modifier.heightIn(min = EecSizes.minTouchTarget),
        onClick = onClick,
        shape = EecTheme.shapes.chip,
        color = if (selected) color.copy(alpha = 0.20f) else EecTheme.colors.surface,
        border = BorderStroke(
            width = if (selected) EecSizes.selectedBorder else EecSizes.border,
            color = if (selected) color else EecTheme.colors.outline,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = EecTheme.spacing.sm, vertical = EecTheme.spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = color,
                )
            }
            Text(
                text = label,
                style = EecTheme.typography.label,
                color = EecTheme.colors.onBackground,
                maxLines = 1,
            )
        }
    }
}

data class CategoryGridItem(
    val id: String,
    val name: String,
    val color: Color,
    val icon: ImageVector,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryGrid(
    items: List<CategoryGridItem>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        maxItemsInEachRow = 4,
    ) {
        items.forEach { item ->
            val selected = item.id == selectedId
            Surface(
                modifier = Modifier.width(76.dp),
                onClick = { onSelect(item.id) },
                shape = EecTheme.shapes.card,
                color = if (selected) item.color.copy(alpha = 0.20f) else EecTheme.colors.surface,
                border = BorderStroke(
                    width = if (selected) EecSizes.selectedBorder else EecSizes.border,
                    color = if (selected) item.color else EecTheme.colors.outline,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = EecTheme.spacing.sm)
                        .heightIn(min = EecSizes.minTouchTarget),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xxs),
                ) {
                    Surface(
                        shape = EecTheme.shapes.chip,
                        color = item.color.copy(alpha = 0.16f),
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp),
                            tint = item.color,
                        )
                    }
                    Text(
                        text = item.name,
                        style = EecTheme.typography.caption,
                        color = EecTheme.colors.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
