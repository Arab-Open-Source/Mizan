package com.eec.design.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

data class HsvColor(
    val hue: Float,
    val saturation: Float,
    val value: Float,
)

object ColorHex {
    private const val HEX_LENGTH: Int = 6
    private const val MAX_CHANNEL: Int = 255
    private const val HUE_MAX: Float = 360f
    private const val HUE_SEXTANT: Float = 60f
    private const val CHANNEL_SCALE: Float = 255f
    private const val LUMINANCE_THRESHOLD: Float = 0.5f
    private val DARK_INK: Color = Color(0xFF141414)
    private val LIGHT_INK: Color = Color(0xFFFFFFFF)

    fun parse(hex: String): Color? {
        val digits = hex.trim().removePrefix("#")
        if (digits.length != HEX_LENGTH) return null
        if (!digits.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }) return null
        val rgb = digits.toLongOrNull(16) ?: return null
        return Color(0xFF000000L or rgb)
    }

    fun format(color: Color): String {
        fun channel(value: Float): String =
            (value * CHANNEL_SCALE).roundToInt().coerceIn(0, MAX_CHANNEL).toString(16).padStart(2, '0')

        return "#${channel(color.red)}${channel(color.green)}${channel(color.blue)}".uppercase()
    }

    fun isHex(value: String): Boolean = parse(value) != null

    fun luminance(color: Color): Float {
        fun linear(channel: Float): Float =
            if (channel <= 0.03928f) channel / 12.92f else ((channel + 0.055f) / 1.055f).pow(2.4f)

        return 0.2126f * linear(color.red) +
            0.7152f * linear(color.green) +
            0.0722f * linear(color.blue)
    }

    fun onColorFor(color: Color): Color =
        if (luminance(color) > LUMINANCE_THRESHOLD) DARK_INK else LIGHT_INK

    fun toHsv(color: Color): HsvColor {
        val maximum = max(color.red, max(color.green, color.blue))
        val minimum = min(color.red, min(color.green, color.blue))
        val delta = maximum - minimum
        val hue = when {
            delta == 0f -> 0f
            maximum == color.red -> HUE_SEXTANT * (((color.green - color.blue) / delta) % 6f)
            maximum == color.green -> HUE_SEXTANT * (((color.blue - color.red) / delta) + 2f)
            else -> HUE_SEXTANT * (((color.red - color.green) / delta) + 4f)
        }
        return HsvColor(
            hue = (hue + HUE_MAX) % HUE_MAX,
            saturation = if (maximum == 0f) 0f else delta / maximum,
            value = maximum,
        )
    }

    fun toColor(hsv: HsvColor): Color {
        val hue = ((hsv.hue % HUE_MAX) + HUE_MAX) % HUE_MAX
        val chroma = hsv.value * hsv.saturation
        val secondary = chroma * (1f - abs((hue / HUE_SEXTANT) % 2f - 1f))
        val offset = hsv.value - chroma
        val (red, green, blue) = when ((hue / HUE_SEXTANT).toInt()) {
            0 -> Triple(chroma, secondary, 0f)
            1 -> Triple(secondary, chroma, 0f)
            2 -> Triple(0f, chroma, secondary)
            3 -> Triple(0f, secondary, chroma)
            4 -> Triple(secondary, 0f, chroma)
            else -> Triple(chroma, 0f, secondary)
        }
        return Color(red + offset, green + offset, blue + offset)
    }
}
