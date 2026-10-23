package com.example.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = VividEmerald,
    onPrimary = Color.White,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    tertiary = SiriViolet,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    primaryContainer = Color(0xFF0D2E1B),
    onPrimaryContainer = Color(0xFF69F0AE),
    secondaryContainer = Color(0xFF102A45),
    onSecondaryContainer = Color(0xFF82B1FF),
    tertiaryContainer = Color(0xFF2B1438),
    onTertiaryContainer = Color(0xFFE1BEE7),
    error = AlertRed,
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF2C2C2E),
    surfaceVariant = Color(0xFF242426),
    onSurfaceVariant = AppleDarkTextMuted
)

private val LightColorScheme = lightColorScheme(
    primary = VividEmerald,
    onPrimary = Color.White,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    tertiary = SiriViolet,
    background = AppleCanvasBg,
    surface = AppleCardSurface,
    onBackground = AppleInk,
    onSurface = AppleInk,
    primaryContainer = VividEmeraldTint,
    onPrimaryContainer = Color(0xFF007A33),
    secondaryContainer = ElectricBlueTint,
    onSecondaryContainer = ElectricBlue,
    tertiaryContainer = SiriVioletTint,
    onTertiaryContainer = SiriViolet,
    error = AlertRed,
    outline = Color(0xFFD1D1D6),
    outlineVariant = Color(0xFFE5E5EA),
    surfaceVariant = AppleParchment,
    onSurfaceVariant = AppleTextMuted
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
