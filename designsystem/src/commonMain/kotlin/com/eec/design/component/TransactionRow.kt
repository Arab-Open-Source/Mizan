package com.eec.design.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@Composable
fun TransactionRow(
    title: String,
    subtitle: String,
    amountText: String,
    modifier: Modifier = Modifier,
    amountSign: AmountSign = AmountSign.None,
    note: String? = null,
    leading: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = EecSizes.minTouchTarget)
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = EecTheme.spacing.md, vertical = EecTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leading?.invoke()
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = EecTheme.typography.bodyStrong,
                    color = EecTheme.colors.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = EecTheme.typography.caption,
                    color = EecTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (note != null) {
                    Text(
                        text = note,
                        style = EecTheme.typography.caption,
                        color = EecTheme.colors.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            AmountText(
                text = amountText,
                sign = amountSign,
                style = EecTheme.typography.bodyStrong,
            )
        }
    }
}
