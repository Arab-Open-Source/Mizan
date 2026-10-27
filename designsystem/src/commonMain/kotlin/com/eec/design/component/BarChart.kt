package com.eec.design.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecMotion
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

data class BarChartPoint(
    val label: String,
    val valueMinor: Long,
)

@Composable
fun BarChart(
    points: List<BarChartPoint>,
    totalDescription: String,
    modifier: Modifier = Modifier,
    height: Dp = EecSizes.chartHeight,
    barColor: Color = EecTheme.colors.primary,
    formatValue: (Long) -> String = { it.toString() },
) {
    if (points.isEmpty()) return
    var selectedIndex by remember(points) { mutableIntStateOf(NO_SELECTION) }
    val selected = points.getOrNull(selectedIndex)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .semantics {
                    this.contentDescription = if (selected == null) {
                        totalDescription
                    } else {
                        "${selected.label}: ${formatValue(selected.valueMinor)}"
                    }
                },
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xxs),
        ) {
            ChartPlot(
                points = points,
                selectedIndex = selectedIndex,
                barColor = barColor,
                formatValue = formatValue,
                onSelect = { index -> selectedIndex = toggle(selectedIndex, index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            AxisLabels(points = points)
        }
    }
}

@Composable
private fun ChartPlot(
    points: List<BarChartPoint>,
    selectedIndex: Int,
    barColor: Color,
    formatValue: (Long) -> String,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val maximum = points.maxOf { it.valueMinor }.coerceAtLeast(1L)
    BoxWithConstraints(modifier = modifier) {
        ChartGrid()
        Row(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(points) {
                    detectTapGestures { position ->
                        onSelect(indexAt(position.x, size.width.toFloat(), points.size))
                    }
                }
                .pointerInput(points) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        onSelect(indexAt(change.position.x, size.width.toFloat(), points.size))
                    }
                },
            horizontalArrangement = Arrangement.spacedBy(BAR_GAP),
            verticalAlignment = Alignment.Bottom,
        ) {
            points.forEachIndexed { index, point ->
                Bar(
                    point = point,
                    maximum = maximum,
                    barColor = barColor,
                    dimmed = selectedIndex != NO_SELECTION && selectedIndex != index,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
        val selected = points.getOrNull(selectedIndex)
        if (selected != null) {
            ChartBubble(
                text = "${selected.label} · ${formatValue(selected.valueMinor)}",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = bubbleOffset(maxWidth, points.size, selectedIndex)),
            )
        }
    }
}

@Composable
private fun ChartGrid() {
    val gridColor = EecTheme.colors.outline.copy(alpha = GRID_ALPHA)
    Canvas(modifier = Modifier.fillMaxSize()) {
        GRID_FRACTIONS.forEach { fraction ->
            val y = size.height * fraction
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = GRID_STROKE,
            )
        }
    }
}

@Composable
private fun Bar(
    point: BarChartPoint,
    maximum: Long,
    barColor: Color,
    dimmed: Boolean,
    modifier: Modifier = Modifier,
) {
    val fraction = if (point.valueMinor <= 0) {
        0f
    } else {
        (point.valueMinor.toFloat() / maximum.toFloat()).coerceIn(MIN_FRACTION, 1f)
    }
    val animated by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = EecMotion.standardMillis),
        label = "barHeight",
    )
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        if (point.valueMinor <= 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ZERO_HEIGHT)
                    .background(EecTheme.colors.surfaceVariant),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(animated)
                    .background(
                        color = if (dimmed) barColor.copy(alpha = DIMMED_ALPHA) else barColor,
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                    ),
            )
        }
    }
}

@Composable
private fun AxisLabels(points: List<BarChartPoint>) {
    val step = labelStepFor(points.size)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AXIS_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(BAR_GAP),
    ) {
        points.forEachIndexed { index, point ->
            if (index % step == 0) {
                Text(
                    text = point.label,
                    modifier = Modifier.weight(1f),
                    style = EecTheme.typography.caption,
                    color = EecTheme.colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

private fun indexAt(x: Float, width: Float, count: Int): Int {
    if (count == 0 || width <= 0f) return NO_SELECTION
    return ((x / width) * count).toInt().coerceIn(0, count - 1)
}

private fun toggle(current: Int, next: Int): Int = if (current == next) NO_SELECTION else next

private fun bubbleOffset(width: Dp, count: Int, index: Int): Dp {
    val center = width * ((index + 0.5f) / count)
    return (center - BUBBLE_HALF_WIDTH).coerceIn(0.dp, (width - BUBBLE_WIDTH).coerceAtLeast(0.dp))
}

private fun labelStepFor(count: Int): Int = when {
    count <= 10 -> 1
    count <= 20 -> 2
    count <= 25 -> 3
    else -> 5
}

private const val NO_SELECTION: Int = -1
private const val MIN_FRACTION: Float = 0.01f
private const val DIMMED_ALPHA: Float = 0.45f
private const val GRID_ALPHA: Float = 0.6f
private const val GRID_STROKE: Float = 1f
private val GRID_FRACTIONS: List<Float> = listOf(0.25f, 0.5f, 0.75f)
private val BAR_GAP: Dp = 2.dp
private val ZERO_HEIGHT: Dp = 2.dp
private val AXIS_HEIGHT: Dp = 18.dp
private val BUBBLE_WIDTH: Dp = 132.dp
private val BUBBLE_HALF_WIDTH: Dp = 66.dp
