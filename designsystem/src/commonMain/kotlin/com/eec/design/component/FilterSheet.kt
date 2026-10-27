package com.eec.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (!visible) return
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = EecTheme.shapes.sheet,
        containerColor = EecTheme.colors.surface,
        contentColor = EecTheme.colors.onBackground,
        tonalElevation = 3.dp,
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = EecTheme.spacing.md)
                .padding(bottom = EecTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
        ) {
            Text(
                text = title,
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
            )
            content()
        }
    }
}
