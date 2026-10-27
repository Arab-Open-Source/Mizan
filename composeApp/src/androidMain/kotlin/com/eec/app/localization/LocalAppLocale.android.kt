package com.eec.app.localization

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

internal actual fun applyPlatformLocale(tag: String) {
    Locale.setDefault(Locale.forLanguageTag(tag))
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
}
