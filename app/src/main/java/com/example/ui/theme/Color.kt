package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme

// Brand Semantic Colors
val ItalianNavy = Color(0xFF0C2340)
val AzureBlue = Color(0xFF007A87)
val EmeraldSuccess = Color(0xFF2E7D32)
val AmberWarning = Color(0xFFD84315)
val StatusGray = Color(0xFF757575)

// Italian Flag Theme Colors (Green, White, Red)
val ItalianGreen = Color(0xFF008C45)
val ItalianWhite = Color(0xFFFFFFFF)
val ItalianRed = Color(0xFFCD212A)

// Soft pastel variations for light container backdrops
val ItalianGreenLight = Color(0xFFE8F5E9)
val ItalianRedLight = Color(0xFFFFEBEE)

// Bento Grid Palette Colors
val BentoBackground @Composable get() = if (isSystemInDarkTheme()) DarkBackground else Color(0xFFF5F7F6)
val BentoTextPrimary @Composable get() = if (isSystemInDarkTheme()) DarkOnBackground else Color(0xFF1E293B)
val BentoTextSecondary @Composable get() = if (isSystemInDarkTheme()) Color(0xFF94A3B8) else Color(0xFF64748B)
val BentoTextHeading @Composable get() = if (isSystemInDarkTheme()) DarkOnSurface else Color(0xFF0F172A)
val BentoCardPrimaryBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1B3B2B) else Color(0xFFE8F5E9)
val BentoCardPrimaryText @Composable get() = if (isSystemInDarkTheme()) Color(0xFFA5D6A7) else Color(0xFF1B5E20)
val BentoBadgeGreen = Color(0xFF008C45)
val BentoBadgeGreenBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1B3B2B) else Color(0xFFE8F5E9)
val BentoCardBorder @Composable get() = if (isSystemInDarkTheme()) Color(0xFF2D3748) else Color(0xFFE2E8F0)
val BentoCardLightBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1E2530) else Color(0xFFF1F5F9)
val BentoAvatarBg @Composable get() = if (isSystemInDarkTheme()) Color(0xFF1C2541) else Color(0xFFE2E8F0)
val BentoAvatarBorder @Composable get() = if (isSystemInDarkTheme()) Color(0xFF475569) else Color(0xFF94A3B8)
val BentoAlertRed = Color(0xFFCD212A)

val LightPrimary = Color(0xFF0F4C81) // Classic blue
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF1F7A8C)
val LightBackground = Color(0xFFF4F6F9)
val LightSurface = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1C2541)
val LightOnSurface = Color(0xFF1C2541)

// Dark Theme Colors
val DarkPrimary = Color(0xFF90CAF9)
val DarkOnPrimary = Color(0xFF0D47A1)
val DarkSecondary = Color(0xFF80DEEA)
val DarkBackground = Color(0xFF0B132B) // Deep space navy
val DarkSurface = Color(0xFF1C2541)    // Slate blue card
val DarkOnBackground = Color(0xFFE2E8F0)
val DarkOnSurface = Color(0xFFF1F5F9)

