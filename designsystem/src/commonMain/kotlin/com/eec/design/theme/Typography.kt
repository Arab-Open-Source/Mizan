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
    val primaryBalance: TextStyle = display,
    val secondaryMetric: TextStyle = display,
    val pageTitle: TextStyle = display,
    val sectionTitle: TextStyle = display,
    val cardTitle: TextStyle = display,
    val headline: TextStyle,
    val title: TextStyle,
    val body: TextStyle,
    val bodyStrong: TextStyle,
    val secondaryBody: TextStyle = body,
    val formLabel: TextStyle = body,
    val buttonLabel: TextStyle = body,
    val label: TextStyle,
    val caption: TextStyle,
    val numericMetadata: TextStyle = caption,
    val tableHeading: TextStyle = label,
    val chartAnnotation: TextStyle = caption,
    val helper: TextStyle = caption,
    val error: TextStyle = helper,
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

    val display = style(size = 32, lineHeight = 40, weight = FontWeight.SemiBold)
    val headline = style(size = 24, lineHeight = 32, weight = FontWeight.SemiBold)
    val title = style(size = 18, lineHeight = 26, weight = FontWeight.SemiBold)
    val body = style(size = 15, lineHeight = 24, weight = FontWeight.Normal)
    val bodyStrong = style(size = 15, lineHeight = 24, weight = FontWeight.Medium)
    val label = style(size = 13, lineHeight = 18, weight = FontWeight.Medium)
    val caption = style(size = 12, lineHeight = 16, weight = FontWeight.Normal)

    return EecTypography(
        display = display,
        primaryBalance = style(size = 36, lineHeight = 44, weight = FontWeight.Bold),
        secondaryMetric = style(size = 20, lineHeight = 28, weight = FontWeight.SemiBold),
        pageTitle = style(size = 22, lineHeight = 30, weight = FontWeight.SemiBold),
        sectionTitle = style(size = 18, lineHeight = 26, weight = FontWeight.SemiBold),
        cardTitle = style(size = 16, lineHeight = 24, weight = FontWeight.SemiBold),
        headline = headline,
        title = title,
        body = body,
        bodyStrong = bodyStrong,
        secondaryBody = style(size = 14, lineHeight = 22, weight = FontWeight.Normal),
        formLabel = style(size = 13, lineHeight = 18, weight = FontWeight.Medium),
        buttonLabel = style(size = 14, lineHeight = 20, weight = FontWeight.Medium),
        label = label,
        caption = caption,
        numericMetadata = style(size = 12, lineHeight = 16, weight = FontWeight.Medium),
        tableHeading = style(size = 12, lineHeight = 16, weight = FontWeight.SemiBold),
        chartAnnotation = style(size = 11, lineHeight = 14, weight = FontWeight.Normal),
        helper = style(size = 12, lineHeight = 16, weight = FontWeight.Normal),
        error = style(size = 12, lineHeight = 16, weight = FontWeight.Medium),
    )
}

internal fun EecTypography.asMaterialTypography(): Typography = toMaterialTypography()
