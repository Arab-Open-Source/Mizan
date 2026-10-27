package com.eec.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecTheme

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EecTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm, Alignment.CenterVertically),
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = EecTheme.colors.onSurfaceVariant,
            )
        }
        Text(
            text = title,
            style = EecTheme.typography.title,
            color = EecTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = EecTheme.typography.body,
            color = EecTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null && onAction != null) {
            PrimaryButton(text = actionLabel, onClick = onAction)
        }
    }
}

@Composable
fun ErrorState(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryLabel: String = "إعادة المحاولة",
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EecTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm, Alignment.CenterVertically),
    ) {
        Text(
            text = title,
            style = EecTheme.typography.title,
            color = EecTheme.colors.onBackground,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            style = EecTheme.typography.body,
            color = EecTheme.colors.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        SecondaryButton(text = retryLabel, onClick = onRetry)
    }
}

@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(EecTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm, Alignment.CenterVertically),
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = EecTheme.colors.primary,
            strokeWidth = 2.dp,
        )
        if (message != null) {
            Text(
                text = message,
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
