package com.eec.design.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object EecSpacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp

    val screen: Dp = md
    val section: Dp = lg
}

object EecMotion {
    const val standardMillis: Int = 200
    const val emphasizedMillis: Int = 300
}

object EecSizes {
    val minTouchTarget: Dp = 48.dp
    val icon: Dp = 24.dp
    val maxContentWidth: Dp = 960.dp
    val border: Dp = 1.dp
    val selectedBorder: Dp = 2.dp
    val chartHeight: Dp = 160.dp
    val editorWidth: Dp = 560.dp
    val editorMaxHeight: Dp = 760.dp
    val railBreakpoint: Dp = 600.dp
    val listBottomInset: Dp = 96.dp
}
