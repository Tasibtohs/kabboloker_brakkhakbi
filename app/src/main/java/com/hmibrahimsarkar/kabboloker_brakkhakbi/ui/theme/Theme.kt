package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography  // ← এই ইমপোর্ট যোগ করুন
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Color definitions (আপনার যেগুলো আছে)
private val GoldLight = Color(0xFFFFD700)
private val GoldDark = Color(0xFFB8860B)
private val GoldPrimary = Color(0xFFD4A017)
private val DarkBackground = Color(0xFF1A1A2E)
private val DarkSurface = Color(0xFF2D2D44)
private val DarkSurfaceVariant = Color(0xFF3D3D5C)
private val DarkTextPrimary = Color(0xFFE8E8E8)
private val DarkTextSecondary = Color(0xFFB0B0B0)
private val DarkBorder = Color(0xFF4A4A6A)
private val SoftLavender = Color(0xFFB39DDB)
private val LavenderDark = Color(0xFF7E57C2)
private val LightBackground = Color(0xFFF5F0EB)
private val LightSurface = Color(0xFFFFFFFF)
private val LightSurfaceVariant = Color(0xFFF0EBE6)
private val LightTextPrimary = Color(0xFF1A1A2E)
private val LightTextSecondary = Color(0xFF666666)
private val LightBorder = Color(0xFFE0D6CC)

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

// ← এখানে Typography ডিফাইন করুন
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 30.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )
)

@Composable
fun KabyolokorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        KabyolokorDarkColorScheme
    } else {
        KabyolokorLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,  // ← এখানে 'AppTypography' ব্যবহার করুন
        content = content
    )
}
