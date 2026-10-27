package com.eec.design.component

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eec.design.theme.EecTheme

@Composable
fun EecSnackbar(
    data: SnackbarData,
    modifier: Modifier = Modifier,
) {
    Snackbar(
        snackbarData = data,
        modifier = modifier,
        shape = EecTheme.shapes.card,
        containerColor = EecTheme.colors.surfaceVariant,
        contentColor = EecTheme.colors.onBackground,
        actionColor = EecTheme.colors.primary,
        dismissActionContentColor = EecTheme.colors.onSurfaceVariant,
    )
}
