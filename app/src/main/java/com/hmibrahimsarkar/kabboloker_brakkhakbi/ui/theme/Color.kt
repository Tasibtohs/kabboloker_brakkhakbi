package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Golden & Amber Accent Colors
val AmberAccent = Color(0xFFD4A017)
val GoldLight = Color(0xFFF4C842)
val GoldPrimary = Color(0xFFD4A017)
val GoldDark = Color(0xFFB8860B)
val GoldGlow = Color(0xFFFFE57F)

// Gold Gradients
val GoldGradient = Brush.linearGradient(
    colors = listOf(GoldLight, GoldPrimary, GoldDark)
)

val GoldGlowGradient = Brush.radialGradient(
    colors = listOf(GoldLight.copy(alpha = 0.6f), Color.Transparent)
)

// Background & Surface Tokens
val LightBackground = Color(0xFFFAFAF8)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0EFE8)
val LightBorder = Color(0xFFB0AEA0) // Distinct, high-contrast crisp border for Light Mode

val DarkBackground = Color(0xFF000000) // Pitch / OLED Pure Black
val DarkSurface = Color(0xFF121212)    // Pitch dark surface for cards
val DarkSurfaceVariant = Color(0xFF1E1E1E)
val DarkBorder = Color(0xFF333333)

// Text Tokens for high contrast readability
val LightTextPrimary = Color(0xFF111318)
val LightTextSecondary = Color(0xFF4A4A5A)
val DarkTextPrimary = Color(0xFFF2F4FB)
val DarkTextSecondary = Color(0xFFA0A0B5)

// Accent Colors
val SoftLavender = Color(0xFFC9B3E8)
val LavenderDark = Color(0xFF8A6CB3)
val MutedGrey = Color(0xFF8A8A9E)
val RoseAccent = Color(0xFFE57373)
val EmeraldGreen = Color(0xFF81C784)

@androidx.compose.runtime.Composable
fun resolveAdaptiveTextColor(hexString: String): Color {
    val defaultOnSurface = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val bg = androidx.compose.material3.MaterialTheme.colorScheme.background
    val bgLuminance = bg.red * 0.2126f + bg.green * 0.7152f + bg.blue * 0.0722f
    val isDarkTheme = bgLuminance < 0.5f

    if (hexString.isBlank() || hexString.equals("#1A1A2E", ignoreCase = true) || hexString.equals("DEFAULT", ignoreCase = true)) {
        return defaultOnSurface
    }
    return try {
        val parsed = Color(android.graphics.Color.parseColor(hexString))
        val luminance = parsed.red * 0.2126f + parsed.green * 0.7152f + parsed.blue * 0.0722f

        if (isDarkTheme && luminance < 0.35f) {
            defaultOnSurface
        } else if (!isDarkTheme && luminance > 0.75f) {
            defaultOnSurface
        } else {
            parsed
        }
    } catch (e: Exception) {
        defaultOnSurface
    }
}

@androidx.compose.runtime.Composable
fun resolveAdaptiveTitleColor(hexString: String): Color {
    val defaultTitleColor = AmberAccent
    val defaultOnSurface = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    val bg = androidx.compose.material3.MaterialTheme.colorScheme.background
    val bgLuminance = bg.red * 0.2126f + bg.green * 0.7152f + bg.blue * 0.0722f
    val isDarkTheme = bgLuminance < 0.5f

    if (hexString.isBlank() || hexString.equals("DEFAULT", ignoreCase = true)) {
        return defaultTitleColor
    }
    if (hexString.equals("#1A1A2E", ignoreCase = true)) {
        return defaultOnSurface
    }
    return try {
        val parsed = Color(android.graphics.Color.parseColor(hexString))
        val luminance = parsed.red * 0.2126f + parsed.green * 0.7152f + parsed.blue * 0.0722f

        if (isDarkTheme && luminance < 0.35f) {
            defaultTitleColor
        } else if (!isDarkTheme && luminance > 0.85f) {
            defaultTitleColor
        } else {
            parsed
        }
    } catch (e: Exception) {
        defaultTitleColor
    }
}

