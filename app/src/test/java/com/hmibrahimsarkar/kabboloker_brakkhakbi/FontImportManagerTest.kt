package com.hmibrahimsarkar.kabboloker_brakkhakbi

import android.content.Context
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.FontPickerSheet
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.FontImportManager
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.FontImportSingleResult
import com.hmibrahimsarkar.kabboloker_brakkhakbi.util.PdfExportHelper
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class FontImportManagerTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FontImportManager.getCustomFontsDir(context).deleteRecursively()
        BengaliFonts.init(context)
    }

    @After
    fun tearDown() {
        FontImportManager.getCustomFontsDir(context).deleteRecursively()
        BengaliFonts.init(context)
    }

    /**
     * Builds a valid minimal OpenType/TrueType SFNT font binary in memory with `head`, `name`, `OS/2`, and `post` tables.
     */
    private fun buildSyntheticSfntFont(
        familyName: String,
        subfamilyName: String = "Regular",
        weightClass: Int = 400,
        sFamilyClass: Int = 0,
        panoseFamilyType: Int = 0,
        italicAngleFixed: Int = 0,
        isOtf: Boolean = false
    ): ByteArray {
        val tables = linkedMapOf<String, ByteArray>()

        // Minimal 54-byte head table
        tables["head"] = ByteArray(54)

        // Build `name` table (Platform 3, Encoding 1, UTF-16BE)
        val nameRecords = listOf(1 to familyName, 2 to subfamilyName, 4 to "$familyName $subfamilyName")
        val stringData = ByteArrayOutputStream()
        val recordData = ByteArrayOutputStream()
        for ((nameId, text) in nameRecords) {
            val encoded = text.toByteArray(Charsets.UTF_16BE)
            val strOffset = stringData.size()
            stringData.write(encoded)
            // platformId=3, encodingId=1, languageId=0x0409, nameId, length, offset
            writeUInt16(recordData, 3)
            writeUInt16(recordData, 1)
            writeUInt16(recordData, 0x0409)
            writeUInt16(recordData, nameId)
            writeUInt16(recordData, encoded.size)
            writeUInt16(recordData, strOffset)
        }
        val nameTable = ByteArrayOutputStream()
        val stringStorageOffset = 6 + nameRecords.size * 12
        writeUInt16(nameTable, 0) // format
        writeUInt16(nameTable, nameRecords.size)
        writeUInt16(nameTable, stringStorageOffset)
        nameTable.write(recordData.toByteArray())
        nameTable.write(stringData.toByteArray())
        tables["name"] = nameTable.toByteArray()

        // Build `OS/2` table (at least 42 bytes for panose)
        val os2 = ByteArray(42)
        os2[4] = ((weightClass ushr 8) and 0xFF).toByte()
        os2[5] = (weightClass and 0xFF).toByte()
        os2[30] = ((sFamilyClass ushr 8) and 0xFF).toByte()
        os2[31] = (sFamilyClass and 0xFF).toByte()
        os2[32] = (panoseFamilyType and 0xFF).toByte()
        tables["OS/2"] = os2

        // Build `post` table (at least 8 bytes for italicAngle)
        val post = ByteArray(32)
        post[4] = ((italicAngleFixed ushr 24) and 0xFF).toByte()
        post[5] = ((italicAngleFixed ushr 16) and 0xFF).toByte()
        post[6] = ((italicAngleFixed ushr 8) and 0xFF).toByte()
        post[7] = (italicAngleFixed and 0xFF).toByte()
        tables["post"] = post

        val numTables = tables.size
        val headerAndDirSize = 12 + numTables * 16
        val out = ByteArrayOutputStream()
        // SFNT version tag
        if (isOtf) {
            out.write("OTTO".toByteArray(Charsets.US_ASCII))
        } else {
            out.write(byteArrayOf(0x00, 0x01, 0x00, 0x00))
        }
        writeUInt16(out, numTables)
        writeUInt16(out, 64)
        writeUInt16(out, 2)
        writeUInt16(out, 0)

        var currentOffset = headerAndDirSize
        for ((tag, data) in tables) {
            out.write(tag.toByteArray(Charsets.US_ASCII))
            writeUInt32(out, 0) // checksum
            writeUInt32(out, currentOffset)
            writeUInt32(out, data.size)
            currentOffset += data.size
        }
        for ((_, data) in tables) {
            out.write(data)
        }
        return out.toByteArray()
    }

    private fun writeUInt16(out: ByteArrayOutputStream, value: Int) {
        out.write((value ushr 8) and 0xFF)
        out.write(value and 0xFF)
    }

    private fun writeUInt32(out: ByteArrayOutputStream, value: Int) {
        out.write((value ushr 24) and 0xFF)
        out.write((value ushr 16) and 0xFF)
        out.write((value ushr 8) and 0xFF)
        out.write(value and 0xFF)
    }

    @Test
    fun isValidFontBytes_rejectsCorruptAndNonFontData_andAcceptsRealAssets() {
        assertFalse(FontImportManager.isValidFontBytes(ByteArray(0)))
        assertFalse(FontImportManager.isValidFontBytes("Not a font file".toByteArray()))
        assertFalse(FontImportManager.isValidFontBytes(byteArrayOf(0x89.toByte(), 'P'.code.toByte(), 'N'.code.toByte(), 'G'.code.toByte())))

        // Real bundled TTF & OTF assets
        val ttfBytes = context.assets.open("fonts/anupam_mahdi.ttf").use { it.readBytes() }
        val otfBytes = context.assets.open("fonts/mahfuz_bornomala.otf").use { it.readBytes() }
        assertTrue(FontImportManager.isValidFontBytes(ttfBytes))
        assertTrue(FontImportManager.isValidFontBytes(otfBytes))
    }

    @Test
    fun parseAndAutoClassify_assignsCalligraphyArtisticDecorativeAutomatically() {
        val calligraphyBytes = buildSyntheticSfntFont(
            familyName = "Charulipi Script",
            sFamilyClass = 10 shl 8 // IBM Script -> Calligraphy
        )
        val decorativeBytes = buildSyntheticSfntFont(
            familyName = "Shonar Bangla Display",
            weightClass = 800,
            sFamilyClass = 12 shl 8, // IBM Ornamental -> Decorative
            isOtf = true
        )
        val artisticBytes = buildSyntheticSfntFont(
            familyName = "Bristi Kobita",
            weightClass = 300
        )

        val calligraphyMeta = FontImportManager.parseFontMetadata(calligraphyBytes)
        val decorativeMeta = FontImportManager.parseFontMetadata(decorativeBytes)
        val artisticMeta = FontImportManager.parseFontMetadata(artisticBytes)

        assertEquals("Charulipi Script", calligraphyMeta?.familyName)
        assertEquals("ক্যালিগ্রাফি", FontImportManager.classifyFontCategory(calligraphyMeta, "charulipi.ttf"))
        assertEquals("ডেকোরেটিভ", FontImportManager.classifyFontCategory(decorativeMeta, "shonar_display.otf"))
        assertEquals("আর্টিস্টিক", FontImportManager.classifyFontCategory(artisticMeta, "bristi.ttf"))
    }

    @Test
    fun duplicateDetection_rejectsSameBytesSameNameAndBundledFonts(): Unit = runBlocking {
        val storageDir = FontImportManager.getCustomFontsDir(context)
        val assetHashes = FontImportManager.computeBuiltInAssetHashes(context, BengaliFonts.fonts)

        // 1. Renamed exact-byte copy of built-in OTF must be rejected with exact built-in name
        val bundledOtfBytes = context.assets.open("fonts/mahfuz_bornomala.otf").use { it.readBytes() }
        val bundledDup = FontImportManager.importFontBytes(
            bytes = bundledOtfBytes,
            fileName = "renamed_copy.otf",
            storageDir = storageDir,
            existingFonts = BengaliFonts.fonts,
            existingAssetHashes = assetHashes
        )
        assertTrue(bundledDup is FontImportSingleResult.Duplicate)
        assertEquals(
            "মাহফুজ বর্ণমালা (Mahfuz Bornomala)",
            (bundledDup as FontImportSingleResult.Duplicate).existingName
        )

        // 2. Different bytes but matching normalized name of a built-in font must be rejected
        val sameNameBytes = buildSyntheticSfntFont(familyName = "Alinur's Effort")
        val nameDup = FontImportManager.importFontBytes(
            bytes = sameNameBytes,
            fileName = "alinur_effort_copy.ttf",
            storageDir = storageDir,
            existingFonts = BengaliFonts.fonts,
            existingAssetHashes = assetHashes
        )
        assertTrue(nameDup is FontImportSingleResult.Duplicate)

        // 3. Batch URI import of two identical custom font files: first succeeds, second rejected as duplicate
        val customBytes = buildSyntheticSfntFont(
            familyName = "Bangla Amar Lipi",
            sFamilyClass = 10 shl 8
        )
        val tempDir = File(context.cacheDir, "font_test_input").apply { mkdirs() }
        val f1 = File(tempDir, "Bangla_Amar_Lipi.ttf").apply { writeBytes(customBytes) }
        val f2 = File(tempDir, "Bangla_Amar_Lipi_Copy.ttf").apply { writeBytes(customBytes) }

        val batch = BengaliFonts.importFontsFromUris(
            context,
            listOf(Uri.fromFile(f1), Uri.fromFile(f2))
        )
        assertEquals(1, batch.imported.size)
        assertEquals(1, batch.duplicates.size)
        assertEquals(0, batch.invalid.size)
        assertEquals("ক্যালিগ্রাফি", batch.imported.first().category)
        assertEquals("Bangla Amar Lipi", batch.imported.first().name)
        assertTrue(batch.imported.first().isCustom)

        tempDir.deleteRecursively()
        Unit
    }

    @Test
    fun persistenceAndDelete_surviveReInitAndFallbackSafely(): Unit = runBlocking {
        val customBytes = buildSyntheticSfntFont(
            familyName = "Nobo Bangla Display",
            weightClass = 750,
            sFamilyClass = 12 shl 8,
            isOtf = true
        )
        val tempFile = File(context.cacheDir, "Nobo_Bangla_Display.otf").apply { writeBytes(customBytes) }
        val batch = BengaliFonts.importFontsFromUris(context, listOf(Uri.fromFile(tempFile)))
        tempFile.delete()

        assertEquals(1, batch.imported.size)
        val imported = batch.imported.first()
        assertEquals("ডেকোরেটিভ", imported.category)

        // Simulate app restart via init(context)
        BengaliFonts.init(context)
        val reloaded = BengaliFonts.getFontByKey(imported.key)
        assertEquals(imported.key, reloaded.key)
        assertEquals("Nobo Bangla Display", reloaded.name)
        assertEquals("ডেকোরেটিভ", reloaded.category)
        assertTrue(reloaded.isCustom)
        assertNotNull(reloaded.customFilePath)
        assertTrue(File(reloaded.customFilePath!!).exists())

        // Verify PDF Export embeds the custom font via @font-face Base64 data URI
        val note = NoteEntity(
            id = 42L,
            title = "আমার কবিতা",
            content = "শব্দের মাঝে নতুন ছন্দ",
            fontFamilyKey = imported.key
        )
        val pdfHtml = PdfExportHelper.buildHtmlForNotes(listOf(note), isSingleNote = true)
        assertTrue(pdfHtml.contains("font-family: '${imported.key}'"))
        assertTrue(pdfHtml.contains("data:font/otf;base64,"))
        assertTrue(pdfHtml.contains("'${imported.key}', 'Tiro Bangla'"))

        // Delete custom font & verify safe fallback
        val deleted = BengaliFonts.deleteCustomFont(context, imported.key)
        assertTrue(deleted)
        assertFalse(File(imported.customFilePath!!).exists())
        val fallback = BengaliFonts.getFontByKey(imported.key)
        assertEquals("anupam_mahdi", fallback.key)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun fontPickerSheet_displaysImportButtonAutoGroupedCustomFontAndSelectAction(): Unit = runBlocking {
        val customBytes = buildSyntheticSfntFont(
            familyName = "Rupkotha Kobita",
            weightClass = 350
        )
        val tempFile = File(context.cacheDir, "Rupkotha_Kobita.ttf").apply { writeBytes(customBytes) }
        val batch = BengaliFonts.importFontsFromUris(context, listOf(Uri.fromFile(tempFile)))
        tempFile.delete()
        val imported = batch.imported.single()
        assertEquals("আর্টিস্টিক", imported.category)

        var lastSelectedKey = ""
        composeRule.setContent {
            MaterialTheme {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                FontPickerSheet(
                    sheetState = sheetState,
                    selectedFontKey = "anupam_mahdi",
                    onFontSelected = { lastSelectedKey = it },
                    onDismiss = {}
                )
            }
        }
        composeRule.waitForIdle()

        // Import button & all existing category tabs exist
        composeRule.onNodeWithTag("import_font_button").assertIsDisplayed()
        composeRule.onNodeWithTag("font_category_সব").assertIsDisplayed()
        composeRule.onNodeWithTag("font_category_ক্যালিগ্রাফি").assertIsDisplayed()
        composeRule.onNodeWithTag("font_category_আর্টিস্টিক").assertIsDisplayed()
        composeRule.onNodeWithTag("font_category_ডেকোরেটিভ").assertIsDisplayed()

        // Custom font appears immediately under "সব" and inside its auto-assigned category ("আর্টিস্টিক")
        composeRule.onNodeWithTag("font_option_${imported.key}").assertIsDisplayed()
        composeRule.onNodeWithTag("delete_custom_font_${imported.key}").assertIsDisplayed()
        composeRule.onNodeWithText("ইমপোর্টেড").assertIsDisplayed()

        composeRule.onNodeWithTag("font_category_ক্যালিগ্রাফি").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("font_option_${imported.key}").assertDoesNotExist()

        composeRule.onNodeWithTag("font_category_আর্টিস্টিক").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("font_option_${imported.key}").assertIsDisplayed()

        // Clicking imported font selects it identically to built-in fonts
        composeRule.onNodeWithTag("font_option_${imported.key}").performClick()
        composeRule.waitForIdle()
        assertEquals(imported.key, lastSelectedKey)
    }
}
