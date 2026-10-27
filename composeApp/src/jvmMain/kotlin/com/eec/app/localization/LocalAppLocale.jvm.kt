package com.eec.app.localization

import java.util.Locale

internal actual fun applyPlatformLocale(tag: String) {
    Locale.setDefault(Locale.forLanguageTag(tag))
}
