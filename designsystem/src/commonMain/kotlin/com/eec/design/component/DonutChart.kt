package com.eec.design.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

data class DonutChartPoint(
    val label: String,
    val valueMinor: Long,
    val color: Color,
)

@Composable
fun DonutChart(
    points: List<DonutChartPoint>,
    totalLabel: String,
    totalDescription: String,
    modifier: Modifier = Modifier,
    diameter: Dp = 200.dp,
    strokeWidth: Dp = 24.dp,
    formatValue: (Long) -> String = { it.toString() },
) {
    if (points.isEmpty()) return
    val total = points.sumOf { it.valueMinor.coerceAtLeast(0L) }
    var selectedIndex by remember(points) { mutableIntStateOf(NO_SELECTION) }
    val selected = points.getOrNull(selectedIndex)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(diameter)
                .semantics {
                    this.contentDescription = selected?.let { "${it.label}: ${formatValue(it.valueMinor)}" }
                        ?: totalDescription
                }
                .pointerInput(points) {
                    detectTapGestures { position ->
                        val index = segmentAt(
                            position = position,
                            diameterPx = diameter.toPx(),
                            strokePx = strokeWidth.toPx(),
                            growthPx = SELECTED_GROWTH.toPx(),
                            points = points,
                            total = total,
                        )
                        if (index != NO_SELECTION) {
                            selectedIndex = if (index == selectedIndex) NO_SELECTION else index
                        }
                    }
                },
        ) {
            DonutRing(
                points = points,
                total = total,
                strokeWidth = strokeWidth,
                selectedIndex = selectedIndex,
            )
            DonutCenter(
                label = selected?.label ?: totalLabel,
                value = formatValue(selected?.valueMinor ?: total),
                supporting = selected?.let { "${sharePercent(it.valueMinor, total)}%" },
                diameter = diameter,
            )
        }
        if (selected != null) {
            DonutTooltip(
                label = selected.label,
                value = formatValue(selected.valueMinor),
                sharePercent = sharePercent(selected.valueMinor, total),
                color = selected.color,
                modifier = Modifier.padding(horizontal = EecTheme.spacing.xxs),
            )
        }
    }
}

@Composable
private fun DonutRing(
    points: List<DonutChartPoint>,
    total: Long,
    strokeWidth: Dp,
    selectedIndex: Int,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (total <= 0L) return@Canvas
        val maxStroke = strokeWidth.toPx() + SELECTED_GROWTH.toPx()
        val diameter = min(size.width, size.height)
        val topLeft = Offset(
            x = (size.width - diameter) / 2f + maxStroke / 2f,
            y = (size.height - diameter) / 2f + maxStroke / 2f,
        )
        val arcSize = Size(diameter - maxStroke, diameter - maxStroke)
        var startAngle = START_ANGLE
        points.forEachIndexed { index, point ->
            val share = point.valueMinor.coerceAtLeast(0L).toFloat() / total.toFloat()
            val sweep = share * FULL_CIRCLE
            val selected = index == selectedIndex
            drawArc(
                color = if (selectedIndex != NO_SELECTION && !selected) {
                    point.color.copy(alpha = DIMMED_ALPHA)
                } else {
                    point.color
                },
                startAngle = startAngle + SEGMENT_GAP / 2f,
                sweepAngle = (sweep - SEGMENT_GAP).coerceAtLeast(MIN_SWEEP),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(
                    width = strokeWidth.toPx() + if (selected) SELECTED_GROWTH.toPx() else 0f,
                    cap = StrokeCap.Butt,
                ),
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun DonutCenter(
    label: String,
    value: String,
    supporting: String?,
    diameter: Dp,
) {
    Column(
        modifier = Modifier
            .size(diameter)
            .padding(horizontal = EecTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style = EecTheme.typography.caption,
            color = EecTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        Text(
            text = value,
            style = EecTheme.typography.title,
            color = EecTheme.colors.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
        if (supporting != null) {
            Text(
                text = supporting,
                style = EecTheme.typography.caption,
                color = EecTheme.colors.primary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun segmentAt(
    position: Offset,
    diameterPx: Float,
    strokePx: Float,
    growthPx: Float,
    points: List<DonutChartPoint>,
    total: Long,
): Int {
    if (total <= 0L) return NO_SELECTION
    val center = Offset(diameterPx / 2f, diameterPx / 2f)
    val distance = hypot(position.x - center.x, position.y - center.y)
    val outer = diameterPx / 2f
    if (distance < outer - strokePx - growthPx || distance > outer) return NO_SELECTION
    val degrees = ((atan2(position.y - center.y, position.x - center.x) * 180.0 / PI).toFloat() -
        START_ANGLE + FULL_CIRCLE) % FULL_CIRCLE
    var cumulative = 0f
    points.forEachIndexed { index, point ->
        cumulative += point.valueMinor.coerceAtLeast(0L).toFloat() / total.toFloat() * FULL_CIRCLE
        if (degrees <= cumulative) return index
    }
    return points.lastIndex
}

private fun sharePercent(value: Long, total: Long): Int =
    if (total <= 0L) 0 else ((value * 100) / total).toInt()

private const val NO_SELECTION: Int = -1
private const val START_ANGLE: Float = -90f
private const val FULL_CIRCLE: Float = 360f
private const val SEGMENT_GAP: Float = 2f
private const val MIN_SWEEP: Float = 0.5f
private const val DIMMED_ALPHA: Float = 0.5f
private val SELECTED_GROWTH: Dp = 4.dp
