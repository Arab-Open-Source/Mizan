package com.eec.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.month_next
import com.eec.app.resources.month_previous
import com.eec.core.model.MonthPeriod
import com.eec.design.icon.EecIcons
import com.eec.design.theme.EecTheme

@Composable
fun MonthSwitcher(
    period: MonthPeriod,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                imageVector = EecIcons.back,
                contentDescription = appString(Res.string.month_previous),
                modifier = Modifier.size(24.dp),
                tint = EecTheme.colors.onBackground,
            )
        }
        Text(
            text = monthTitle(period),
            style = EecTheme.typography.headline,
            color = EecTheme.colors.onBackground,
        )
        IconButton(onClick = onNext) {
            Icon(
                imageVector = EecIcons.back,
                contentDescription = appString(Res.string.month_next),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f),
                tint = EecTheme.colors.onBackground,
            )
        }
    }
}
