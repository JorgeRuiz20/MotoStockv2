package com.taller.motostock.core.designsystem.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class MotoStockShapes(
    val extraSmall: Shape = RoundedCornerShape(4.dp), // Tailwind rounded (DEFAULT)
    val small: Shape = RoundedCornerShape(8.dp),      // Tailwind rounded-lg
    val medium: Shape = RoundedCornerShape(12.dp),    // Tailwind rounded-xl
    val large: Shape = RoundedCornerShape(16.dp),     // Tailwind rounded-2xl
    val extraLarge: Shape = RoundedCornerShape(24.dp), // Tailwind rounded-3xl
    val full: Shape = RoundedCornerShape(9999.dp)
)

val LocalMotoStockShapes = staticCompositionLocalOf { MotoStockShapes() }
