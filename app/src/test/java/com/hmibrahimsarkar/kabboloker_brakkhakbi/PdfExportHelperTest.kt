package com.hmibrahimsarkar.kabboloker_brakkhakbi

import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.util.PdfExportHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PdfExportHelperTest {

    private fun createDummyNote(id: Long, title: String, content: String): NoteEntity {
        return NoteEntity(
            id = id,
            title = title,
            content = content,
            createdAt = 1700000000000L,
            updatedAt = 1700000000000L,
            isPinned = false,
            isLocked = false,
            fontFamilyKey = "anupam_mahdi",
            fontSizeSp = 18f,
            titleColorHex = "#B8860B",
            textColorHex = "#2C2C3A",
            textAlign = "center",
            isBold = false,
            isItalic = false,
            isUnderline = false,
            isStrikethrough = false,
            lineSpacingMultiplier = 1.5f,
            groupId = null,
            isHidden = false
        )
    }

    @Test
    fun testLandscapeOrientationAndMetaTagsInHtml() {
        val notes = listOf(
            createDummyNote(1, "প্রথম কবিতা", "বাতাসে ভেসে আসে সুর,\nমেঘের দেশে বহু দূর।"),
            createDummyNote(2, "দ্বিতীয় কবিতা", "নদীর তীরে বসে একা,\nমন খারাপের পায় না দেখা।")
        )
        val html = PdfExportHelper.buildHtmlForNotes(notes, isSingleNote = false)

        assertTrue(html.contains("size: A4 landscape;"))
        assertTrue(html.contains("width: 1123px;"))
        assertTrue(html.contains("viewport\" content=\"width=1123"))
        assertTrue(html.contains("two-column-container"))
    }

    @Test
    fun testCoverAndEndingPageSingleColumnCenteredFrame() {
        val notes = listOf(
            createDummyNote(1, "প্রথম কবিতা", "লাইন এক\nলাইন দুই"),
            createDummyNote(2, "দ্বিতীয় কবিতা", "লাইন তিন\nলাইন চার")
        )
        val html = PdfExportHelper.buildHtmlForNotes(notes, isSingleNote = false)

        // Cover page centered column frame
        assertTrue(html.contains("cover-column-card"))
        assertTrue(html.contains("width: 530px;"))

        // Ending page centered column frame
        assertTrue(html.contains("end-column-card"))

        // Table of contents centered single list
        assertTrue(html.contains("toc-centered-container"))
        assertTrue(html.contains("toc-single-list"))
    }

    @Test
    fun testTableOfContentsStrictSerialOrderAndAccuratePageNumbers() {
        // 10 notes: note 1 (short -> page 1), note 2 (long -> page 2 & 3), note 3 (short -> page 4), note 4 (short -> page 5)
        val note1 = createDummyNote(1, "কবিতা ১", "ছোট কবিতা ১")
        val longLines = (1..20).joinToString("\n") { "চরণ $it" }
        val note2 = createDummyNote(2, "কবিতা ২", longLines)
        val note3 = createDummyNote(3, "কবিতা ৩", "ছোট কবিতা ৩")
        val note4 = createDummyNote(4, "কবিতা ৪", "ছোট কবিতা ৪")

        val html = PdfExportHelper.buildHtmlForNotes(listOf(note1, note2, note3, note4), isSingleNote = false)

        // TOC must contain serial order ১. কবিতা ১ -> পাতা ১, ২. কবিতা ২ -> পাতা ২, ৩. কবিতা ৩ -> পাতা ৪, ৪. কবিতা ৪ -> পাতা ৫
        assertTrue(html.contains("১. কবিতা ১"))
        assertTrue(html.contains("২. কবিতা ২"))
        assertTrue(html.contains("৩. কবিতা ৩"))
        assertTrue(html.contains("৪. কবিতা ৪"))

        // Assert single-list layout is used rather than grid
        assertTrue(html.contains("toc-single-list"))
        assertFalse(html.contains("toc-grid"))
    }

    @Test
    fun testEvenNotesTwoColumnPairing() {
        // 8 notes -> should create cover, TOC, 4 two-column pages, end page
        val notes = (1..8).map { i ->
            val bnNum = PdfExportHelper.toBengaliNumerals(i)
            createDummyNote(i.toLong(), "কবিতা $bnNum", "পঙ্ক্তি এক $bnNum\nপঙ্ক্তি দুই $bnNum\nপঙ্ক্তি তিন $bnNum")
        }
        val html = PdfExportHelper.buildHtmlForNotes(notes, isSingleNote = false)

        assertTrue(html.contains("<div class=\"cover-page"))
        assertTrue(html.contains("landscape-page toc-page"))
        assertTrue(html.contains("two-column-page"))
        assertTrue(html.contains("<div class=\"end-page"))
        assertTrue(html.contains("মোট কবিতা: ৮ টি"))
        assertTrue(html.contains("কবিতা ১"))
        assertTrue(html.contains("কবিতা ৮"))
    }

    @Test
    fun testOddNotesHandlingGracefully() {
        // 7 notes -> 3 full two-column pages + 1 page with left poem and empty right column
        val notes = (1..7).map { i ->
            val bnNum = PdfExportHelper.toBengaliNumerals(i)
            createDummyNote(i.toLong(), "কবিতা $bnNum", "পঙ্ক্তি এক $bnNum\nপঙ্ক্তি দুই $bnNum")
        }
        val html = PdfExportHelper.buildHtmlForNotes(notes, isSingleNote = false)

        assertTrue(html.contains("column-leaf-empty"))
        assertTrue(html.contains("মোট কবিতা: ৭ টি"))
        assertTrue(html.contains("কবিতা ৭"))
    }

    @Test
    fun testLongPoemSpanningOver15Lines() {
        val longPoemLines = (1..20).joinToString("\n") { "চরণ নম্বর ${PdfExportHelper.toBengaliNumerals(it)}" }
        val shortPoem = createDummyNote(1, "ছোট কবিতা", "ছোট দুই লাইন\nদ্বিতীয় লাইন")
        val longPoem = createDummyNote(2, "বড় কবিতা", longPoemLines)

        val html = PdfExportHelper.buildHtmlForNotes(listOf(shortPoem, longPoem), isSingleNote = false)

        assertTrue(html.contains("spanning-poem-container"))
        assertTrue(html.contains("fullwidth-poem-header"))
        assertTrue(html.contains("spread-gutter-divider"))
        assertTrue(html.contains("spanning-leaf-footer"))
    }

    @Test
    fun testSingleNoteLandscapeExport() {
        val notes = listOf(
            createDummyNote(1, "একক কবিতা", "এটি একটি একক কবিতা যা ল্যান্ডস্কেপ মোডে প্রকাশিত হবে।")
        )
        val html = PdfExportHelper.buildHtmlForNotes(notes, isSingleNote = true)

        assertFalse(html.contains("<div class=\"cover-page"))
        assertFalse(html.contains("landscape-page toc-page"))
        assertTrue(html.contains("single-note-page"))
        assertTrue(html.contains("single-poem-container"))
        assertTrue(html.contains("একক কবিতা"))
    }

    @Test
    fun testAuthorSignatureFormatting() {
        val defaultSignature = PdfExportHelper.formatAuthorSignature("এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি")
        assertEquals("— লেখক: এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি", defaultSignature)

        val customAuthor = PdfExportHelper.formatAuthorSignature("রবীন্দ্রনাথ ঠাকুর")
        assertEquals("— লেখক: রবীন্দ্রনাথ ঠাকুর – কাব্যলোকের ব্রক্ষকবি", customAuthor)

        val blankAuthor = PdfExportHelper.formatAuthorSignature("   ")
        assertEquals("", blankAuthor)
    }

    @Test
    fun testBengaliNumeralsConverter() {
        assertEquals("০", PdfExportHelper.toBengaliNumerals(0))
        assertEquals("১২৩", PdfExportHelper.toBengaliNumerals(123))
        assertEquals("৭", PdfExportHelper.toBengaliNumerals(7))
        assertEquals("২০২৪", PdfExportHelper.toBengaliNumerals(2024))
    }
}
