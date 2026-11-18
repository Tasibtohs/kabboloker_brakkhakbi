package com.hmibrahimsarkar.kabboloker_brakkhakbi

import android.app.Application
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.AppDatabase
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.GroupEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.repository.NoteRepository
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.AssignGroupDialog
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NavigationDrawerContent
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.components.NoteCardItem
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.screens.EditorScreen
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.MainViewModel
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NavigationAndGroupFeaturesTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var db: AppDatabase
    private lateinit var repository: NoteRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = NoteRepository(db.noteDao(), db.groupDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun navigationDrawer_showsLockedNotesAlongsideAllGroupsPinned_andHeaderLowered() {
        var navigatedScreen: Screen? = null

        composeRule.setContent {
            MaterialTheme {
                NavigationDrawerContent(
                    currentScreen = Screen.NotesList,
                    isDarkMode = false,
                    onNavigate = { navigatedScreen = it },
                    onToggleDarkMode = {},
                    onCloseDrawer = {}
                )
            }
        }
        composeRule.waitForIdle()

        // Verify header is offset downward cleanly (extra top padding applied)
        val headerBounds = composeRule.onNodeWithTag("drawer_header", useUnmergedTree = true).getUnclippedBoundsInRoot()
        assertTrue("Expected drawer header top >= 20.dp, got ${headerBounds.top}", headerBounds.top >= 20.dp)

        // Verify all 4 items exist together
        composeRule.onNodeWithTag("drawer_item_all_notes").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_item_groups").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_item_pinned_notes").assertIsDisplayed()
        composeRule.onNodeWithTag("drawer_item_locked_notes").assertIsDisplayed()

        // Click "লক করা নোট"
        composeRule.onNodeWithTag("drawer_item_locked_notes").performClick()
        composeRule.waitForIdle()
        assertEquals(Screen.LockedNotes, navigatedScreen)
    }

    @Test
    fun daoLockedNotesAndMoveNoteBetweenGroups_workAccurately(): Unit = runBlocking {
        val groupA = repository.insertGroup(GroupEntity(name = "প্রেম ও বিরহ"))
        val groupB = repository.insertGroup(GroupEntity(name = "প্রকৃতি ও দেশ"))

        val note1Id = repository.insertNote(
            NoteEntity(title = "লক করা কবিতা", content = "গোপন পঙ্ক্তি", isLocked = true, groupId = groupA)
        )
        repository.insertNote(
            NoteEntity(title = "সাধারণ কবিতা", content = "স্বাভাবিক পঙ্ক্তি", isLocked = false, groupId = groupA)
        )
        repository.insertNote(
            NoteEntity(title = "ট্র্যাশড লক নোট", content = "মুছে ফেলা", isLocked = true, isTrashed = true)
        )

        val locked = repository.lockedNotes.first()
        assertEquals(1, locked.size)
        assertEquals("লক করা কবিতা", locked.first().title)

        // Move note1 from Group A to Group B
        val note1 = repository.getNoteByIdSync(note1Id)!!
        repository.updateNote(note1.copy(groupId = groupB))

        val inGroupA = repository.getNotesByGroup(groupA).first()
        val inGroupB = repository.getNotesByGroup(groupB).first()
        assertEquals(1, inGroupA.size)
        assertEquals("সাধারণ কবিতা", inGroupA.first().title)
        assertEquals(1, inGroupB.size)
        assertEquals("লক করা কবিতা", inGroupB.first().title)
        Unit
    }

    @Test
    fun assignGroupDialog_andNoteCardMoveAction_switchGroupsCleanly() {
        val groups = listOf(
            GroupEntity(id = 1L, name = "গীতি"),
            GroupEntity(id = 2L, name = "আধুনিক")
        )
        val note = NoteEntity(id = 7L, title = "আমার গান", content = "সুর ভেসে যায়", groupId = 1L)
        var moveDialogOpened = false
        var selectedTargetGroupId: Long? = -1L

        composeRule.setContent {
            MaterialTheme {
                androidx.compose.foundation.layout.Column {
                    NoteCardItem(
                        note = note,
                        groupName = "গীতি",
                        onClick = {},
                        onLongClick = {},
                        onTogglePin = {},
                        onMoveToGroup = { moveDialogOpened = true }
                    )
                    AssignGroupDialog(
                        groups = groups,
                        currentGroupId = note.groupId,
                        onSelectGroup = { selectedTargetGroupId = it },
                        onDismiss = {}
                    )
                }
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("move_note_group_7", useUnmergedTree = true).performClick()
        assertTrue(moveDialogOpened)

        composeRule.onNodeWithText("আধুনিক").performClick()
        assertEquals(2L, selectedTargetGroupId)
    }

    @Test
    fun editorBottomBar_doesNotContainCheckmarkSaveIcon_andKeepsFormattingActions() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val mainViewModel = MainViewModel(app)

        composeRule.setContent {
            MaterialTheme {
                EditorScreen(
                    noteId = null,
                    mainViewModel = mainViewModel,
                    onBack = {}
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("editor_bottom_bar").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Formatting").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Font Picker").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Color Picker").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Lock").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Pin").assertIsDisplayed()

        // Checkmark icon ("Save Note") must not exist in bottom bar
        composeRule.onNodeWithContentDescription("Save Note").assertDoesNotExist()
    }
}
