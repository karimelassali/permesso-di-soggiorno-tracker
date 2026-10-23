package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ============================================================================
// APPLE iOS 18 VIVID DESIGN SYSTEM TOKENS
// ============================================================================

// Canvas & Surfaces
val AppleCanvasBg = Color(0xFFFBFBFA)           // Crisp iOS base canvas
val AppleCardSurface = Color(0xFFFFFFFF)        // Pure white squircle cards
val AppleParchment = Color(0xFFF5F5F7)          // Secondary grouped background
val AppleBorderHairline = Color(0x0F000000)     // Subtle 1px hairline border (rgba(0,0,0,0.06))

// High-Energy Vivid Accents
val VividEmerald = Color(0xFF00C853)            // Approved status, primary live badges, hero button
val VividEmeraldGlow = Color(0x3300C853)        // Ambient pulsing aura
val ElectricBlue = Color(0xFF007AFF)            // iOS Action Blue, verified seals, links
val SunsetTangerine = Color(0xFFFF9500)         // Convocazione & appointment alerts
val AlertRed = Color(0xFFFF3B30)                // Schengen forbidden transit warnings
val SiriViolet = Color(0xFFAF52DE)              // Sofia AI assistant glow & accent
val AppleInk = Color(0xFF1D1D1F)                // High-contrast primary text
val AppleTextMuted = Color(0xFF6E6E73)          // Secondary subtitles and captions

// Soft tinted surfaces for vivid accents
val VividEmeraldTint = Color(0xFFE8FBF0)
val ElectricBlueTint = Color(0xFFEBF5FF)
val SunsetTangerineTint = Color(0xFFFFF8F0)
val AlertRedTint = Color(0xFFFFF0EF)
val SiriVioletTint = Color(0xFFF7EEFC)

// Dark Theme iOS 18 Equivalents
val AppleDarkCanvasBg = Color(0xFF000000)
val AppleDarkCardSurface = Color(0xFF1C1C1E)
val AppleDarkParchment = Color(0xFF2C2C2E)
val AppleDarkBorderHairline = Color(0x26FFFFFF)
val AppleDarkInk = Color(0xFFF5F5F7)
val AppleDarkTextMuted = Color(0xFF98989D)

// Brand Semantic & Legacy Compatibility Aliases
val ItalianNavy = Color(0xFF0C2340)
val AzureBlue = ElectricBlue
val EmeraldSuccess = VividEmerald
val AmberWarning = SunsetTangerine
val StatusGray = AppleTextMuted

// Italian Flag Theme Colors (Green, White, Red)
val ItalianGreen = Color(0xFF008C45)
val ItalianWhite = Color(0xFFFFFFFF)
val ItalianRed = Color(0xFFCD212A)

// Soft pastel variations for light container backdrops
val ItalianGreenLight = VividEmeraldTint
val ItalianRedLight = AlertRedTint

// Theme helper to detect if we are in the DarkTheme
val isThemeDark @Composable get() = MaterialTheme.colorScheme.background == DarkBackground

// Adaptive iOS / Bento Tokens
val BentoBackground @Composable get() = if (isThemeDark) DarkBackground else AppleCanvasBg
val BentoTextPrimary @Composable get() = if (isThemeDark) AppleDarkInk else AppleInk
val BentoTextSecondary @Composable get() = if (isThemeDark) AppleDarkTextMuted else AppleTextMuted
val BentoTextHeading @Composable get() = if (isThemeDark) AppleDarkInk else AppleInk
val BentoCardPrimaryBg @Composable get() = if (isThemeDark) Color(0xFF0D2E1B) else VividEmeraldTint
val BentoCardPrimaryText @Composable get() = if (isThemeDark) Color(0xFF69F0AE) else Color(0xFF008C3A)
val BentoBadgeGreen = VividEmerald
val BentoBadgeGreenBg @Composable get() = if (isThemeDark) Color(0xFF0D2E1B) else VividEmeraldTint
val BentoCardBorder @Composable get() = if (isThemeDark) AppleDarkBorderHairline else Color(0xFFE5E5EA)
val BentoCardLightBg @Composable get() = if (isThemeDark) AppleDarkCardSurface else AppleCardSurface
val BentoAvatarBg @Composable get() = if (isThemeDark) AppleDarkParchment else AppleParchment
val BentoAvatarBorder @Composable get() = if (isThemeDark) Color(0xFF48484A) else Color(0xFFD1D1D6)
val BentoAlertRed = AlertRed

val LightPrimary = VividEmerald
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = ElectricBlue
val LightBackground = AppleCanvasBg
val LightSurface = AppleCardSurface
val LightOnBackground = AppleInk
val LightOnSurface = AppleInk

// Dark Theme Colors
val DarkPrimary = VividEmerald
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkSecondary = ElectricBlue
val DarkBackground = Color(0xFF0B132B)
val DarkSurface = Color(0xFF1C1C1E)
val DarkOnBackground = Color(0xFFF5F5F7)
val DarkOnSurface = Color(0xFFF5F5F7)
