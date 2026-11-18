package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.ui.text.font.FontFamily
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.util.Locale

data class ParsedFontMetadata(
    val familyName: String = "",
    val subfamilyName: String = "",
    val fullName: String = "",
    val description: String = "",
    val weightClass: Int = 400,
    val sFamilyClass: Int = 0,
    val panoseFamilyType: Int = 0,
    val panoseWeight: Int = 0,
    val panoseStrokeVariation: Int = 0,
    val isItalic: Boolean = false
)

sealed class FontImportSingleResult {
    data class Imported(val option: BengaliFontOption) : FontImportSingleResult()
    data class Duplicate(val fileName: String, val existingName: String) : FontImportSingleResult()
    data class Invalid(val fileName: String, val reason: String) : FontImportSingleResult()
}

data class FontImportBatchResult(
    val imported: List<BengaliFontOption>,
    val duplicates: List<FontImportSingleResult.Duplicate>,
    val invalid: List<FontImportSingleResult.Invalid>
)

object FontImportManager {

    private const val TAG = "FontImportManager"
    private const val CUSTOM_FONTS_DIR = "custom_fonts"
    private const val METADATA_FILE_NAME = "custom_fonts_meta.json"
    private const val MAX_FONT_FILE_BYTES = 25 * 1024 * 1024 // 25 MB safety limit

    val SUPPORTED_CATEGORIES = listOf("ক্যালিগ্রাফি", "আর্টিস্টিক", "ডেকোরেটিভ")

    fun getCustomFontsDir(context: Context): File {
        val dir = File(context.filesDir, CUSTOM_FONTS_DIR)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    private fun getMetadataFile(context: Context): File {
        return File(getCustomFontsDir(context), METADATA_FILE_NAME)
    }

    /**
     * Validates SFNT magic bytes and table directory bounds for TTF/OTF font binaries.
     */
    fun isValidFontBytes(bytes: ByteArray): Boolean {
        if (bytes.size < 12 || bytes.size > MAX_FONT_FILE_BYTES) return false

        val isTrueType = bytes[0] == 0x00.toByte() && bytes[1] == 0x01.toByte() &&
            bytes[2] == 0x00.toByte() && bytes[3] == 0x00.toByte()
        val isAppleTrue = bytes[0] == 't'.code.toByte() && bytes[1] == 'r'.code.toByte() &&
            bytes[2] == 'u'.code.toByte() && bytes[3] == 'e'.code.toByte()
        val isOpenTypeCff = bytes[0] == 'O'.code.toByte() && bytes[1] == 'T'.code.toByte() &&
            bytes[2] == 'T'.code.toByte() && bytes[3] == 'O'.code.toByte()

        if (!isTrueType && !isAppleTrue && !isOpenTypeCff) return false

        val numTables = readUInt16(bytes, 4)
        if (numTables !in 1..200) return false
        if (12 + numTables * 16 > bytes.size) return false

        val tableTags = mutableSetOf<String>()
        for (i in 0 until numTables) {
            val recOffset = 12 + i * 16
            val tag = readAsciiTag(bytes, recOffset)
            val offset = readUInt32(bytes, recOffset + 8)
            val length = readUInt32(bytes, recOffset + 12)
            if (offset < 0 || length < 0 || offset + length > bytes.size) {
                return false
            }
            tableTags.add(tag)
        }

        // Must have at least one core SFNT font table
        return tableTags.contains("head") || tableTags.contains("name") ||
            tableTags.contains("OS/2") || tableTags.contains("cmap") || tableTags.contains("maxp")
    }

    fun detectFontExtension(bytes: ByteArray, fileName: String): String {
        if (bytes.size >= 4 &&
            bytes[0] == 'O'.code.toByte() && bytes[1] == 'T'.code.toByte() &&
            bytes[2] == 'T'.code.toByte() && bytes[3] == 'O'.code.toByte()
        ) {
            return "otf"
        }
        val lower = fileName.lowercase(Locale.ROOT)
        return if (lower.endsWith(".otf")) "otf" else "ttf"
    }

    /**
     * Parses OpenType/TrueType `name`, `OS/2`, and `post` tables safely in pure Kotlin.
     */
    fun parseFontMetadata(bytes: ByteArray): ParsedFontMetadata? {
        if (!isValidFontBytes(bytes)) return null

        val numTables = readUInt16(bytes, 4)
        val tables = mutableMapOf<String, Pair<Int, Int>>()
        for (i in 0 until numTables) {
            val recOffset = 12 + i * 16
            val tag = readAsciiTag(bytes, recOffset)
            val offset = readUInt32(bytes, recOffset + 8).toInt()
            val length = readUInt32(bytes, recOffset + 12).toInt()
            if (offset >= 0 && length >= 0 && offset + length <= bytes.size) {
                tables[tag] = offset to length
            }
        }

        var familyName = ""
        var preferredFamilyName = ""
        var subfamilyName = ""
        var preferredSubfamilyName = ""
        var fullName = ""
        var description = ""

        tables["name"]?.let { (tOffset, tLength) ->
            if (tLength >= 6) {
                val count = readUInt16(bytes, tOffset + 2)
                val stringStorageOffset = readUInt16(bytes, tOffset + 4)
                val storageBase = tOffset + stringStorageOffset
                for (i in 0 until count) {
                    val recOff = tOffset + 6 + i * 12
                    if (recOff + 12 > tOffset + tLength) break
                    val platformId = readUInt16(bytes, recOff)
                    val nameId = readUInt16(bytes, recOff + 6)
                    val strLen = readUInt16(bytes, recOff + 8)
                    val strOff = readUInt16(bytes, recOff + 10)
                    val absStart = storageBase + strOff
                    if (strLen <= 0 || absStart < 0 || absStart + strLen > tOffset + tLength || absStart + strLen > bytes.size) {
                        continue
                    }

                    val decoded = decodeNameString(bytes, absStart, strLen, platformId)?.trim().orEmpty()
                    if (decoded.isBlank()) continue

                    when (nameId) {
                        16 -> if (preferredFamilyName.isBlank() || platformId == 3) preferredFamilyName = decoded
                        1 -> if (familyName.isBlank() || platformId == 3) familyName = decoded
                        17 -> if (preferredSubfamilyName.isBlank() || platformId == 3) preferredSubfamilyName = decoded
                        2 -> if (subfamilyName.isBlank() || platformId == 3) subfamilyName = decoded
                        4 -> if (fullName.isBlank() || platformId == 3) fullName = decoded
                        10 -> if (description.isBlank() || platformId == 3) description = decoded
                    }
                }
            }
        }

        var weightClass = 400
        var sFamilyClass = 0
        var panoseFamilyType = 0
        var panoseWeight = 0
        var panoseStrokeVariation = 0

        tables["OS/2"]?.let { (tOffset, tLength) ->
            if (tLength >= 6) {
                weightClass = readUInt16(bytes, tOffset + 4)
            }
            if (tLength >= 32) {
                sFamilyClass = readUInt16(bytes, tOffset + 30)
            }
            if (tLength >= 42) {
                panoseFamilyType = bytes[tOffset + 32].toInt() and 0xFF
                panoseWeight = bytes[tOffset + 34].toInt() and 0xFF
                panoseStrokeVariation = bytes[tOffset + 39].toInt() and 0xFF
            }
        }

        var isItalic = false
        tables["post"]?.let { (tOffset, tLength) ->
            if (tLength >= 8) {
                val italicFixed = readInt32(bytes, tOffset + 4)
                if (italicFixed != 0) isItalic = true
            }
        }

        val resolvedSubfamily = preferredSubfamilyName.ifBlank { subfamilyName }
        if (resolvedSubfamily.contains("italic", ignoreCase = true) ||
            resolvedSubfamily.contains("oblique", ignoreCase = true)
        ) {
            isItalic = true
        }

        return ParsedFontMetadata(
            familyName = preferredFamilyName.ifBlank { familyName },
            subfamilyName = resolvedSubfamily,
            fullName = fullName,
            description = description,
            weightClass = weightClass,
            sFamilyClass = sFamilyClass,
            panoseFamilyType = panoseFamilyType,
            panoseWeight = panoseWeight,
            panoseStrokeVariation = panoseStrokeVariation,
            isItalic = isItalic
        )
    }

    /**
     * Automatically classifies an imported Bengali font into one of the app's existing groups:
     * "ক্যালিগ্রাফি", "আর্টিস্টিক", or "ডেকোরেটিভ".
     */
    fun classifyFontCategory(meta: ParsedFontMetadata?, fileName: String): String {
        val searchCorpus = buildString {
            if (meta != null) {
                append(meta.familyName).append(' ')
                append(meta.fullName).append(' ')
                append(meta.subfamilyName).append(' ')
                append(meta.description).append(' ')
            }
            append(fileName)
        }.lowercase(Locale.ROOT)

        var calligraphyScore = 0
        var artisticScore = 0
        var decorativeScore = 0

        val calligraphyKeywords = listOf(
            "callig", "kalig", "script", "lipi", "charu", "brush", "quill", "kalam",
            "lekha", "monjari", "ankon", "bakano", "tuli", "cursive", "alpona",
            "ichamati", "mahdi", "badhon", "shakuntala", "mohinee", "tahmid",
            "ক্যালিগ্রাফি", "লিপি", "তুলি", "কলম"
        )
        val decorativeKeywords = listOf(
            "decor", "display", "poster", "headline", "banner", "title", "bold",
            "heavy", "black", "extra", "shadow", "outline", "3d", "retro", "modern",
            "stylish", "geometric", "block", "bornomala", "bornali", "basanti",
            "ruposhi", "orin", "sharifa", "effort", "utshob", "bijoy",
            "ডেকোরেটিভ", "ডিসপ্লে", "বোল্ড"
        )
        val artisticKeywords = listOf(
            "art", "hand", "pen", "note", "chirkut", "kobita", "poetry", "kabbo",
            "kavya", "atma", "kalpana", "bonolota", "gaudhuli", "shobuj", "noluya",
            "megh", "bristi", "nodi", "akash", "phool", "swapno", "srabon",
            "hemonto", "shishir", "rodela", "soft", "light", "thin", "solaiman",
            "kalpurush", "nikosh", "siyam", "mukti", "আর্টিস্টিক", "কবিতা", "হাতে", "স্বপ্ন"
        )

        for (kw in calligraphyKeywords) {
            if (searchCorpus.contains(kw)) calligraphyScore += 3
        }
        for (kw in decorativeKeywords) {
            if (searchCorpus.contains(kw)) decorativeScore += 3
        }
        for (kw in artisticKeywords) {
            if (searchCorpus.contains(kw)) artisticScore += 3
        }

        if (meta != null) {
            val familyClassHigh = (meta.sFamilyClass ushr 8) and 0xFF
            when (familyClassHigh) {
                10 -> calligraphyScore += 4 // IBM Script / Calligraphy
                12 -> decorativeScore += 4  // IBM Ornamental / Decorative
            }

            when (meta.panoseFamilyType) {
                3 -> calligraphyScore += 3  // Latin Script / Handwritten
                4 -> decorativeScore += 3   // Latin Decorative
            }

            if (meta.weightClass >= 650 || meta.panoseWeight >= 7) {
                decorativeScore += 2
            } else if (meta.weightClass in 100..350) {
                artisticScore += 2
            }

            if (meta.isItalic) {
                calligraphyScore += 2
                artisticScore += 1
            }

            if (meta.panoseStrokeVariation >= 6) {
                calligraphyScore += 2
            }
        }

        return when {
            calligraphyScore >= decorativeScore && calligraphyScore >= artisticScore && calligraphyScore > 0 ->
                "ক্যালিগ্রাফি"
            decorativeScore >= calligraphyScore && decorativeScore >= artisticScore && decorativeScore > 0 ->
                "ডেকোরেটিভ"
            artisticScore > 0 ->
                "আর্টিস্টিক"
            else -> {
                // Default literary Bengali font falls naturally into Artistic
                "আর্টিস্টিক"
            }
        }
    }

    fun resolveDisplayName(meta: ParsedFontMetadata?, fileName: String): String {
        val rawMetaName = meta?.familyName?.trim().orEmpty()
        val hasUsableMetaName = rawMetaName.length >= 2 &&
            !rawMetaName.equals("new font", ignoreCase = true) &&
            !rawMetaName.equals("untitled", ignoreCase = true) &&
            rawMetaName.any { it.isLetterOrDigit() || it in '\u0980'..'\u09FF' }

        if (hasUsableMetaName) {
            val sub = meta?.subfamilyName?.trim().orEmpty()
            return if (sub.isNotBlank() &&
                !sub.equals("Regular", ignoreCase = true) &&
                !sub.equals("Normal", ignoreCase = true) &&
                !rawMetaName.contains(sub, ignoreCase = true)
            ) {
                "$rawMetaName $sub"
            } else {
                rawMetaName
            }
        }

        val withoutExt = fileName.substringBeforeLast('.')
        val cleaned = withoutExt
            .replace('_', ' ')
            .replace('-', ' ')
            .replace(Regex("\\s+"), " ")
            .trim()
        return cleaned.ifBlank { "Custom Bengali Font" }
    }

    /**
     * Normalizes font names/keys for duplicate detection (e.g., "Anupam_Mahdi.ttf" == "Anupam Mahdi").
     */
    fun normalizeFontIdentifier(raw: String): String {
        val withoutExt = raw
            .replace(Regex("\\.(ttf|otf|ttc)$", RegexOption.IGNORE_CASE), "")
            .lowercase(Locale.ROOT)
        val stripped = withoutExt.replace(Regex("[^a-z0-9\u0980-\u09FF]"), "")
        return stripped
            .removeSuffix("unicode")
            .removeSuffix("regular")
            .removeSuffix("normal")
            .ifBlank { stripped }
    }

    fun sha256Hex(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun computeBuiltInAssetHashes(context: Context, builtInFonts: List<BengaliFontOption>): Map<String, String> {
        val hashes = mutableMapOf<String, String>()
        for (option in builtInFonts) {
            if (option.assetFileName.isBlank()) continue
            try {
                context.assets.open("fonts/${option.assetFileName}").use { stream ->
                    hashes[sha256Hex(stream.readBytes())] = option.name
                }
            } catch (e: Exception) {
                Log.w(TAG, "Unable to hash asset font ${option.assetFileName}", e)
            }
        }
        return hashes
    }

    /**
     * Pure-file testable import pipeline: validates binary font, rejects duplicates by SHA-256 or normalized name,
     * auto-classifies into app categories, writes file to [storageDir], and returns [FontImportSingleResult].
     */
    fun importFontBytes(
        bytes: ByteArray,
        fileName: String,
        storageDir: File,
        existingFonts: List<BengaliFontOption>,
        existingAssetHashes: Map<String, String>
    ): FontImportSingleResult {
        if (!isValidFontBytes(bytes)) {
            return FontImportSingleResult.Invalid(
                fileName = fileName,
                reason = "সঠিক .ttf বা .otf বাংলা ফন্ট ফাইল নির্বাচন করুন"
            )
        }

        val contentHash = sha256Hex(bytes)

        // 1. Check exact binary hash match against existing custom fonts
        val existingCustomByHash = existingFonts.firstOrNull { it.contentHash == contentHash }
        if (existingCustomByHash != null) {
            return FontImportSingleResult.Duplicate(
                fileName = fileName,
                existingName = existingCustomByHash.name
            )
        }

        // 2. Check exact binary hash match against built-in asset fonts
        val matchedBuiltInName = existingAssetHashes[contentHash]
        if (matchedBuiltInName != null) {
            return FontImportSingleResult.Duplicate(
                fileName = fileName,
                existingName = matchedBuiltInName
            )
        }

        val meta = parseFontMetadata(bytes)
        val displayName = resolveDisplayName(meta, fileName)
        val normalizedDisplay = normalizeFontIdentifier(displayName)
        val normalizedFile = normalizeFontIdentifier(fileName)

        // 3. Check normalized name / key match against all existing fonts
        val duplicateByName = existingFonts.firstOrNull { existing ->
            val existingNormName = normalizeFontIdentifier(existing.name.substringBefore(" ("))
            val existingNormParen = if (existing.name.contains(" (")) {
                normalizeFontIdentifier(existing.name.substringAfter(" (").substringBeforeLast(")"))
            } else ""
            val existingNormFull = normalizeFontIdentifier(existing.name)
            val existingNormKey = normalizeFontIdentifier(existing.key)
            val existingNormAsset = normalizeFontIdentifier(existing.assetFileName)
            normalizedDisplay == existingNormName ||
                (existingNormParen.isNotBlank() && normalizedDisplay == existingNormParen) ||
                normalizedDisplay == existingNormFull ||
                normalizedDisplay == existingNormKey ||
                normalizedDisplay == existingNormAsset ||
                (normalizedFile.isNotBlank() && (
                    normalizedFile == existingNormName ||
                        (existingNormParen.isNotBlank() && normalizedFile == existingNormParen) ||
                        normalizedFile == existingNormKey ||
                        normalizedFile == existingNormAsset
                    ))
        }
        if (duplicateByName != null) {
            return FontImportSingleResult.Duplicate(
                fileName = fileName,
                existingName = duplicateByName.name
            )
        }

        val category = classifyFontCategory(meta, fileName)
        val ext = detectFontExtension(bytes, fileName)
        val safeSlug = normalizeFontIdentifier(displayName).take(24).ifBlank { "font" }
        val uniqueKey = "custom_${safeSlug}_${contentHash.take(8)}"

        if (!storageDir.exists()) storageDir.mkdirs()
        val targetFile = File(storageDir, "$uniqueKey.$ext")
        targetFile.writeBytes(bytes)

        val loadedFontFamily = try {
            val typeface = Typeface.createFromFile(targetFile)
            if (typeface != null) FontFamily(typeface) else FontFamily.Default
        } catch (e: Throwable) {
            Log.w(TAG, "Typeface creation fallback to Default in current runtime", e)
            FontFamily.Default
        }

        val option = BengaliFontOption(
            key = uniqueKey,
            name = displayName,
            category = category,
            description = "ব্যবহারকারীর ইমপোর্টকৃত বাংলা $category ফন্ট",
            fontFamily = loadedFontFamily,
            assetFileName = targetFile.name,
            resId = 0,
            isCustom = true,
            customFilePath = targetFile.absolutePath,
            contentHash = contentHash
        )
        return FontImportSingleResult.Imported(option)
    }

    fun loadPersistedCustomFonts(context: Context): List<BengaliFontOption> {
        val metaFile = getMetadataFile(context)
        if (!metaFile.exists()) return emptyList()
        return try {
            val array = JSONArray(metaFile.readText())
            val list = mutableListOf<BengaliFontOption>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val key = obj.optString("key", "")
                val name = obj.optString("name", "")
                val category = obj.optString("category", "আর্টিস্টিক")
                    .let { if (it in SUPPORTED_CATEGORIES) it else "আর্টিস্টিক" }
                val filePath = obj.optString("customFilePath", "")
                val contentHash = obj.optString("contentHash", "")
                val file = File(filePath)
                if (key.isBlank() || name.isBlank() || !file.exists() || file.length() == 0L) continue

                val fontFamily = FontLoader.loadFontFamilyFromFile(key, file) ?: FontFamily.Default
                list.add(
                    BengaliFontOption(
                        key = key,
                        name = name,
                        category = category,
                        description = obj.optString("description", "ব্যবহারকারীর ইমপোর্টকৃত ফন্ট"),
                        fontFamily = fontFamily,
                        assetFileName = file.name,
                        resId = 0,
                        isCustom = true,
                        customFilePath = file.absolutePath,
                        contentHash = contentHash.ifBlank { null }
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error loading persisted custom fonts", e)
            emptyList()
        }
    }

    fun savePersistedCustomFonts(context: Context, customFonts: List<BengaliFontOption>) {
        try {
            val array = JSONArray()
            for (font in customFonts) {
                if (!font.isCustom) continue
                val obj = JSONObject().apply {
                    put("key", font.key)
                    put("name", font.name)
                    put("category", font.category)
                    put("description", font.description)
                    put("customFilePath", font.customFilePath ?: "")
                    put("contentHash", font.contentHash ?: "")
                }
                array.put(obj)
            }
            getMetadataFile(context).writeText(array.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Error saving persisted custom fonts", e)
        }
    }

    fun resolveUriFileName(context: Context, uri: Uri): String {
        var name: String? = null
        if (uri.scheme == "content") {
            try {
                context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
                    ?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (idx >= 0) name = cursor.getString(idx)
                        }
                    }
            } catch (_: Exception) {
            }
        }
        if (name.isNullOrBlank()) {
            name = uri.lastPathSegment?.substringAfterLast('/')
        }
        return name?.takeIf { it.isNotBlank() } ?: "imported_font.ttf"
    }

    fun readBytesFromUri(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to read font bytes from URI: $uri", e)
            null
        }
    }

    private fun readUInt16(bytes: ByteArray, offset: Int): Int {
        if (offset + 1 >= bytes.size) return 0
        return ((bytes[offset].toInt() and 0xFF) shl 8) or (bytes[offset + 1].toInt() and 0xFF)
    }

    private fun readUInt32(bytes: ByteArray, offset: Int): Long {
        if (offset + 3 >= bytes.size) return -1L
        return ((bytes[offset].toLong() and 0xFFL) shl 24) or
            ((bytes[offset + 1].toLong() and 0xFFL) shl 16) or
            ((bytes[offset + 2].toLong() and 0xFFL) shl 8) or
            (bytes[offset + 3].toLong() and 0xFFL)
    }

    private fun readInt32(bytes: ByteArray, offset: Int): Int {
        if (offset + 3 >= bytes.size) return 0
        return ((bytes[offset].toInt() and 0xFF) shl 24) or
            ((bytes[offset + 1].toInt() and 0xFF) shl 16) or
            ((bytes[offset + 2].toInt() and 0xFF) shl 8) or
            (bytes[offset + 3].toInt() and 0xFF)
    }

    private fun readAsciiTag(bytes: ByteArray, offset: Int): String {
        if (offset + 3 >= bytes.size) return ""
        return String(bytes, offset, 4, Charsets.US_ASCII)
    }

    private fun decodeNameString(bytes: ByteArray, offset: Int, length: Int, platformId: Int): String? {
        return try {
            when (platformId) {
                0, 3 -> String(bytes, offset, length, Charsets.UTF_16BE)
                else -> String(bytes, offset, length, Charsets.ISO_8859_1)
            }
        } catch (_: Exception) {
            null
        }
    }
}
