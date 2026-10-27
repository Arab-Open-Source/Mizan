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
import com.eec.app.localization.LanguageMode
import com.eec.app.localization.appString
import com.eec.app.resources.Res
import com.eec.app.resources.about_description
import com.eec.app.resources.about_version
import com.eec.app.resources.action_export_all
import com.eec.app.resources.action_manage_categories
import com.eec.app.resources.categories_hint
import com.eec.app.resources.export_hint
import com.eec.app.resources.export_unavailable
import com.eec.app.resources.exporting_csv
import com.eec.app.resources.language_description
import com.eec.app.resources.section_about
import com.eec.app.resources.section_categories
import com.eec.app.resources.section_data
import com.eec.app.resources.section_language
import com.eec.app.resources.section_lock
import com.eec.app.resources.section_theme
import com.eec.app.resources.theme_dark
import com.eec.app.resources.theme_light
import com.eec.app.resources.theme_system
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
    language: LanguageMode,
    canShareFiles: Boolean,
    onThemeChange: (ThemeMode) -> Unit,
    onLanguageChange: (LanguageMode) -> Unit,
    onOpenCategories: () -> Unit,
    onEvent: (SettingsEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val message = state.message?.let { appString(it) }
    LaunchedEffect(message) {
        if (message == null) return@LaunchedEffect
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
            item { SectionHeader(title = appString(Res.string.section_theme)) }
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
            item { SectionHeader(title = appString(Res.string.section_language)) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                    Text(
                        text = appString(Res.string.language_description),
                        style = EecTheme.typography.body,
                        color = EecTheme.colors.onSurfaceVariant,
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
                        verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs),
                    ) {
                        LanguageMode.entries.forEach { mode ->
                            CategoryChip(
                                label = appString(mode.label),
                                color = EecTheme.colors.primary,
                                selected = language == mode,
                                onClick = { onLanguageChange(mode) },
                            )
                        }
                    }
                }
            }
            item { SectionHeader(title = appString(Res.string.section_lock)) }
            item { LockSection(state = state, onEvent = onEvent) }
            item { SectionHeader(title = appString(Res.string.section_categories)) }
            item { CategoriesCard(onOpenCategories = onOpenCategories) }
            item { SectionHeader(title = appString(Res.string.section_data)) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(EecTheme.spacing.xs)) {
                    Text(
                        text = appString(Res.string.export_hint),
                        style = EecTheme.typography.body,
                        color = EecTheme.colors.onSurfaceVariant,
                    )
                    SecondaryButton(
                        text = if (state.exporting) {
                            appString(Res.string.exporting_csv)
                        } else {
                            appString(Res.string.action_export_all)
                        },
                        onClick = { onEvent(SettingsEvent.ExportAll) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canShareFiles && !state.exporting,
                    )
                    if (!canShareFiles) {
                        Text(
                            text = appString(Res.string.export_unavailable),
                            style = EecTheme.typography.caption,
                            color = EecTheme.colors.onSurfaceVariant,
                        )
                    }
                }
            }
            item { SectionHeader(title = appString(Res.string.section_about)) }
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
                text = appString(Res.string.categories_hint),
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
            SecondaryButton(
                text = appString(Res.string.action_manage_categories),
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
                text = appString(Res.string.about_version, AppInfo.VERSION, AppInfo.MILESTONE),
                style = EecTheme.typography.label,
                color = EecTheme.colors.onSurfaceVariant,
            )
            Text(
                text = appString(Res.string.about_description),
                style = EecTheme.typography.body,
                color = EecTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun themeLabel(mode: ThemeMode): String = appString(
    when (mode) {
        ThemeMode.SYSTEM -> Res.string.theme_system
        ThemeMode.LIGHT -> Res.string.theme_light
        ThemeMode.DARK -> Res.string.theme_dark
    },
)
