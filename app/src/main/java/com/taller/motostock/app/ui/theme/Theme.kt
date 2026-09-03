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

// Material 3 Color Schemes fallback
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFACC7FF),
    onPrimary = Color(0xFF002F67),
    primaryContainer = Color(0xFF08458E),
    onPrimaryContainer = Color(0xFFD7E2FF),
    secondary = Color(0xFFFFB68C),
    onSecondary = Color(0xFF532200),
    secondaryContainer = Color(0xFF763400),
    onSecondaryContainer = Color(0xFFFFDBC9),
    tertiary = Color(0xFF82DB7E),
    onTertiary = Color(0xFF00390A),
    tertiaryContainer = Color(0xFF005312),
    onTertiaryContainer = Color(0xFF9DF898),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1A1C1C),
    onBackground = Color(0xFFE2E2E2),
    surface = Color(0xFF1A1C1C),
    onSurface = Color(0xFFE2E2E2),
    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF002452),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1B3A6B),
    onPrimaryContainer = Color(0xFF89A5DD),
    secondary = Color(0xFF9A4601),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFE924E),
    onSecondaryContainer = Color(0xFF6D2F00),
    tertiary = Color(0xFF002C06),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF00450E),
    onTertiaryContainer = Color(0xFF61B760),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF93000A),
    background = Color(0xFFF9F9F9),
    onBackground = Color(0xFF1A1C1C),
    surface = Color(0xFFF9F9F9),
    onSurface = Color(0xFF1A1C1C),
    surfaceVariant = Color(0xFFE2E2E2),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFF747780)
)

@Composable
fun MotoStockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        MotoStockColors(
            primary = Color(0xFFACC7FF),
            onPrimary = Color(0xFF002F67),
            primaryFixed = Color(0xFFD7E2FF),
            onPrimaryFixed = Color(0xFF001A40),
            primaryContainer = Color(0xFF08458E),
            onPrimaryContainer = Color(0xFFD7E2FF),
            secondary = Color(0xFFFFB68C),
            onSecondary = Color(0xFF532200),
            secondaryContainer = Color(0xFF763400),
            onSecondaryContainer = Color(0xFFFFDBC9),
            tertiary = Color(0xFF82DB7E),
            onTertiary = Color(0xFF00390A),
            tertiaryContainer = Color(0xFF005312),
            onTertiaryContainer = Color(0xFF9DF898),
            background = Color(0xFF1A1C1C),
            onBackground = Color(0xFFE2E2E2),
            surface = Color(0xFF1A1C1C),
            onSurface = Color(0xFFE2E2E2),
            surfaceVariant = Color(0xFF44474F),
            onSurfaceVariant = Color(0xFFC4C6D0),
            surfaceContainerLowest = Color(0xFF0E0E0E),
            surfaceContainerLow = Color(0xFF1C1C1C),
            surfaceContainer = Color(0xFF202020),
            surfaceContainerHigh = Color(0xFF2B2B2B),
            surfaceContainerHighest = Color(0xFF363636),
            outline = Color(0xFF8E9099),
            outlineVariant = Color(0xFF44474F),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            success = Color(0xFF388E3C),
            onSuccess = Color(0xFFFFFFFF),
            successContainer = Color(0xFF1B5E20),
            onSuccessContainer = Color(0xFFC8E6C9),
            warning = Color(0xFFFFB74D),
            onWarning = Color(0xFF3E2100),
            warningContainer = Color(0xFF6B3B00),
            onWarningContainer = Color(0xFFFFDDB8),
            infoContainer = Color(0xFF1B3A6B),
            onInfoContainer = Color(0xFFD7E2FF),
            isLight = false
        )
    } else {
        MotoStockColors(
            primary = Color(0xFF002452),
            onPrimary = Color(0xFFFFFFFF),
            primaryFixed = Color(0xFFD7E2FF),
            onPrimaryFixed = Color(0xFF001A40),
            primaryContainer = Color(0xFF1B3A6B),
            onPrimaryContainer = Color(0xFF89A5DD),
            secondary = Color(0xFF9A4601),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFE924E),
            onSecondaryContainer = Color(0xFF6D2F00),
            tertiary = Color(0xFF002C06),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFF00450E),
            onTertiaryContainer = Color(0xFF61B760),
            background = Color(0xFFF9F9F9),
            onBackground = Color(0xFF1A1C1C),
            surface = Color(0xFFF9F9F9),
            onSurface = Color(0xFF1A1C1C),
            surfaceVariant = Color(0xFFE2E2E2),
            onSurfaceVariant = Color(0xFF44474F),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF3F3F3),
            surfaceContainer = Color(0xFFEEEEEE),
            surfaceContainerHigh = Color(0xFFE8E8E8),
            surfaceContainerHighest = Color(0xFFE2E2E2),
            outline = Color(0xFF747780),
            outlineVariant = Color(0xFFC4C6D0),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF93000A),
            success = Color(0xFF388E3C),
            onSuccess = Color(0xFFFFFFFF),
            successContainer = Color(0xFFE8F5E9),
            onSuccessContainer = Color(0xFF1B5E20),
            warning = Color(0xFFF57C00),
            onWarning = Color(0xFFFFFFFF),
            warningContainer = Color(0xFFFFF3E0),
            onWarningContainer = Color(0xFFE65100),
            infoContainer = Color(0xFFE8F0FE),
            onInfoContainer = Color(0xFF1B3A6B),
            isLight = true
        )
    }

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
            window.statusBarColor = (if (darkTheme) DarkColorScheme.primary else LightColorScheme.primary).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
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
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
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
