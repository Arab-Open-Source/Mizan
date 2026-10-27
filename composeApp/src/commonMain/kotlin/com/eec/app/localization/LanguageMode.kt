package com.eec.app.localization

import com.eec.app.resources.Res
import com.eec.app.resources.language_arabic
import com.eec.app.resources.language_dari
import com.eec.app.resources.language_english
import com.eec.app.resources.language_french
import com.eec.app.resources.language_german
import com.eec.app.resources.language_italian
import com.eec.app.resources.language_pashto
import org.jetbrains.compose.resources.StringResource

enum class LanguageMode(
    val tag: String,
    val isRtl: Boolean,
    val label: StringResource,
) {
    ARABIC("ar", true, Res.string.language_arabic),
    ENGLISH("en", false, Res.string.language_english),
    FRENCH("fr", false, Res.string.language_french),
    ITALIAN("it", false, Res.string.language_italian),
    GERMAN("de", false, Res.string.language_german),
    DARI("fa", true, Res.string.language_dari),
    PASHTO("ps", true, Res.string.language_pashto),
    ;

    companion object {
        fun fromStorage(value: String?): LanguageMode =
            entries.firstOrNull { it.name == value } ?: ARABIC
    }
}
