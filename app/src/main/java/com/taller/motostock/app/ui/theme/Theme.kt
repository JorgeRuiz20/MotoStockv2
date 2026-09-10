package com.taller.motostock.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.taller.motostock.core.designsystem.tokens.*

// Material 3 Color Schemes - Apex Moto Workshop Dark Automotive
private val ApexDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B00),            // Apex Racing Orange (CTA)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFF6B00),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFB77F),          // Warm Amber Gold (Telemetría / Métricas)
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFFFF8A00),
    onSecondaryContainer = Color(0xFF351000),
    tertiary = Color(0xFFB9C8DE),           // Ice Titanium (Specs / Tags)
    onTertiary = Color(0xFF233143),
    tertiaryContainer = Color(0xFF292A2D),
    onTertiaryContainer = Color(0xFFB9C8DE),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121316),         // Obsidian Black técnico
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF121316),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF343538),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFFA98A7D),
    outlineVariant = Color(0xFF292A2D)
)

private val ApexColors = MotoStockColors(
    primary = Color(0xFFFF6B00),            // Apex Racing Orange
    onPrimary = Color(0xFFFFFFFF),
    primaryFixed = Color(0xFFFFDBCC),
    onPrimaryFixed = Color(0xFF351000),
    primaryContainer = Color(0xFFFF6B00),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFFFB77F),          // Warm Amber Gold
    onSecondary = Color(0xFF4E2600),
    secondaryContainer = Color(0xFFFF8A00),
    onSecondaryContainer = Color(0xFF351000),
    tertiary = Color(0xFFB9C8DE),           // Ice Titanium
    onTertiary = Color(0xFF233143),
    tertiaryContainer = Color(0xFF292A2D),
    onTertiaryContainer = Color(0xFFB9C8DE),
    background = Color(0xFF121316),         // Obsidian Black
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF121316),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF343538),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceContainerLowest = Color(0xFF1B1B1F), // Contenedor oscuro base para cards
    surfaceContainerLow = Color(0xFF1B1B1F),    // Fondos de inputs y barras
    surfaceContainer = Color(0xFF1F1F23),       // Card contenedora
    surfaceContainerHigh = Color(0xFF292A2D),   // Hero tiles y banner taller
    surfaceContainerHighest = Color(0xFF343538),// Botones secundarios y stepper
    outline = Color(0xFFA98A7D),
    outlineVariant = Color(0xFF292A2D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    success = Color(0xFF10B981),            // Verde Esmeralda
    onSuccess = Color(0xFFFFFFFF),
    successContainer = Color(0xFF064E3B),
    onSuccessContainer = Color(0xFFD1FAE5),
    warning = Color(0xFFF59E0B),            // Ámbar alerta
    onWarning = Color(0xFFFFFFFF),
    warningContainer = Color(0xFF78350F),
    onWarningContainer = Color(0xFFFEF3C7),
    infoContainer = Color(0xFF1F1F23),
    onInfoContainer = Color(0xFFB9C8DE),
    isLight = false
)

@Composable
fun MotoStockTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) ApexColors else ApexColors
    val colorScheme = if (darkTheme) ApexDarkColorScheme else ApexDarkColorScheme

    val typography = MotoStockTypography(
        h1 = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.25).sp),
        h2 = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
        h3 = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
        bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp),
        bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
        labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp),
        labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp),
        labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.5.sp)
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF121316).toArgb()
            window.navigationBarColor = Color(0xFF121316).toArgb()
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = false
            insets.isAppearanceLightNavigationBars = false
        }
    }

    CompositionLocalProvider(
        LocalMotoStockColors provides colors,
        LocalMotoStockTypography provides typography,
        LocalMotoStockSpacing provides MotoStockSpacing(),
        LocalMotoStockShapes provides MotoStockShapes(),
        LocalMotoStockElevation provides MotoStockElevation()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(
                displayLarge = typography.h1,
                headlineLarge = typography.h2,
                headlineMedium = typography.h3,
                titleLarge = typography.h3,
                bodyLarge = typography.bodyLarge,
                bodyMedium = typography.bodyMedium,
                bodySmall = typography.bodySmall,
                labelLarge = typography.labelLarge,
                labelMedium = typography.labelMedium,
                labelSmall = typography.labelSmall
            ),
            content = content
        )
    }
}
