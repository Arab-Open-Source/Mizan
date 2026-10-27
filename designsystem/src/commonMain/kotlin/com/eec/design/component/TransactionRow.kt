package com.eec.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = EecSizes.minTouchTarget)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(vertical = EecTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
            modifier = Modifier.width(112.dp),
            style = EecTheme.typography.bodyStrong,
        )
    }
}
