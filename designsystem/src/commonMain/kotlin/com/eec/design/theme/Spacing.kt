package com.eec.design.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object EecSpacing {
    val space1: Dp = 4.dp
    val space2: Dp = 8.dp
    val space3: Dp = 12.dp
    val space4: Dp = 16.dp
    val space5: Dp = 20.dp
    val space6: Dp = 24.dp
    val space8: Dp = 32.dp
    val space10: Dp = 40.dp
    val space12: Dp = 48.dp
    val space16: Dp = 64.dp

    val xxs: Dp = space1
    val xs: Dp = space2
    val sm: Dp = space3
    val md: Dp = space4
    val lg: Dp = space6
    val xl: Dp = space8
    val xxl: Dp = space12

    val screen: Dp = md
    val section: Dp = lg
}

object EecMotion {
    const val standardMillis: Int = 200
    const val emphasizedMillis: Int = 300
    const val quickMillis: Int = 120
    const val slowMillis: Int = 400
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
