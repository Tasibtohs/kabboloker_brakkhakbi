package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class AppThemeData(
    val id: String,
    val nameBn: String,
    val descriptionBn: String,
    val isDark: Boolean = true,
    val darkColorScheme: ColorScheme,
    val lightColorScheme: ColorScheme,
    val darkPreviewBg: Color = Color(0xFF000000),
    val darkPreviewCard: Color = Color(0xFF121212),
    val darkPreviewPrimary: Color,
    val darkPreviewText: Color = Color(0xFFF3F4F6),
    val darkPreviewSecondary: Color,
    val lightPreviewBg: Color = Color(0xFFF8F5EC),
    val lightPreviewCard: Color = Color(0xFFFFFFFF),
    val lightPreviewPrimary: Color,
    val lightPreviewText: Color = Color(0xFF111827),
    val lightPreviewSecondary: Color,
    val isCustom: Boolean = false,
    val primaryHex: String? = null,
    val secondaryHex: String? = null,
    val darkBgHex: String? = null,
    val lightBgHex: String? = null
) {
    fun colorSchemeFor(isDark: Boolean): ColorScheme = if (isDark) darkColorScheme else lightColorScheme

    fun previewBgFor(isDark: Boolean): Color = if (isDark) darkPreviewBg else lightPreviewBg
    fun previewCardFor(isDark: Boolean): Color = if (isDark) darkPreviewCard else lightPreviewCard
    fun previewPrimaryFor(isDark: Boolean): Color = if (isDark) darkPreviewPrimary else lightPreviewPrimary
    fun previewTextFor(isDark: Boolean): Color = if (isDark) darkPreviewText else lightPreviewText
    fun previewSecondaryFor(isDark: Boolean): Color = if (isDark) darkPreviewSecondary else lightPreviewSecondary
}

fun parseHexColor(hexStr: String?, defaultColor: Color): Color {
    if (hexStr.isNullOrBlank()) return defaultColor
    return try {
        val cleanHex = hexStr.trim().removePrefix("#")
        val colorInt = when (cleanHex.length) {
            6 -> android.graphics.Color.parseColor("#$cleanHex")
            8 -> android.graphics.Color.parseColor("#$cleanHex")
            3 -> {
                val r = cleanHex[0]
                val g = cleanHex[1]
                val b = cleanHex[2]
                android.graphics.Color.parseColor("#$r$r$g$g$b$b")
            }
            else -> return defaultColor
        }
        Color(colorInt)
    } catch (e: Exception) {
        defaultColor
    }
}

fun buildDarkColorScheme(
    primary: Color,
    secondary: Color,
    tertiary: Color = primary,
    bg: Color = Color(0xFF000000), // Pure Black mandatory for Dark mode
    surface: Color = Color(0xFF121212),
    surfaceVariant: Color = Color(0xFF1E1E24)
): ColorScheme {
    val isPrimaryLight = (0.2126f * primary.red + 0.7152f * primary.green + 0.0722f * primary.blue) > 0.45f
    val onPrimaryColor = if (isPrimaryLight) Color.Black else Color.White

    return darkColorScheme(
        primary = primary,
        onPrimary = onPrimaryColor,
        primaryContainer = primary.copy(alpha = 0.22f),
        onPrimaryContainer = primary,
        secondary = secondary,
        onSecondary = Color.Black,
        secondaryContainer = secondary.copy(alpha = 0.20f),
        onSecondaryContainer = secondary,
        tertiary = tertiary,
        onTertiary = Color.Black,
        background = bg,
        onBackground = Color(0xFFF3F4F6),
        surface = surface,
        onSurface = Color(0xFFF3F4F6),
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = Color(0xFFA1A1AA),
        outline = Color(0xFF2E3440),
        outlineVariant = Color(0xFF2E3440)
    )
}

fun buildLightColorScheme(
    primary: Color,
    secondary: Color,
    tertiary: Color = primary,
    bg: Color = Color(0xFFFAFAFC),
    surface: Color = Color(0xFFFFFFFF),
    surfaceVariant: Color = Color(0xFFF1F5F9)
): ColorScheme {
    val isPrimaryVeryLight = (0.2126f * primary.red + 0.7152f * primary.green + 0.0722f * primary.blue) > 0.75f
    val onPrimaryColor = if (isPrimaryVeryLight) Color.Black else Color.White

    return lightColorScheme(
        primary = primary,
        onPrimary = onPrimaryColor,
        primaryContainer = primary.copy(alpha = 0.14f),
        onPrimaryContainer = primary,
        secondary = secondary,
        onSecondary = Color.White,
        secondaryContainer = secondary.copy(alpha = 0.14f),
        onSecondaryContainer = secondary,
        tertiary = tertiary,
        onTertiary = Color.White,
        background = bg,
        onBackground = Color(0xFF111827),
        surface = surface,
        onSurface = Color(0xFF111827),
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = Color(0xFF4B5563),
        outline = Color(0xFFD1D5DB),
        outlineVariant = Color(0xFFE5E7EB)
    )
}

object AppThemes {
    // 1. সোনালি ক্লাসিক (Royal Gold / Default)
    val RoyalGold = AppThemeData(
        id = "royal_gold",
        nameBn = "সোনালি ক্লাসিক",
        descriptionBn = "খাটি সোনালি আভা ও মার্জিত ক্যানভাস",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = GoldPrimary,
            secondary = SoftLavender,
            tertiary = GoldLight
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFFB8860B),
            secondary = Color(0xFF8B5A2B),
            tertiary = Color(0xFF9E6B20),
            bg = Color(0xFFF9F6EE),
            surfaceVariant = Color(0xFFF2EFE6)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = GoldPrimary,
        darkPreviewSecondary = SoftLavender,
        lightPreviewBg = Color(0xFFF9F6EE),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFFB8860B),
        lightPreviewSecondary = Color(0xFF8B5A2B)
    )

    // 2. রাজকীয় বেগুনি (Royal Purple)
    val RoyalPurple = AppThemeData(
        id = "royal_purple",
        nameBn = "রাজকীয় বেগুনি",
        descriptionBn = "অভিজাত রাজকীয় ভেলভেট বেগুনি আভা",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFC084FC),
            secondary = Color(0xFFEC4899),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF7E22CE),
            secondary = Color(0xFFBE185D),
            bg = Color(0xFFFAF5FF),
            surfaceVariant = Color(0xFFF3E8FF)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFC084FC),
        darkPreviewSecondary = Color(0xFFEC4899),
        lightPreviewBg = Color(0xFFFAF5FF),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF7E22CE),
        lightPreviewSecondary = Color(0xFFBE185D)
    )

    // 3. নীল সমুদ্র (Ocean Blue)
    val OceanBlue = AppThemeData(
        id = "ocean_blue",
        nameBn = "নীল সমুদ্র",
        descriptionBn = "গভীর সাগরের গাঢ় নীল ও সাফায়ার আভা",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFF818CF8),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF0284C7),
            secondary = Color(0xFF4338CA),
            bg = Color(0xFFF0F9FF),
            surfaceVariant = Color(0xFFE0F2FE)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFF38BDF8),
        darkPreviewSecondary = Color(0xFF818CF8),
        lightPreviewBg = Color(0xFFF0F9FF),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF0284C7),
        lightPreviewSecondary = Color(0xFF4338CA)
    )

    // 4. প্রকৃতি সবুজ (Nature Green)
    val NatureGreen = AppThemeData(
        id = "nature_green",
        nameBn = "প্রকৃতি সবুজ",
        descriptionBn = "সতেজ সবুজ পাতা ও বনের প্রশান্তি",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFF34D399),
            secondary = Color(0xFFA3E635),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF15803D),
            secondary = Color(0xFF4D7C0F),
            bg = Color(0xFFF0FDF4),
            surfaceVariant = Color(0xFFDCFCE7)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFF34D399),
        darkPreviewSecondary = Color(0xFFA3E635),
        lightPreviewBg = Color(0xFFF0FDF4),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF15803D),
        lightPreviewSecondary = Color(0xFF4D7C0F)
    )

    // 5. গোধূলি গোলাপি (Twilight Pink)
    val TwilightPink = AppThemeData(
        id = "twilight_pink",
        nameBn = "গোধূলি গোলাপি",
        descriptionBn = "নরম গোধূলির রিমঝিম গোলাপি রঙ",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFF472B6),
            secondary = Color(0xFFFB923C),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFFDB2777),
            secondary = Color(0xFFC2410C),
            bg = Color(0xFFFFF1F2),
            surfaceVariant = Color(0xFFFFE4E6)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFF472B6),
        darkPreviewSecondary = Color(0xFFFB923C),
        lightPreviewBg = Color(0xFFFFF1F2),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFFDB2777),
        lightPreviewSecondary = Color(0xFFC2410C)
    )

    // 6. মিনিমাল মনোক্রোম (Minimal Monochrome)
    val MinimalMonochrome = AppThemeData(
        id = "minimal_monochrome",
        nameBn = "মিনিমাল মনোক্রোম",
        descriptionBn = "পরিচ্ছন্ন, আধুনিক রূপালি ও চারকোল ক্যানভাস",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFE2E8F0),
            secondary = Color(0xFF94A3B8),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF0F172A),
            secondary = Color(0xFF475569),
            bg = Color(0xFFFAFAFA),
            surfaceVariant = Color(0xFFF1F5F9)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFE2E8F0),
        darkPreviewSecondary = Color(0xFF94A3B8),
        lightPreviewBg = Color(0xFFFAFAFA),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF0F172A),
        lightPreviewSecondary = Color(0xFF475569)
    )

    // 7. মধ্যরাতের কালি (Midnight Ink)
    val MidnightInk = AppThemeData(
        id = "midnight_ink",
        nameBn = "মধ্যরাতের কালি",
        descriptionBn = "গাঢ় কালির ক্যানভাস ও কুল স্লেট এক্সেন্ট",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFF94A3B8),
            secondary = Color(0xFF38BDF8),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF1E293B),
            secondary = Color(0xFF0284C7),
            bg = Color(0xFFF1F5F9),
            surfaceVariant = Color(0xFFE2E8F0)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFF94A3B8),
        darkPreviewSecondary = Color(0xFF38BDF8),
        lightPreviewBg = Color(0xFFF1F5F9),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF1E293B),
        lightPreviewSecondary = Color(0xFF0284C7)
    )

    // 8. সোনালি ঊষা (Golden Dawn)
    val GoldenDawn = AppThemeData(
        id = "golden_dawn",
        nameBn = "সোনালি ঊষা",
        descriptionBn = "কমলা-সোনালি ভোরের স্নিগ্ধ উজ্জ্বল আভা",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFFB8C00),
            secondary = Color(0xFFFFD54F),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFFD97706),
            secondary = Color(0xFFB45309),
            bg = Color(0xFFFFFBEB),
            surfaceVariant = Color(0xFFFEF3C7)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFFB8C00),
        darkPreviewSecondary = Color(0xFFFFD54F),
        lightPreviewBg = Color(0xFFFFFBEB),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFFD97706),
        lightPreviewSecondary = Color(0xFFB45309)
    )

    // 9. পান্না রাত (Emerald Night)
    val EmeraldNight = AppThemeData(
        id = "emerald_night",
        nameBn = "পান্না রাত",
        descriptionBn = "গভীর রাতে ঝিলমিল পান্না ও সবুজ রত্ন",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFF10B981),
            secondary = Color(0xFF34D399),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF047857),
            secondary = Color(0xFF059669),
            bg = Color(0xFFECFDF5),
            surfaceVariant = Color(0xD1D1FAE5)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFF10B981),
        darkPreviewSecondary = Color(0xFF34D399),
        lightPreviewBg = Color(0xFFECFDF5),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF047857),
        lightPreviewSecondary = Color(0xFF059669)
    )

    // 10. চেরি ব্লসম (Cherry Blossom)
    val CherryBlossom = AppThemeData(
        id = "cherry_blossom",
        nameBn = "চেরি ব্লসম",
        descriptionBn = "চেরি ফুলের নরম গোলাপি ও মেরুন রূপ",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFF48FB1),
            secondary = Color(0xFFEC4899),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFFBE185D),
            secondary = Color(0xFF9D174D),
            bg = Color(0xFFFFF0F5),
            surfaceVariant = Color(0xFFFFD6E7)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFF48FB1),
        darkPreviewSecondary = Color(0xFFEC4899),
        lightPreviewBg = Color(0xFFFFF0F5),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFFBE185D),
        lightPreviewSecondary = Color(0xFF9D174D)
    )

    // 11. রক্তিম গোধূলি (Crimson Sunset)
    val CrimsonSunset = AppThemeData(
        id = "crimson_sunset",
        nameBn = "রক্তিম গোধূলি",
        descriptionBn = "গোধূলি বেলার লালচে রক্তিম আকাশের সৌন্দর্য",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFF87171),
            secondary = Color(0xFFFBBF24),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFFDC2626),
            secondary = Color(0xFFD97706),
            bg = Color(0xFFFEF2F2),
            surfaceVariant = Color(0xFFFEE2E2)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFF87171),
        darkPreviewSecondary = Color(0xFFFBBF24),
        lightPreviewBg = Color(0xFFFEF2F2),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFFDC2626),
        lightPreviewSecondary = Color(0xFFD97706)
    )

    // 12. তুষার শুভ্র (Snow White)
    val SnowWhite = AppThemeData(
        id = "snow_white",
        nameBn = "তুষার শুভ্র",
        descriptionBn = "শুভ্র তুষারপাত ও আইস-ব্লু রঙের শীতলতা",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFFA5F3FC),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF0284C7),
            secondary = Color(0xFF0369A1),
            bg = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFFE2E8F0)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFF38BDF8),
        darkPreviewSecondary = Color(0xFFA5F3FC),
        lightPreviewBg = Color(0xFFF8FAFC),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF0284C7),
        lightPreviewSecondary = Color(0xFF0369A1)
    )

    // 13. অ্যাম্বার নাইট (Amber Night)
    val AmberNight = AppThemeData(
        id = "amber_night",
        nameBn = "অ্যাম্বার নাইট",
        descriptionBn = "উষ্ণ মধু-সোনালি আলো ও গাঢ় রাতের আবহ",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFFBBF24),
            secondary = Color(0xFFF59E0B),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFFB45309),
            secondary = Color(0xFFD97706),
            bg = Color(0xFFFEF3C7),
            surfaceVariant = Color(0xFFFDE68A)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFFBBF24),
        darkPreviewSecondary = Color(0xFFF59E0B),
        lightPreviewBg = Color(0xFFFEF3C7),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFFB45309),
        lightPreviewSecondary = Color(0xFFD97706)
    )

    // 14. মহাসাগর নীল (Deep Ocean)
    val DeepOcean = AppThemeData(
        id = "deep_ocean",
        nameBn = "মহাসাগর নীল",
        descriptionBn = "গভীর মহাসাগরের ফিরোজা ও সাইয়ান ঢেউ",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFF22D3EE),
            secondary = Color(0xFF38BDF8),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF0891B2),
            secondary = Color(0xFF0284C7),
            bg = Color(0xFFECFEFF),
            surfaceVariant = Color(0xFFCFFAFE)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFF22D3EE),
        darkPreviewSecondary = Color(0xFF38BDF8),
        lightPreviewBg = Color(0xFFECFEFF),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF0891B2),
        lightPreviewSecondary = Color(0xFF0284C7)
    )

    // 15. লিলাক স্বপ্ন (Lilac Dream)
    val LilacDream = AppThemeData(
        id = "lilac_dream",
        nameBn = "লিলাক স্বপ্ন",
        descriptionBn = "স্বপ্নের মতো নরম ল্যাভেন্ডার ও বেগুনি আলো",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFE9D5FF),
            secondary = Color(0xFFF472B6),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF7E22CE),
            secondary = Color(0xFFBE185D),
            bg = Color(0xFFF3E8FF),
            surfaceVariant = Color(0xFFE9D5FF)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFE9D5FF),
        darkPreviewSecondary = Color(0xFFF472B6),
        lightPreviewBg = Color(0xFFF3E8FF),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF7E22CE),
        lightPreviewSecondary = Color(0xFFBE185D)
    )

    // 16. কার্বন গ্রে (Carbon Gray)
    val CarbonGray = AppThemeData(
        id = "carbon_gray",
        nameBn = "কার্বন গ্রে",
        descriptionBn = "গভীর কার্বন ব্ল্যাক ও কুল স্লেট ফিনিশ",
        isDark = true,
        darkColorScheme = buildDarkColorScheme(
            primary = Color(0xFFCBD5E1),
            secondary = Color(0xFF94A3B8),
            surface = Color(0xFF121212)
        ),
        lightColorScheme = buildLightColorScheme(
            primary = Color(0xFF334155),
            secondary = Color(0xFF475569),
            bg = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFFE2E8F0)
        ),
        darkPreviewBg = Color(0xFF000000),
        darkPreviewCard = Color(0xFF121212),
        darkPreviewPrimary = Color(0xFFCBD5E1),
        darkPreviewSecondary = Color(0xFF94A3B8),
        lightPreviewBg = Color(0xFFF8FAFC),
        lightPreviewCard = Color(0xFFFFFFFF),
        lightPreviewPrimary = Color(0xFF334155),
        lightPreviewSecondary = Color(0xFF475569)
    )

    val ALL: List<AppThemeData> = listOf(
        RoyalGold,
        RoyalPurple,
        OceanBlue,
        NatureGreen,
        TwilightPink,
        MinimalMonochrome,
        MidnightInk,
        GoldenDawn,
        EmeraldNight,
        CherryBlossom,
        CrimsonSunset,
        SnowWhite,
        AmberNight,
        DeepOcean,
        LilacDream,
        CarbonGray
    )

    fun getThemeById(id: String, customThemes: List<AppThemeData> = emptyList()): AppThemeData {
        return customThemes.find { it.id == id }
            ?: ALL.find { it.id == id }
            ?: RoyalGold
    }
}
