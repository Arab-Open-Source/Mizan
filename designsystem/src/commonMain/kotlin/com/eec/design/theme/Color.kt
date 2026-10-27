package com.eec.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class EecColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onBackground: Color,
    val onSurfaceVariant: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val secondary: Color,
    val tertiary: Color,
    val success: Color,
    val danger: Color,
    val successContainer: Color,
    val dangerContainer: Color,
    val outline: Color,
    val isDark: Boolean,
    val categoryPalette: CategoryPalette,
)

val LightColors: EecColors = EecColors(
    background = Color(0xFFFFFBF5),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF4EDE2),
    onBackground = Color(0xFF171412),
    onSurfaceVariant = Color(0xFF5C554A),
    primary = Color(0xFF0E7C6B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC9F0E7),
    secondary = Color(0xFF9A5B12),
    tertiary = Color(0xFFC2410C),
    success = Color(0xFF17803D),
    danger = Color(0xFFC0261E),
    successContainer = Color(0xFFDCFCE7),
    dangerContainer = Color(0xFFFEE2E2),
    outline = Color(0xFFDDD2C2),
    isDark = false,
    categoryPalette = CategoryPalette.Light,
)

val DarkColors: EecColors = EecColors(
    background = Color(0xFF0F1412),
    surface = Color(0xFF161D1A),
    surfaceVariant = Color(0xFF223029),
    onBackground = Color(0xFFF0F5F1),
    onSurfaceVariant = Color(0xFFB7C4BB),
    primary = Color(0xFF5EEAD4),
    onPrimary = Color(0xFF042F2A),
    primaryContainer = Color(0xFF0F3F38),
    secondary = Color(0xFFFCD34D),
    tertiary = Color(0xFFFDBA74),
    success = Color(0xFF86EFAC),
    danger = Color(0xFFFCA5A5),
    successContainer = Color(0xFF14351F),
    dangerContainer = Color(0xFF4C1D1D),
    outline = Color(0xFF34463D),
    isDark = true,
    categoryPalette = CategoryPalette.Dark,
)
