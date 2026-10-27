package com.eec.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.chart_category_description
import com.eec.app.resources.chart_category_total
import com.eec.app.resources.chart_total
import com.eec.core.format.AmountFormatter
import com.eec.core.report.CategoryShare
import com.eec.design.component.DonutChart
import com.eec.design.component.DonutChartPoint
import com.eec.design.theme.EecTheme

@Composable
fun CategoryDonutChart(
    categories: List<CategoryShare>,
    modifier: Modifier = Modifier,
) {
    val points = categories.map { category ->
        DonutChartPoint(
            label = category.name,
            valueMinor = category.amount.minor,
            color = EecTheme.colors.categoryPalette.colorFor(category.colorKey),
        )
    }
    DonutChart(
        points = points,
        totalLabel = appString(Res.string.chart_total),
        totalDescription = appString(
            Res.string.chart_category_total,
            appString(Res.string.chart_category_description),
            AmountFormatter.format(categories.sumOf { it.amount.minor }),
        ),
        modifier = modifier,
        formatValue = { AmountFormatter.format(it) },
    )
}

@Composable
fun CategoryShareRow(
    category: CategoryShare,
    modifier: Modifier = Modifier,
) {
    val color = EecTheme.colors.categoryPalette.colorFor(category.colorKey)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xxs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(color = color, shape = CircleShape),
            )
            Text(
                text = category.name,
                style = EecTheme.typography.bodyStrong,
                color = EecTheme.colors.onBackground,
                modifier = Modifier.weight(1f),
                maxLines = 1,
            )
            Text(
                text = "${AmountFormatter.format(category.amount.minor)} · ${category.sharePercent}%",
                style = EecTheme.typography.label,
                color = EecTheme.colors.onSurfaceVariant,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(color = EecTheme.colors.surfaceVariant, shape = RoundedCornerShape(3.dp)),
        ) {
            if (category.sharePercent > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = category.sharePercent.coerceAtMost(100) / 100f)
                        .height(6.dp)
                        .background(color = color, shape = RoundedCornerShape(3.dp)),
                )
            }
        }
    }
}
