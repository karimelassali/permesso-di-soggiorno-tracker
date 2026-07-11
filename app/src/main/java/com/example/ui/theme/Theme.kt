package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ItalianGreen,
    onPrimary = Color.White,
    secondary = ItalianRed,
    onSecondary = Color.White,
    background = BentoBackground,
    surface = Color.White,
    onBackground = BentoTextPrimary,
    onSurface = BentoTextPrimary,
    primaryContainer = ItalianGreenLight,
    onPrimaryContainer = BentoCardPrimaryText,
    secondaryContainer = ItalianGreenLight,
    onSecondaryContainer = ItalianGreen,
    tertiary = ItalianRed,
    tertiaryContainer = ItalianRedLight,
    onTertiaryContainer = ItalianRed,
    error = ItalianRed,
    outline = BentoAvatarBorder,
    outlineVariant = BentoCardBorder,
    surfaceVariant = BentoCardLightBg
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Light theme by default as requested by the user
  // Disable dynamic colors by default to show the Bento design system exactly
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
