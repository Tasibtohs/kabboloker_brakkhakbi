package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KabyolokorDarkColorScheme = darkColorScheme(
    primary = GoldLight,
    onPrimary = DarkBackground,
    primaryContainer = GoldDark.copy(alpha = 0.3f),
    onPrimaryContainer = GoldLight,
    secondary = SoftLavender,
    onSecondary = DarkBackground,
    secondaryContainer = LavenderDark.copy(alpha = 0.2f),
    onSecondaryContainer = SoftLavender,
    tertiary = GoldPrimary,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = Color(0xFF383838)
)

private val KabyolokorLightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = GoldLight.copy(alpha = 0.2f),
    onPrimaryContainer = GoldDark,
    secondary = LavenderDark,
    onSecondary = Color.White,
    secondaryContainer = SoftLavender.copy(alpha = 0.3f),
    onSecondaryContainer = LightTextPrimary,
    tertiary = GoldDark,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightBorder
)

@Composable
fun KabyolokorTheme(
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KabyolokorDarkColorScheme else KabyolokorLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}
