package com.eec.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CategoryPaletteTest {
    private val palette = CategoryPalette.Light

    @Test
    fun resolvesTokenKeys() {
        assertEquals(Color(0xFF0E7C6B), palette.colorFor("teal"))
        assertEquals(Color(0xFF7E22CE), palette.colorFor("plum"))
    }

    @Test
    fun resolvesHexKeys() {
        assertEquals(Color(0xFF0EA5E9), palette.colorFor("#0EA5E9"))
        assertEquals(Color(0xFF0EA5E9), palette.colorFor("#0ea5e9"))
    }

    @Test
    fun fallsBackToTealForUnknownKey() {
        assertEquals(palette.colorFor("teal"), palette.colorFor("missing"))
        assertEquals(palette.colorFor("teal"), palette.colorFor("#12"))
    }

    @Test
    fun parsesAndFormatsHex() {
        assertEquals("#0EA5E9", ColorHex.format(assertNotNull(ColorHex.parse("#0ea5e9"))))
        assertEquals("#FFFFFF", ColorHex.format(assertNotNull(ColorHex.parse("FFFFFF"))))
        assertEquals(null, ColorHex.parse("#12345"))
        assertEquals(null, ColorHex.parse("#GGGGGG"))
    }

    @Test
    fun choosesReadableOnColor() {
        assertEquals(Color(0xFF141414), ColorHex.onColorFor(Color(0xFFFFFFFF)))
        assertEquals(Color(0xFFFFFFFF), ColorHex.onColorFor(Color(0xFF000000)))
        assertEquals(Color(0xFF141414), palette.onColorFor("#FDE047"))
        assertEquals(Color(0xFFFFFFFF), palette.onColorFor("#0E7C6B"))
    }

    @Test
    fun roundTripsThroughHsv() {
        val colors = listOf(0xFF0E7C6B, 0xFFC2410C, 0xFF7E22CE, 0xFFFACC15, 0xFF111827)
        colors.forEach { value ->
            val color = Color(value)
            val hsv = ColorHex.toHsv(color)
            assertTrue(abs(hsv.hue) <= 360f)
            assertEquals(ColorHex.format(color), ColorHex.format(ColorHex.toColor(hsv)))
        }
    }
}
