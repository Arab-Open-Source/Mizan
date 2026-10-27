package com.eec.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalEecColors = staticCompositionLocalOf { LightColors }
val LocalEecTypography = staticCompositionLocalOf<EecTypography> {
    error("EecTypography must be provided by EecTheme")
}
val LocalEecShapes = staticCompositionLocalOf { EecShapes() }

object EecTheme {
    val colors: EecColors
        @Composable
        @ReadOnlyComposable
        get() = LocalEecColors.current

    val typography: EecTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalEecTypography.current

    val shapes: EecShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalEecShapes.current

    val spacing: EecSpacing
        get() = EecSpacing
}

@Composable
fun EecTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val typography = eecTypography()
    val shapes = EecShapes()
    val materialColors = if (darkTheme) darkMaterialColors(colors) else lightMaterialColors(colors)

    CompositionLocalProvider(
        LocalEecColors provides colors,
        LocalEecTypography provides typography,
        LocalEecShapes provides shapes,
    ) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = typography.asMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content,
        )
    }
}

private fun lightMaterialColors(colors: EecColors) = lightColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.primary,
    secondary = colors.secondary,
    onSecondary = colors.surface,
    tertiary = colors.tertiary,
    onTertiary = colors.surface,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onBackground,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    surfaceTint = colors.primary,
    outline = colors.outline,
    outlineVariant = colors.outline,
    error = colors.danger,
    onError = Color.White,
    scrim = Color.Black,
)

private fun darkMaterialColors(colors: EecColors) = darkColorScheme(
    primary = colors.primary,
    onPrimary = colors.onPrimary,
    primaryContainer = colors.primaryContainer,
    onPrimaryContainer = colors.primary,
    secondary = colors.secondary,
    onSecondary = colors.background,
    tertiary = colors.tertiary,
    onTertiary = colors.background,
    background = colors.background,
    onBackground = colors.onBackground,
    surface = colors.surface,
    onSurface = colors.onBackground,
    surfaceVariant = colors.surfaceVariant,
    onSurfaceVariant = colors.onSurfaceVariant,
    surfaceTint = colors.primary,
    outline = colors.outline,
    outlineVariant = colors.outline,
    error = colors.danger,
    onError = Color(0xFF601410),
    scrim = Color.Black,
)
