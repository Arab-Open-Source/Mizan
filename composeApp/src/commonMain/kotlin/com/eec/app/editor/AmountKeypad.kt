package com.eec.app.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

private val keypadRows: List<List<String>> = listOf(
    listOf("1", "2", "3"),
    listOf("4", "5", "6"),
    listOf("7", "8", "9"),
    listOf(".", "0", "حذف"),
)

@Composable
fun AmountKeypad(
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
    ) {
        keypadRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
            ) {
                row.forEach { key ->
                    KeypadKey(
                        label = key,
                        onClick = { if (key == "حذف") onBackspace() else onKey(key) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = EecSizes.minTouchTarget),
        onClick = onClick,
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(vertical = EecTheme.spacing.sm),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.fillMaxWidth(),
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
}
