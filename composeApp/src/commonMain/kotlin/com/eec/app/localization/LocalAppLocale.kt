package com.eec.app.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val LocalAppLocale: ProvidableCompositionLocal<LanguageMode> =
    staticCompositionLocalOf { LanguageMode.ARABIC }

@Composable
fun ProvideAppLocale(language: LanguageMode, content: @Composable () -> Unit) {
    applyLanguage(language)
    CompositionLocalProvider(
        LocalAppLocale provides language,
        LocalLayoutDirection provides if (language.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr,
        content = content,
    )
}

fun applyLanguage(language: LanguageMode) {
    if (appliedTag == language.tag) return
    appliedTag = language.tag
    applyPlatformLocale(language.tag)
}

@Composable
fun appString(resource: StringResource, vararg args: Any): String {
    LocalAppLocale.current
    return stringResource(resource, *args)
}

private var appliedTag: String? = null

internal expect fun applyPlatformLocale(tag: String)
