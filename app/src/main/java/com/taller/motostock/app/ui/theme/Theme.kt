package com.taller.motostock.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.taller.motostock.core.designsystem.tokens.*

private val DarkColorScheme = darkColorScheme(
    primary = AzulTaller,
    secondary = NaranjaMoto,
    tertiary = VerdeOk,
    background = Negro,
    surface = AzulTallerOscuro,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onTertiary = Blanco,
    onBackground = Blanco,
    onSurface = Blanco,
)

private val LightColorScheme = lightColorScheme(
    primary = AzulTaller,
    secondary = NaranjaMoto,
    tertiary = VerdeOk,
    background = GrisClaro,
    surface = Blanco,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onTertiary = Blanco,
    onBackground = Negro,
    onSurface = Negro,
)

@Composable
fun MotoStockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        MotoStockColors(
            primary = AzulTaller,
            onPrimary = Blanco,
            secondary = NaranjaMoto,
            onSecondary = Blanco,
            tertiary = VerdeOk,
            onTertiary = Blanco,
            background = Negro,
            onBackground = Blanco,
            surface = AzulTallerOscuro,
            onSurface = Blanco,
            error = RojoAlerta,
            onError = Blanco,
            success = VerdeOk,
            onSuccess = Blanco,
            isLight = false
        )
    } else {
        MotoStockColors(
            primary = AzulTaller,
            onPrimary = Blanco,
            secondary = NaranjaMoto,
            onSecondary = Blanco,
            tertiary = VerdeOk,
            onTertiary = Blanco,
            background = GrisClaro,
            onBackground = Negro,
            surface = Blanco,
            onSurface = Negro,
            error = RojoAlerta,
            onError = Blanco,
            success = VerdeOk,
            onSuccess = Blanco,
            isLight = true
        )
    }

    val typography = MotoStockTypography(
        h1 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 30.sp),
        h2 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp),
        h3 = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
        bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp),
        labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
        labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
        labelSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 10.sp)
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
            typography = Typography,
            content = content
        )
    }
}
