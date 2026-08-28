package com.taller.motostock.core.designsystem.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class MotoStockElevation(
    val none: Dp = 0.dp,
    val small: Dp = 2.dp,
    val medium: Dp = 4.dp,
    val large: Dp = 8.dp
)

val LocalMotoStockElevation = staticCompositionLocalOf { MotoStockElevation() }
