package com.eec.app.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eec.app.AppInfo
import com.eec.app.ThemeMode
import com.eec.app.ui.MaxWidthContent
import com.eec.design.component.CategoryChip
import com.eec.design.component.SecondaryButton
import com.eec.design.component.SectionHeader
import com.eec.design.theme.EecSizes
import com.eec.design.theme.EecTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    theme: ThemeMode,
    canShareFiles: Boolean,
    onThemeChange: (ThemeMode) -> Unit,
    onOpenCategories: () -> Unit,
    onEvent: (SettingsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.message) {
        val message = state.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onEvent(SettingsEvent.MessageShown)
    }

    MaxWidthContent(modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = EecTheme.spacing.screen,
                end = EecTheme.spacing.screen,
                top = EecTheme.spacing.xs,
                bottom = EecTheme.spacing.xxl,
            ),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.sm),
        ) {
            item { SectionHeader(title = "المظهر") }
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
                    verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
                ) {
                    ThemeMode.entries.forEach { mode ->
                        CategoryChip(
                            label = themeLabel(mode),
                            color = EecTheme.colors.primary,
                            selected = theme == mode,
                            onClick = { onThemeChange(mode) },
                        )
                    }
                }
            }
            item { SectionHeader(title = "القفل") }
            item { LockSection(state = state, onEvent = onEvent) }
            item { SectionHeader(title = "التصنيفات") }
            item { CategoriesCard(onOpenCategories = onOpenCategories) }
            item { SectionHeader(title = "البيانات") }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                    Text(
                        text = "تصدير كل المعاملات إلى ملف CSV بترميز UTF-8.",
                        style = EecTheme.typography.body,
                        color = EecTheme.colors.onSurfaceVariant,
                    )
                    SecondaryButton(
                        text = if (state.exporting) "جارٍ تجهيز CSV" else "تصدير كل المعاملات",
                        onClick = { onEvent(SettingsEvent.ExportAll) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canShareFiles && !state.exporting,
                    )
                    if (!canShareFiles) {
                        Text(
                            text = "تصدير الملفات غير متاح على هذا الجهاز.",
                            style = EecTheme.typography.caption,
                            color = EecTheme.colors.onSurfaceVariant,
                        )
                    }
                }
            }
            item { SectionHeader(title = "حول") }
            item { AboutCard() }
        }
        state.lockDraft?.let { LockDialog(state = state, onEvent = onEvent) }
    }
}

@Composable
private fun CategoriesCard(onOpenCategories: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surface,
        border = BorderStroke(EecSizes.border, EecTheme.colors.outline),
    ) {
        Column(
            modifier = Modifier.padding(EecTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            Text(
                text = "أضف تصنيفاتك أو أعد تسميتها أو غيّر ألوانها.",
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
            SecondaryButton(
                text = "إدارة التصنيفات",
                onClick = onOpenCategories,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AboutCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = EecTheme.spacing.xs),
        shape = EecTheme.shapes.card,
        color = EecTheme.colors.surface,
        border = BorderStroke(1.dp, EecTheme.colors.outline),
    ) {
        Column(
            modifier = Modifier.padding(EecTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
        ) {
            Text(
                text = AppInfo.NAME,
                style = EecTheme.typography.title,
                color = EecTheme.colors.onBackground,
            )
            Text(
                text = "الإصدار ${AppInfo.VERSION} · ${AppInfo.MILESTONE}",
                style = EecTheme.typography.label,
                color = EecTheme.colors.onSurfaceVariant,
            )
            Text(
                text = "متعقب مصروفات شخصي يعمل دون شبكة، بالعربية ومن اليمين إلى اليسار.",
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
        }
    }
}

private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "النظام"
    ThemeMode.LIGHT -> "فاتح"
    ThemeMode.DARK -> "داكن"
}
