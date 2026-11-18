package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * Robust FontLoader utility that programmatically verifies the existence of custom TTF/OTF
 * files in the assets folder and maps them to Jetpack Compose [FontFamily] for immediate
 * application across the app's UI.
 */
object FontLoader {

    private const val TAG = "FontLoader"
    private const val DEFAULT_ASSET_FOLDER = "fonts"

    // Thread-safe caches to prevent redundant IO operations and Typeface re-instantiation
    private val fontFamilyCache = ConcurrentHashMap<String, FontFamily>()
    private val assetExistenceCache = ConcurrentHashMap<String, Boolean>()

    /**
     * Programmatically verifies if a TTF/OTF file exists in the specified assets directory.
     *
     * @param context Application context
     * @param fileName File name (e.g., "galada.ttf")
     * @param folderPath Relative directory in assets (defaults to "fonts")
     * @return True if file exists and can be opened; false otherwise
     */
    fun verifyAssetFontExists(
        context: Context,
        fileName: String,
        folderPath: String = DEFAULT_ASSET_FOLDER
    ): Boolean {
        val cacheKey = if (folderPath.isBlank()) fileName else "$folderPath/$fileName"
        assetExistenceCache[cacheKey]?.let { return it }

        val exists = try {
            context.assets.open(cacheKey).use { true }
        } catch (e: IOException) {
            Log.w(TAG, "Asset font file not found or unreadable: $cacheKey", e)
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking asset font presence: $cacheKey", e)
            false
        }

        assetExistenceCache[cacheKey] = exists
        return exists
    }

    /**
     * Programmatically loads a custom TTF/OTF font file from assets and converts it to a Compose [FontFamily].
     * Falls back to a resource ID font or FontFamily.Default if the asset is missing or invalid.
     *
     * @param context Application context
     * @param assetFileName Font file name in assets (e.g. "galada.ttf")
     * @param resIdFallback Resource ID in res/font/ as fallback
     * @param folderPath Subfolder in assets (defaults to "fonts")
     * @return Valid Compose [FontFamily]
     */
    fun loadFontFamilyFromAsset(
        context: Context,
        assetFileName: String,
        resIdFallback: Int? = null,
        folderPath: String = DEFAULT_ASSET_FOLDER
    ): FontFamily {
        val cacheKey = if (folderPath.isBlank()) assetFileName else "$folderPath/$assetFileName"
        fontFamilyCache[cacheKey]?.let { return it }

        // 1. Programmatically verify asset file and attempt Typeface creation
        if (verifyAssetFontExists(context, assetFileName, folderPath)) {
            try {
                val typeface = Typeface.createFromAsset(context.assets, cacheKey)
                if (typeface != null) {
                    val fontFamily = FontFamily(typeface)
                    fontFamilyCache[cacheKey] = fontFamily
                    Log.d(TAG, "Successfully loaded and mapped asset font: $cacheKey")
                    return fontFamily
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create Typeface from asset: $cacheKey", e)
            }
        }

        // 2. Fallback to res/font/ resource if available
        if (resIdFallback != null && resIdFallback != 0) {
            try {
                val fontFamily = FontFamily(Font(resIdFallback))
                fontFamilyCache[cacheKey] = fontFamily
                Log.d(TAG, "Fallback to resource font ID: $resIdFallback for $assetFileName")
                return fontFamily
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load resource font ID: $resIdFallback", e)
            }
        }

        // 3. Default fallback
        return FontFamily.Default
    }

    /**
     * Programmatically initializes and pre-caches all custom Bengali font options.
     *
     * @param context Application context
     * @param options List of [BengaliFontOption] entries to verify and load
     */
    fun initFonts(context: Context, options: List<BengaliFontOption>) {
        options.forEach { option ->
            val fontFamily = loadFontFamilyFromAsset(
                context = context,
                assetFileName = option.assetFileName,
                resIdFallback = option.resId
            )
            fontFamilyCache[option.key] = fontFamily
        }
        Log.i(TAG, "FontLoader programmatically initialized ${options.size} custom Bengali fonts.")
    }

    /**
     * Loads a custom TTF/OTF font from an internal File and converts it to a Compose [FontFamily].
     */
    fun loadFontFamilyFromFile(
        key: String,
        fontFile: java.io.File
    ): FontFamily? {
        fontFamilyCache[key]?.let { return it }
        if (!fontFile.exists() || fontFile.length() == 0L) return null
        return try {
            val typeface = Typeface.createFromFile(fontFile)
            if (typeface != null) {
                val fontFamily = FontFamily(typeface)
                fontFamilyCache[key] = fontFamily
                fontFamily
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load custom font from file: ${fontFile.absolutePath}", e)
            null
        }
    }

    /**
     * Registers or updates a cached [FontFamily] for a given font key.
     */
    fun registerFontFamily(key: String, fontFamily: FontFamily) {
        fontFamilyCache[key] = fontFamily
    }

    /**
     * Removes a cached [FontFamily] by key (used when deleting custom imported fonts).
     */
    fun removeFontFamily(key: String) {
        fontFamilyCache.remove(key)
    }

    /**
     * Retrieves a cached Compose [FontFamily] by key.
     *
     * @param key Font option key (e.g., "galada")
     * @return Cached [FontFamily] or null if not yet cached
     */
    fun getFontFamily(key: String): FontFamily? {
        return fontFamilyCache[key] ?: fontFamilyCache["$DEFAULT_ASSET_FOLDER/$key.ttf"]
    }

    /**
     * Clears all internal font and asset existence caches.
     */
    fun clearCache() {
        fontFamilyCache.clear()
        assetExistenceCache.clear()
    }
}

