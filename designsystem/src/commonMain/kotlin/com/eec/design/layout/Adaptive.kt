package com.eec.design.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eec.design.theme.EecSizes

enum class WindowSizeClass { Compact, Medium, Expanded }

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    var result = WindowSizeClass.Compact
    BoxWithConstraints { result = windowSizeClass(maxWidth) }
    return result
}

fun windowSizeClass(width: Dp): WindowSizeClass = when {
    width < 600.dp -> WindowSizeClass.Compact
    width < 840.dp -> WindowSizeClass.Medium
    else -> WindowSizeClass.Expanded
}

fun isCompact(width: Dp): Boolean = width < EecSizes.railBreakpoint
