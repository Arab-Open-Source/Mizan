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
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.delta_down
import com.eec.app.resources.delta_none
import com.eec.app.resources.delta_up
import com.eec.app.resources.summary_expense
import com.eec.app.resources.summary_income
import com.eec.app.resources.summary_net
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
            label = appString(Res.string.summary_expense),
            value = AmountFormatter.format(snapshot.totals.expense.minor),
            supporting = if (showDelta) deltaLabel(snapshot.expenseDelta) else null,
            accent = EecTheme.colors.danger,
            containerColor = EecTheme.colors.dangerContainer,
        ),
        StatCardData(
            label = appString(Res.string.summary_income),
            value = AmountFormatter.format(snapshot.totals.income.minor),
            supporting = if (showDelta) deltaLabel(snapshot.incomeDelta) else null,
            accent = EecTheme.colors.success,
            containerColor = EecTheme.colors.successContainer,
        ),
        StatCardData(
            label = appString(Res.string.summary_net),
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

@Composable
fun deltaLabel(delta: Money): String = when {
    delta.isZero -> appString(Res.string.delta_none)
    delta.isPositive -> appString(Res.string.delta_up, AmountFormatter.format(delta.minor))
    else -> appString(Res.string.delta_down, AmountFormatter.format(delta.absolute().minor))
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
