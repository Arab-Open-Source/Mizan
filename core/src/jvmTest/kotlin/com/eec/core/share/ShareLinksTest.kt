package com.eec.core.share

import kotlin.test.Test
import kotlin.test.assertEquals

class ShareLinksTest {
    @Test
    fun encodesArabicAsUtf8PercentBytes() {
        assertEquals("%D9%85%D8%B1%D8%AD%D8%A8%D8%A7", ShareLinks.encode("مرحبا"))
    }

    @Test
    fun encodesSpacesAndReservedCharacters() {
        assertEquals("a%20b%26c%3Fd%3De", ShareLinks.encode("a b&c?d=e"))
    }

    @Test
    fun keepsUnreservedCharacters() {
        assertEquals("Aa0-._~", ShareLinks.encode("Aa0-._~"))
    }

    @Test
    fun buildsWhatsAppLink() {
        assertEquals("https://wa.me/?text=%D8%A7%D8%AE%D8%AA%D8%A8%D8%A7%D8%B1", ShareLinks.whatsApp("اختبار"))
    }

    @Test
    fun buildsTelegramLinkWithEmptyUrl() {
        assertEquals("https://t.me/share/url?url=&text=a%20b", ShareLinks.telegram("a b"))
    }

    @Test
    fun buildsMailToLink() {
        assertEquals("mailto:?subject=a%20b&body=c%0Ad", ShareLinks.email("a b", "c\nd"))
    }
}
