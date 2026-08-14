package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.AppDatabase
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.GroupEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.preferences.ThemePreferencesRepository
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.repository.NoteRepository
import com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.font.BengaliFonts
import com.hmibrahimsarkar.kabboloker_brakkhakbi.util.PdfExportHelper
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class Screen {
    object Splash : Screen()
    object NotesList : Screen()
    data class Editor(val noteId: Long? = null) : Screen()
    object Groups : Screen()
    object PinnedNotes : Screen()
    object HiddenNotes : Screen()
    object Trash : Screen()
    object BackupRestore : Screen()
    object SettingsAbout : Screen()
    object Settings : Screen()
    object About : Screen()
    object PrivacyPolicy : Screen()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = NoteRepository(db.noteDao(), db.groupDao())
    val themePreferences = ThemePreferencesRepository(application)

    // Current Active Screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Dark Mode preference
    val isDarkMode: StateFlow<Boolean?> = themePreferences.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkMode(isDark)
        }
    }

    // Notification preference
    val isNotificationsEnabled: StateFlow<Boolean> = themePreferences.isNotificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Font size preference
    val fontSizePreference: StateFlow<String> = themePreferences.fontSizePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "medium")

    // View mode preference (LIST, CARD, GRID)
    val viewModePreference: StateFlow<String> = themePreferences.viewModePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "CARD")

    // Sort Order preference (NEWEST_FIRST, OLDEST_FIRST, TITLE_ASC, TITLE_DESC)
    val sortOrderPreference: StateFlow<String> = themePreferences.sortOrderPreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "NEWEST_FIRST")

    fun setViewMode(mode: String) {
        viewModelScope.launch {
            themePreferences.setViewModePreference(mode)
        }
    }

    fun setSortOrder(order: String) {
        viewModelScope.launch {
            themePreferences.setSortOrderPreference(order)
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setNotificationsEnabled(enabled)
        }
    }

    fun setFontSizePreference(size: String) {
        viewModelScope.launch {
            themePreferences.setFontSizePreference(size)
        }
    }

    // Default Writing Settings
    val defaultFontSizeKey: StateFlow<String> = themePreferences.defaultFontSizeKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "medium")

    val defaultFontFamilyKey: StateFlow<String> = themePreferences.defaultFontFamilyKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "anupam_mahdi")

    val defaultTextAlignKey: StateFlow<String> = themePreferences.defaultTextAlignKey
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "LEFT")

    fun setDefaultFontSizeKey(key: String, applyToAllExisting: Boolean = true) {
        viewModelScope.launch {
            themePreferences.setDefaultFontSizeKey(key)
            if (applyToAllExisting) {
                val sizeSp = when (key) {
                    "small" -> 14f
                    "large" -> 22f
                    else -> 18f
                }
                repository.updateAllNotesFontSize(sizeSp)
            }
        }
    }

    fun setDefaultFontFamilyKey(key: String, applyToAllExisting: Boolean = true) {
        viewModelScope.launch {
            themePreferences.setDefaultFontFamilyKey(key)
            if (applyToAllExisting) {
                repository.updateAllNotesFontFamily(key)
            }
        }
    }

    fun setDefaultTextAlignKey(align: String, applyToAllExisting: Boolean = true) {
        viewModelScope.launch {
            themePreferences.setDefaultTextAlignKey(align)
            if (applyToAllExisting) {
                repository.updateAllNotesTextAlign(align)
            }
        }
    }

    fun applyDefaultWritingStylesToAllNotes() {
        viewModelScope.launch {
            val sizeSp = when (defaultFontSizeKey.value) {
                "small" -> 14f
                "large" -> 22f
                else -> 18f
            }
            val fontKey = defaultFontFamilyKey.value
            val align = defaultTextAlignKey.value
            repository.updateAllNotesWritingStyle(sizeSp, fontKey, align)
        }
    }

    // Reminder Settings
    val isReminderMasterEnabled: StateFlow<Boolean> = themePreferences.isReminderMasterEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isDailyReminderEnabled: StateFlow<Boolean> = themePreferences.isDailyReminderEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val reminderHour: StateFlow<Int> = themePreferences.reminderHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20)

    val reminderMinute: StateFlow<Int> = themePreferences.reminderMinute
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setReminderMasterEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setReminderMasterEnabled(enabled)
            if (enabled && isDailyReminderEnabled.value) {
                com.hmibrahimsarkar.kabboloker_brakkhakbi.receiver.DailyReminderManager.scheduleDailyReminder(
                    context, reminderHour.value, reminderMinute.value
                )
            } else {
                com.hmibrahimsarkar.kabboloker_brakkhakbi.receiver.DailyReminderManager.cancelDailyReminder(context)
            }
        }
    }

    fun setDailyReminderEnabled(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDailyReminderEnabled(enabled)
            if (enabled && isReminderMasterEnabled.value) {
                com.hmibrahimsarkar.kabboloker_brakkhakbi.receiver.DailyReminderManager.scheduleDailyReminder(
                    context, reminderHour.value, reminderMinute.value
                )
            } else {
                com.hmibrahimsarkar.kabboloker_brakkhakbi.receiver.DailyReminderManager.cancelDailyReminder(context)
            }
        }
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        viewModelScope.launch {
            themePreferences.setReminderTime(hour, minute)
            if (isReminderMasterEnabled.value && isDailyReminderEnabled.value) {
                com.hmibrahimsarkar.kabboloker_brakkhakbi.receiver.DailyReminderManager.scheduleDailyReminder(
                    context, hour, minute
                )
            }
        }
    }

    fun clearAppCache(context: Context) {
        try {
            context.cacheDir.deleteRecursively()
            Toast.makeText(context, "অ্যাপ ক্যাশ সফলভাবে ক্লিয়ার করা হয়েছে!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "ক্যাশ ক্লিয়ার করতে সমস্যা হয়েছে", Toast.LENGTH_SHORT).show()
        }
    }

    // Editor Top Bar Name preference
    val editorTopBarName: StateFlow<String> = themePreferences.editorTopBarName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "কাব্যলোকের ব্রক্ষকবি")

    // Author Signature Name preference (for Editor footer & PDF export)
    val authorSignatureName: StateFlow<String> = themePreferences.authorSignatureName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "এইচ. এম. ইব্রাহীম ত্বহা সরকার - কাব্যলোকের ব্রক্ষকবি")

    fun updateEditorTopBarName(name: String) {
        viewModelScope.launch {
            themePreferences.setEditorTopBarName(name)
        }
    }

    fun resetEditorTopBarName() {
        viewModelScope.launch {
            themePreferences.resetEditorTopBarName()
        }
    }

    fun updateAuthorSignatureName(name: String) {
        viewModelScope.launch {
            themePreferences.setAuthorSignatureName(name)
        }
    }

    fun resetAuthorSignatureName() {
        viewModelScope.launch {
            themePreferences.resetAuthorSignatureName()
        }
    }

    fun setAppPassword(password: String, question: String = "", answer: String = "") {
        viewModelScope.launch {
            themePreferences.setAppPassword(password, question, answer)
        }
    }

    // Password Hash preference
    val appPasswordHash: StateFlow<String?> = themePreferences.appPasswordHash
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected Group Filter
    private val _selectedGroupId = MutableStateFlow<Long?>(null)
    val selectedGroupId: StateFlow<Long?> = _selectedGroupId.asStateFlow()

    // Notes Flow based on search, group, and sort order
    @OptIn(FlowPreview::class)
    val notesList: StateFlow<List<NoteEntity>> = combine(
        _searchQuery.debounce(200),
        _selectedGroupId,
        themePreferences.sortOrderPreference
    ) { query, groupId, sortOrder ->
        Triple(query, groupId, sortOrder)
    }.flatMapLatest { (query, groupId, sortOrder) ->
        val baseFlow = when {
            query.isNotBlank() -> repository.searchNotes(query)
            groupId != null -> repository.getNotesByGroup(groupId)
            else -> repository.activeNotes
        }
        baseFlow.map { list ->
            val bnCollator = java.text.Collator.getInstance(java.util.Locale("bn", "BD"))
            when (sortOrder) {
                "OLDEST_FIRST" -> list.sortedWith(
                    compareByDescending<NoteEntity> { it.isPinned }
                        .thenBy { it.updatedAt }
                        .thenBy { it.id }
                )
                "TITLE_ASC" -> list.sortedWith(
                    compareByDescending<NoteEntity> { it.isPinned }
                        .thenComparator { a, b ->
                            val titleA = a.title.trim().ifEmpty { "\uFFFF" }
                            val titleB = b.title.trim().ifEmpty { "\uFFFF" }
                            val comp = bnCollator.compare(titleA, titleB)
                            if (comp != 0) comp else b.updatedAt.compareTo(a.updatedAt)
                        }
                )
                "TITLE_DESC" -> list.sortedWith(
                    compareByDescending<NoteEntity> { it.isPinned }
                        .thenComparator { a, b ->
                            val titleA = a.title.trim().ifEmpty { "\u0000" }
                            val titleB = b.title.trim().ifEmpty { "\u0000" }
                            val comp = bnCollator.compare(titleB, titleA)
                            if (comp != 0) comp else b.updatedAt.compareTo(a.updatedAt)
                        }
                )
                else -> list.sortedWith( // "NEWEST_FIRST"
                    compareByDescending<NoteEntity> { it.isPinned }
                        .thenByDescending { it.updatedAt }
                        .thenByDescending { it.id }
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pinnedNotes: StateFlow<List<NoteEntity>> = repository.pinnedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hiddenNotes: StateFlow<List<NoteEntity>> = repository.hiddenNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashedNotes: StateFlow<List<NoteEntity>> = repository.trashedNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeNoteCount: StateFlow<Int> = repository.activeNoteCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allGroups: StateFlow<List<GroupEntity>> = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Multi-select mode
    private val _selectedNoteIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedNoteIds: StateFlow<Set<Long>> = _selectedNoteIds.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedGroupFilter(groupId: Long?) {
        _selectedGroupId.value = groupId
    }

    fun setPassword(password: String, question: String = "", answer: String = "") {
        viewModelScope.launch {
            themePreferences.setAppPassword(password, question, answer)
        }
    }

    fun toggleNoteSelection(noteId: Long) {
        val current = _selectedNoteIds.value.toMutableSet()
        if (current.contains(noteId)) {
            current.remove(noteId)
        } else {
            current.add(noteId)
        }
        _selectedNoteIds.value = current
    }

    fun clearSelection() {
        _selectedNoteIds.value = emptySet()
    }

    fun selectAllNotes(noteIds: List<Long>) {
        _selectedNoteIds.value = noteIds.toSet()
    }

    fun togglePinNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isPinned = !note.isPinned, updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleLockNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isLocked = !note.isLocked, updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleHideNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isHidden = !note.isHidden, updatedAt = System.currentTimeMillis()))
        }
    }

    fun softDeleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.softDeleteNote(noteId)
        }
    }

    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            repository.restoreNote(noteId)
        }
    }

    fun deleteNotePermanently(noteId: Long) {
        viewModelScope.launch {
            repository.deleteNoteById(noteId)
        }
    }

    fun emptyTrash() {
        viewModelScope.launch {
            repository.emptyTrash()
        }
    }

    fun softDeleteSelectedNotes() {
        viewModelScope.launch {
            _selectedNoteIds.value.forEach { noteId ->
                repository.softDeleteNote(noteId)
            }
            clearSelection()
        }
    }

    fun hideSelectedNotes() {
        viewModelScope.launch {
            _selectedNoteIds.value.forEach { noteId ->
                val note = repository.getNoteByIdSync(noteId)
                if (note != null) {
                    repository.updateNote(note.copy(isHidden = true, updatedAt = System.currentTimeMillis()))
                }
            }
            clearSelection()
        }
    }

    fun hideNotesByIds(noteIds: Set<Long>) {
        viewModelScope.launch {
            noteIds.forEach { noteId ->
                val note = repository.getNoteByIdSync(noteId)
                if (note != null) {
                    repository.updateNote(note.copy(isHidden = true, updatedAt = System.currentTimeMillis()))
                }
            }
        }
    }

    fun removeNoteFromGroup(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(groupId = null, updatedAt = System.currentTimeMillis()))
        }
    }

    fun assignSelectedNotesToGroup(groupId: Long?) {
        viewModelScope.launch {
            _selectedNoteIds.value.forEach { noteId ->
                val note = repository.getNoteByIdSync(noteId)
                if (note != null) {
                    repository.updateNote(note.copy(groupId = groupId, updatedAt = System.currentTimeMillis()))
                }
            }
            clearSelection()
        }
    }

    fun saveGroup(name: String, colorHex: String = "#D4A017", id: Long = 0) {
        viewModelScope.launch {
            if (id == 0L) {
                repository.insertGroup(GroupEntity(name = name, colorHex = colorHex))
            } else {
                repository.updateGroup(GroupEntity(id = id, name = name, colorHex = colorHex))
            }
        }
    }

    fun deleteGroup(group: GroupEntity) {
        viewModelScope.launch {
            repository.deleteGroup(group)
            if (_selectedGroupId.value == group.id) {
                _selectedGroupId.value = null
            }
        }
    }

    // Backup & Restore
    fun exportBackupToJson(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val notes = repository.getAllNotesForBackup()
                val groups = repository.getAllGroupsForBackup()

                val rootObj = JSONObject()
                rootObj.put("app", "কাব্যলোকের ব্রক্ষকবি")
                rootObj.put("version", 1)
                rootObj.put("exportedAt", System.currentTimeMillis())

                val groupsArray = JSONArray()
                groups.forEach { g ->
                    val gObj = JSONObject()
                    gObj.put("id", g.id)
                    gObj.put("name", g.name)
                    gObj.put("colorHex", g.colorHex)
                    groupsArray.put(gObj)
                }
                rootObj.put("groups", groupsArray)

                val notesArray = JSONArray()
                notes.forEach { n ->
                    val nObj = JSONObject()
                    nObj.put("id", n.id)
                    nObj.put("title", n.title)
                    nObj.put("content", n.content)
                    nObj.put("titleColorHex", n.titleColorHex)
                    nObj.put("textColorHex", n.textColorHex)
                    nObj.put("fontFamilyKey", n.fontFamilyKey)
                    nObj.put("fontSizeSp", n.fontSizeSp)
                    nObj.put("isBold", n.isBold)
                    nObj.put("isItalic", n.isItalic)
                    nObj.put("isUnderline", n.isUnderline)
                    nObj.put("isStrikethrough", n.isStrikethrough)
                    nObj.put("textAlign", n.textAlign)
                    nObj.put("lineBreakMode", n.lineBreakMode)
                    nObj.put("lineSpacingMultiplier", n.lineSpacingMultiplier)
                    nObj.put("groupId", n.groupId ?: JSONObject.NULL)
                    nObj.put("isPinned", n.isPinned)
                    nObj.put("isLocked", n.isLocked)
                    nObj.put("isHidden", n.isHidden)
                    nObj.put("isTrashed", n.isTrashed)
                    nObj.put("createdAt", n.createdAt)
                    nObj.put("updatedAt", n.updatedAt)
                    notesArray.put(nObj)
                }
                rootObj.put("notes", notesArray)

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(rootObj.toString(2).toByteArray())
                }
                Toast.makeText(context, "ব্যাকআপ সফলভাবে সংরক্ষণ করা হয়েছে!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "ব্যাকআপ তৈরিতে ত্রুটি: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun restoreBackupFromJson(context: Context, uri: android.net.Uri) {
        viewModelScope.launch {
            try {
                val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().use { it.readText() }
                } ?: return@launch

                val rootObj = JSONObject(jsonString)
                val groupsArray = rootObj.optJSONArray("groups") ?: JSONArray()
                val notesArray = rootObj.optJSONArray("notes") ?: JSONArray()

                val groups = mutableListOf<GroupEntity>()
                for (i in 0 until groupsArray.length()) {
                    val gObj = groupsArray.getJSONObject(i)
                    groups.add(
                        GroupEntity(
                            id = gObj.optLong("id", 0),
                            name = gObj.optString("name", "গ্রুপ"),
                            colorHex = gObj.optString("colorHex", "#D4A017"),
                            createdAt = gObj.optLong("createdAt", System.currentTimeMillis())
                        )
                    )
                }

                val notes = mutableListOf<NoteEntity>()
                for (i in 0 until notesArray.length()) {
                    val nObj = notesArray.getJSONObject(i)
                    val gId = if (nObj.isNull("groupId")) null else nObj.optLong("groupId")
                    notes.add(
                        NoteEntity(
                            id = nObj.optLong("id", 0),
                            title = nObj.optString("title", ""),
                            content = nObj.optString("content", ""),
                            titleColorHex = nObj.optString("titleColorHex", "#D4A017"),
                            textColorHex = nObj.optString("textColorHex", "#1A1A2E"),
                            fontFamilyKey = nObj.optString("fontFamilyKey", "hind_siliguri"),
                            fontSizeSp = nObj.optDouble("fontSizeSp", 16.0).toFloat(),
                            isBold = nObj.optBoolean("isBold", false),
                            isItalic = nObj.optBoolean("isItalic", false),
                            isUnderline = nObj.optBoolean("isUnderline", false),
                            isStrikethrough = nObj.optBoolean("isStrikethrough", false),
                            textAlign = nObj.optString("textAlign", "LEFT"),
                            lineBreakMode = nObj.optString("lineBreakMode", "WORD"),
                            lineSpacingMultiplier = nObj.optDouble("lineSpacingMultiplier", 1.3).toFloat(),
                            groupId = gId,
                            isPinned = nObj.optBoolean("isPinned", false),
                            isLocked = nObj.optBoolean("isLocked", false),
                            isHidden = nObj.optBoolean("isHidden", false),
                            isTrashed = nObj.optBoolean("isTrashed", false),
                            createdAt = nObj.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = nObj.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                }

                repository.restoreBackup(notes, groups)
                Toast.makeText(context, "রিস্টোর সম্পন্ন হয়েছে! ${notes.size} টি নোট পুনরুদ্ধার করা হয়েছে।", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "রিস্টোরে সমস্যা হয়েছে: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // SAF PDF Export function (Single Note)
    fun exportNoteToPdfToUri(context: Context, note: NoteEntity, targetUri: Uri) {
        viewModelScope.launch {
            try {
                val author = authorSignatureName.value
                val htmlContent = PdfExportHelper.buildHtmlForNotes(listOf(note), isSingleNote = true, authorName = author)
                PdfExportHelper.exportToPdfToUri(
                    context = context,
                    htmlContent = htmlContent,
                    targetUri = targetUri,
                    onSuccess = {
                        Toast.makeText(context, "PDF ফাইলটি ডিভাইসে সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_LONG).show()
                    },
                    onError = { e ->
                        Toast.makeText(context, "PDF এক্সপোর্টে সমস্যা: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(context, "PDF এক্সপোর্টে ত্রুটি: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // SAF PDF Export function (All Notes)
    fun exportAllNotesToPdfToUri(context: Context, targetUri: Uri) {
        viewModelScope.launch {
            try {
                val notes = repository.getAllNotesForBackup().filter { !it.isTrashed && !it.isHidden }
                if (notes.isEmpty()) {
                    Toast.makeText(context, "এক্সপোর্ট করার মতো কোনো নোট নেই!", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val author = authorSignatureName.value
                val htmlContent = PdfExportHelper.buildHtmlForNotes(notes, isSingleNote = false, authorName = author)
                PdfExportHelper.exportToPdfToUri(
                    context = context,
                    htmlContent = htmlContent,
                    targetUri = targetUri,
                    onSuccess = {
                        Toast.makeText(context, "সব নোটের PDF সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_LONG).show()
                    },
                    onError = { e ->
                        Toast.makeText(context, "PDF এক্সপোর্টে সমস্যা: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(context, "PDF এক্সপোর্টে ত্রুটি: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
