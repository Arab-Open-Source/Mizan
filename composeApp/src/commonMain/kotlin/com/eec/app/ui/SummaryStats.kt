package com.eec.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eec.core.format.AmountFormatter
import com.eec.core.model.Money
import com.eec.core.report.ReportSnapshot
import com.eec.design.component.StatCard
import com.eec.design.theme.EecTheme

@Composable
fun SummaryStats(
    snapshot: ReportSnapshot,
    modifier: Modifier = Modifier,
    showDelta: Boolean = true,
) {
    val cards = listOf(
        StatCardData(
            label = "المصروف",
            value = AmountFormatter.format(snapshot.totals.expense.minor),
            supporting = if (showDelta) deltaLabel(snapshot.expenseDelta) else null,
            accent = EecTheme.colors.danger,
            containerColor = EecTheme.colors.dangerContainer,
        ),
        StatCardData(
            label = "الدخل",
            value = AmountFormatter.format(snapshot.totals.income.minor),
            supporting = if (showDelta) deltaLabel(snapshot.incomeDelta) else null,
            accent = EecTheme.colors.success,
            containerColor = EecTheme.colors.successContainer,
        ),
        StatCardData(
            label = "الصافي",
            value = AmountFormatter.format(snapshot.net.minor),
            supporting = null,
            accent = EecTheme.colors.primary,
            containerColor = EecTheme.colors.primaryContainer,
        ),
    )
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 600.dp) {
            Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                cards.forEach { card -> card.Item(Modifier.fillMaxWidth()) }
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm)) {
                cards.forEach { card -> card.Item(Modifier.weight(1f)) }
            }
        }
    }
}

fun deltaLabel(delta: Money): String = when {
    delta.isZero -> "بدون تغيير عن الشهر السابق"
    delta.isPositive -> "أعلى بمقدار ${AmountFormatter.format(delta.minor)}"
    else -> "أقل بمقدار ${AmountFormatter.format(delta.absolute().minor)}"
}

@Composable
private fun StatCardData.Item(modifier: Modifier) {
    StatCard(
        label = label,
        value = value,
        supporting = supporting,
        accent = accent,
        containerColor = containerColor,
        modifier = modifier,
    )
}

private data class StatCardData(
    val label: String,
    val value: String,
    val supporting: String?,
    val accent: Color,
    val containerColor: Color,
)
