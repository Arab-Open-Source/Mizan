package com.eec.design.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp

@Immutable
data class EecShapes(
    val radiusSmall: CornerBasedShape = RoundedCornerShape(8.dp),
    val radiusMedium: CornerBasedShape = RoundedCornerShape(12.dp),
    val radiusLarge: CornerBasedShape = RoundedCornerShape(16.dp),
    val radiusExtraLarge: CornerBasedShape = RoundedCornerShape(24.dp),
    val input: CornerBasedShape = RoundedCornerShape(10.dp),
    val card: CornerBasedShape = RoundedCornerShape(16.dp),
    val sheet: CornerBasedShape = radiusExtraLarge,
    val dialog: CornerBasedShape = RoundedCornerShape(28.dp),
    val chip: CornerBasedShape = RoundedCornerShape(percent = 50),
)

internal fun EecShapes.toMaterialShapes(): Shapes = Shapes(
    extraSmall = input,
    small = input,
    medium = card,
    large = sheet,
    extraLarge = dialog,
)
