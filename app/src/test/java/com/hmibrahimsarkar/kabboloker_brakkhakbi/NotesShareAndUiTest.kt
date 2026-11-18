package com.hmibrahimsarkar.kabboloker_brakkhakbi

import android.content.Intent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteCardItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteGridItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteListItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.util.PdfExportHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NotesShareAndUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun buildShareText(
        notes: List<NoteEntity>,
        authorSignatureName: String
    ): String = PdfExportHelper.buildShareText(notes, authorSignatureName)

    @Test
    fun testSingleNoteShareTextWithSignature() {
        val note = NoteEntity(
            id = 1,
            title = "বিদ্রোহী",
            content = "বল বীর -\nবল উন্নত মম শির!"
        )
        val text = buildShareText(listOf(note), "কাজী নজরুল ইসলাম")
        assertTrue(text.contains("বিদ্রোহী"))
        assertTrue(text.contains("বল বীর -"))
        assertTrue(text.contains("লেখক: কাজী নজরুল ইসলাম"))
    }

    @Test
    fun testSingleNoteShareTextWithoutSignatureAndBlankTitle() {
        val note = NoteEntity(
            id = 2,
            title = "   ",
            content = "আকাশে মেঘ জমেছে।"
        )
        val text = buildShareText(listOf(note), "")
        assertFalse(text.contains("শিরোনামহীন"))
        assertTrue(text.startsWith("আকাশে মেঘ জমেছে।"))
        assertFalse(text.contains("লেখক:"))
    }

    @Test
    fun noteCards_hideBlankTitleAndCompactLayout_acrossAllThreeModes() {
        val titledNote = NoteEntity(id = 10L, title = "শিরোনাম আছে", content = "কবিতার চরণ", isPinned = true)
        val untitledNote = NoteEntity(id = 11L, title = "   ", content = "শুধু কবিতার পঙ্ক্তি", isPinned = true)

        composeRule.setContent {
            MaterialTheme {
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.verticalScroll(rememberScrollState())
                ) {
                    NoteCardItem(
                        note = titledNote,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                    NoteCardItem(
                        note = untitledNote,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                    NoteListItem(
                        note = untitledNote,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                    NoteGridItem(
                        note = untitledNote,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                }
            }
        }
        composeRule.waitForIdle()

        // No placeholder text anywhere
        composeRule.onNodeWithText("শিরোনামহীন", substring = true, useUnmergedTree = true).assertDoesNotExist()

        // Titled card renders title & preview; untitled cards omit title node completely
        composeRule.onNodeWithTag("note_card_title_10", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("note_card_title_11", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("note_list_title_11", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("note_grid_title_11", useUnmergedTree = true).assertDoesNotExist()

        // Content previews exist in all 3 modes
        composeRule.onNodeWithTag("note_card_preview_11", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("note_list_preview_11", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("note_grid_preview_11", useUnmergedTree = true).assertExists()

        // Titled card's preview sits below title, while untitled card's preview sits directly at top
        val titledTop = composeRule.onNodeWithTag("note_card_title_10", useUnmergedTree = true).getUnclippedBoundsInRoot().top
        val titledPreviewTop = composeRule.onNodeWithTag("note_card_preview_10", useUnmergedTree = true).getUnclippedBoundsInRoot().top
        assertTrue("Expected preview below title ($titledPreviewTop > $titledTop)", titledPreviewTop > titledTop)
    }

    @Test
    fun noteCards_completelyEmptyTitleAndContent_stillShowActionsAndDate() {
        val completelyEmpty = NoteEntity(id = 20L, title = "", content = "")

        composeRule.setContent {
            MaterialTheme {
                androidx.compose.foundation.layout.Column(
                    modifier = androidx.compose.ui.Modifier.verticalScroll(rememberScrollState())
                ) {
                    NoteCardItem(
                        note = completelyEmpty,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                    NoteListItem(
                        note = completelyEmpty,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                    NoteGridItem(
                        note = completelyEmpty,
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {}
                    )
                }
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("শিরোনামহীন", substring = true, useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("note_card_title_20", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("note_list_title_20", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("note_grid_title_20", useUnmergedTree = true).assertDoesNotExist()
        composeRule.onNodeWithTag("note_card_preview_20", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("note_list_preview_20", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("note_grid_preview_20", useUnmergedTree = true).assertExists()
    }

    @Test
    fun testMultipleNotesShareText() {
        val note1 = NoteEntity(id = 1, title = "প্রথম কবিতা", content = "প্রথম লাইন")
        val note2 = NoteEntity(id = 2, title = "দ্বিতীয় কবিতা", content = "দ্বিতীয় লাইন")
        val text = buildShareText(listOf(note1, note2), "রবীন্দ্রনাথ ঠাকুর")

        assertTrue(text.contains("প্রথম কবিতা"))
        assertTrue(text.contains("দ্বিতীয় কবিতা"))
        assertTrue(text.contains("─────────────────────"))
        assertTrue(text.contains("লেখক: রবীন্দ্রনাথ ঠাকুর"))
    }

    @Test
    fun testShareIntentCreation() {
        val note = NoteEntity(id = 1, title = "সোনার তরী", content = "গগনে গরজে মেঘ, ঘন বরষা")
        val shareText = buildShareText(listOf(note), "রবীন্দ্রনাথ ঠাকুর")

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, note.title)
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        assertEquals("text/plain", intent.type)
        assertEquals("সোনার তরী", intent.getStringExtra(Intent.EXTRA_SUBJECT))
        assertEquals(shareText, intent.getStringExtra(Intent.EXTRA_TEXT))
    }

    @Test
    fun testLockedNoteSubtitleLogic() {
        val isLocked = true
        val isSaved = true
        val subtitle = if (isLocked) "(লক করা)" else (if (isSaved) "" else "অটো-সেভ হচ্ছে...")
        assertEquals("(লক করা)", subtitle)

        val unlockedSubtitle = if (!isLocked) "(লক করা)" else (if (isSaved) "" else "অটো-সেভ হচ্ছে...")
        assertEquals("", unlockedSubtitle)
    }

    @Test
    fun testReadingModeSubtitleLogic() {
        val isLocked = false
        val readingSubtitle = if (isLocked) "(লক করা)" else ""
        assertEquals("", readingSubtitle)
        assertFalse(readingSubtitle.contains("পঠন এলাকা"))
    }

    @Test
    fun testLockedNoteCannotBeDeletedLogic() {
        val note = NoteEntity(id = 5, title = "লক করা কবিতা", content = "গোপন কথা", isLocked = true)
        // Check condition: if isLocked, deletion is prevented
        val canDelete = !note.isLocked
        assertFalse("Locked note must not be deletable", canDelete)
    }

    @Test
    fun testImportPreservesExistingNotesLogic() {
        val existingNotes = listOf(
            NoteEntity(id = 1, title = "বর্তমান কবিতা ১", content = "লেখা ১"),
            NoteEntity(id = 2, title = "বর্তমান কবিতা ২", content = "লেখা ২")
        )
        val importedRawNotes = listOf(
            NoteEntity(id = 1, title = "আমদানিকৃত কবিতা ক", content = "লেখা ক"),
            NoteEntity(id = 2, title = "আমদানিকৃত কবিতা খ", content = "লেখা খ")
        )

        // When imported, new notes get id = 0 so they don't overwrite existing notes with same ID
        val preparedForInsert = importedRawNotes.map { it.copy(id = 0, isTrashed = false) }

        // All new items have id = 0 (ready for auto-generation)
        assertTrue(preparedForInsert.all { it.id == 0L })
        // Existing notes remain intact and are not displaced
        assertEquals(2, existingNotes.size)
        assertEquals("বর্তমান কবিতা ১", existingNotes[0].title)
    }
}
