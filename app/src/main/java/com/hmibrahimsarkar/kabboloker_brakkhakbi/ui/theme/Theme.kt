package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun KabyolokorTheme(
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val themeData = AppThemes.RoyalGold
    val finalColorScheme = themeData.colorSchemeFor(isDarkMode)

    MaterialTheme(
        colorScheme = finalColorScheme,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}
