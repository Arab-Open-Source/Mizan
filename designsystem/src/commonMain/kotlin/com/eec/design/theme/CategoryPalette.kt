package com.eec.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

enum class CategoryColorKey(val key: String) {
    TEAL("teal"),
    GOLD("gold"),
    RUST("rust"),
    GREEN("green"),
    RED("red"),
    SLATE("slate"),
    PLUM("plum"),
    OLIVE("olive"),
    ;

    companion object {
        fun fromKey(key: String): CategoryColorKey? = entries.firstOrNull { it.key == key }
    }
}

@Immutable
data class CategoryPalette(private val colors: Map<CategoryColorKey, Color>) {
    val keys: List<CategoryColorKey> = CategoryColorKey.entries

    fun colorFor(key: String): Color {
        ColorHex.parse(key)?.let { return it }
        return CategoryColorKey.fromKey(key)?.let(colors::get) ?: colors.getValue(CategoryColorKey.TEAL)
    }

    fun colorFor(key: CategoryColorKey): Color = colors.getValue(key)

    fun onColorFor(key: String): Color = ColorHex.onColorFor(colorFor(key))

    fun onColorFor(color: Color): Color = ColorHex.onColorFor(color)

    companion object {
        val Light: CategoryPalette = CategoryPalette(
            mapOf(
                CategoryColorKey.TEAL to Color(0xFF173B32),
                CategoryColorKey.GOLD to Color(0xFF9A5B12),
                CategoryColorKey.RUST to Color(0xFFC2410C),
                CategoryColorKey.GREEN to Color(0xFF17803D),
                CategoryColorKey.RED to Color(0xFFC0261E),
                CategoryColorKey.SLATE to Color(0xFF475569),
                CategoryColorKey.PLUM to Color(0xFF7E22CE),
                CategoryColorKey.OLIVE to Color(0xFF4D7C0F),
            ),
        )

        val Dark: CategoryPalette = CategoryPalette(
            mapOf(
                CategoryColorKey.TEAL to Color(0xFF45B88A),
                CategoryColorKey.GOLD to Color(0xFFFCD34D),
                CategoryColorKey.RUST to Color(0xFFFDBA74),
                CategoryColorKey.GREEN to Color(0xFF86EFAC),
                CategoryColorKey.RED to Color(0xFFFCA5A5),
                CategoryColorKey.SLATE to Color(0xFF94A3B8),
                CategoryColorKey.PLUM to Color(0xFFD8B4FE),
                CategoryColorKey.OLIVE to Color(0xFFBEF264),
            ),
        )
    }
}
