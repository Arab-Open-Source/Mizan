package com.eec.app.localization

import kotlinx.browser.document
import org.w3c.dom.HTMLElement

internal actual fun applyPlatformLocale(tag: String) {
    val root = document.documentElement as? HTMLElement
    root?.lang = tag
    root?.dir = if (tag in RTL_TAGS) "rtl" else "ltr"
    installNavigatorLanguage(tag)
}

private val RTL_TAGS: Set<String> = setOf("ar", "fa", "ps")

@Suppress("UnsafeCastFromDynamic")
private val installNavigatorLanguage: (String) -> Unit = js(
    """function(tag) {
        Object.defineProperty(window.navigator, 'languages', {
            configurable: true,
            get: function () { return [tag]; }
        });
        Object.defineProperty(window.navigator, 'language', {
            configurable: true,
            get: function () { return tag; }
        });
    }""",
)
