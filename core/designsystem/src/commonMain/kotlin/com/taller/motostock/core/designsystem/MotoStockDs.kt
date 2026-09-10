package com.taller.motostock.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.taller.motostock.core.designsystem.tokens.*

object MotoStockDs {
    val colors: MotoStockColors
        @Composable
        @ReadOnlyComposable
        get() = LocalMotoStockColors.current

    val typography: MotoStockTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalMotoStockTypography.current

    val spacing: MotoStockSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalMotoStockSpacing.current

    val shapes: MotoStockShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalMotoStockShapes.current

    val elevation: MotoStockElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalMotoStockElevation.current
}
