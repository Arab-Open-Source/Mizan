package com.eec.design.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.eec.design.resources.Res
import com.eec.design.resources.ibm_plex_sans_arabic_bold
import com.eec.design.resources.ibm_plex_sans_arabic_medium
import com.eec.design.resources.ibm_plex_sans_arabic_regular
import com.eec.design.resources.ibm_plex_sans_arabic_semibold
import org.jetbrains.compose.resources.Font

@Immutable
data class EecTypography(
    val display: TextStyle,
    val headline: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val label: TextStyle,
    val caption: TextStyle,
)

@Composable
fun eecFontFamily(): FontFamily = FontFamily(
    Font(Res.font.ibm_plex_sans_arabic_regular, FontWeight.Normal),
    Font(Res.font.ibm_plex_sans_arabic_medium, FontWeight.Medium),
    Font(Res.font.ibm_plex_sans_arabic_semibold, FontWeight.SemiBold),
    Font(Res.font.ibm_plex_sans_arabic_bold, FontWeight.Bold),
)

private fun EecTypography.toMaterialTypography(): Typography {
    val base = Typography()
    return base.copy(
        displayLarge = display,
        displayMedium = display,
        displaySmall = headline,
        headlineLarge = headline,
        headlineMedium = headline,
        headlineSmall = title,
        titleLarge = title,
        titleMedium = title,
        titleSmall = label,
        bodyLarge = body,
        bodyMedium = body,
        bodySmall = caption,
        labelLarge = bodyStrong,
        labelMedium = label,
        labelSmall = caption,
    )
}

@Composable
fun eecTypography(): EecTypography {
    val family = eecFontFamily()

    fun style(size: Int, lineHeight: Int, weight: FontWeight): TextStyle = TextStyle(
        fontFamily = family,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        fontWeight = weight,
        fontFeatureSettings = "tnum",
    )

    return EecTypography(
        display = style(size = 32, lineHeight = 40, weight = FontWeight.SemiBold),
        headline = style(size = 24, lineHeight = 32, weight = FontWeight.SemiBold),
        title = style(size = 18, lineHeight = 26, weight = FontWeight.SemiBold),
        body = style(size = 15, lineHeight = 24, weight = FontWeight.Normal),
        bodyStrong = style(size = 15, lineHeight = 24, weight = FontWeight.Medium),
        label = style(size = 13, lineHeight = 18, weight = FontWeight.Medium),
        caption = style(size = 12, lineHeight = 16, weight = FontWeight.Normal),
    )
}

internal fun EecTypography.asMaterialTypography(): Typography = toMaterialTypography()
