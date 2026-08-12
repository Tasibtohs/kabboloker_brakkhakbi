package com.hmibrahimsarkar.kabboloker_brakkhakbi.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.AppDatabase
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.repository.NoteRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class TextHistoryItem(
    val title: String,
    val content: String
)

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = NoteRepository(db.noteDao(), db.groupDao())

    private var currentNoteId: Long? = null
    private val saveMutex = Mutex()

    // Editor state
    private val _noteState = MutableStateFlow(NoteEntity())
    val noteState: StateFlow<NoteEntity> = _noteState.asStateFlow()

    private val _isSaved = MutableStateFlow(true)
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<TextHistoryItem>()
    private val redoStack = mutableListOf<TextHistoryItem>()

    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private val _canRedo = MutableStateFlow(false)
    val canRedo: StateFlow<Boolean> = _canRedo.asStateFlow()

    private var autoSaveJob: Job? = null

    fun loadNote(
        noteId: Long?,
        defaultFontSizeSp: Float = 18f,
        defaultFontFamilyKey: String = "anupam_mahdi",
        defaultTextAlign: String = "LEFT"
    ) {
        currentNoteId = if (noteId != null && noteId > 0) noteId else null
        undoStack.clear()
        redoStack.clear()
        updateUndoRedoStates()
        if (currentNoteId != null) {
            viewModelScope.launch {
                val existing = repository.getNoteByIdSync(currentNoteId!!)
                if (existing != null) {
                    _noteState.value = existing
                    _isSaved.value = true
                }
            }
        } else {
            _noteState.value = NoteEntity(
                fontSizeSp = defaultFontSizeSp,
                fontFamilyKey = defaultFontFamilyKey,
                textAlign = defaultTextAlign
            )
            _isSaved.value = true
        }
    }

    fun updateTitle(newTitle: String) {
        saveHistoryState()
        val activeId = currentNoteId ?: _noteState.value.id
        _noteState.value = _noteState.value.copy(id = activeId, title = newTitle, updatedAt = System.currentTimeMillis())
        triggerDebouncedAutoSave()
    }

    fun updateContent(newContent: String) {
        saveHistoryState()
        val activeId = currentNoteId ?: _noteState.value.id
        _noteState.value = _noteState.value.copy(id = activeId, content = newContent, updatedAt = System.currentTimeMillis())
        triggerDebouncedAutoSave()
    }

    fun updateStyle(
        titleColorHex: String? = null,
        textColorHex: String? = null,
        fontFamilyKey: String? = null,
        fontSizeSp: Float? = null,
        isBold: Boolean? = null,
        isItalic: Boolean? = null,
        isUnderline: Boolean? = null,
        isStrikethrough: Boolean? = null,
        textAlign: String? = null,
        lineBreakMode: String? = null,
        lineSpacingMultiplier: Float? = null,
        groupId: Long? = null,
        isPinned: Boolean? = null,
        isLocked: Boolean? = null,
        isHidden: Boolean? = null
    ) {
        val current = _noteState.value
        val activeId = currentNoteId ?: current.id
        _noteState.value = current.copy(
            id = activeId,
            titleColorHex = titleColorHex ?: current.titleColorHex,
            textColorHex = textColorHex ?: current.textColorHex,
            fontFamilyKey = fontFamilyKey ?: current.fontFamilyKey,
            fontSizeSp = fontSizeSp ?: current.fontSizeSp,
            isBold = isBold ?: current.isBold,
            isItalic = isItalic ?: current.isItalic,
            isUnderline = isUnderline ?: current.isUnderline,
            isStrikethrough = isStrikethrough ?: current.isStrikethrough,
            textAlign = textAlign ?: current.textAlign,
            lineBreakMode = lineBreakMode ?: current.lineBreakMode,
            lineSpacingMultiplier = lineSpacingMultiplier ?: current.lineSpacingMultiplier,
            groupId = if (groupId == -1L) null else (groupId ?: current.groupId),
            isPinned = isPinned ?: current.isPinned,
            isLocked = isLocked ?: current.isLocked,
            isHidden = isHidden ?: current.isHidden,
            updatedAt = System.currentTimeMillis()
        )
        triggerDebouncedAutoSave()
    }

    private fun triggerDebouncedAutoSave() {
        _isSaved.value = false
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(400) // 400ms debounce
            saveNote()
        }
    }

    suspend fun saveNote() = saveMutex.withLock {
        withContext(NonCancellable) {
            val current = _noteState.value
            if (current.title.isBlank() && current.content.isBlank() && (currentNoteId == null || currentNoteId == 0L) && current.id == 0L) {
                _isSaved.value = true
                return@withContext
            }

            val idToSave = if (current.id != 0L) current.id else (currentNoteId ?: 0L)

            if (idToSave == 0L) {
                val noteToInsert = current.copy(id = 0L)
                val generatedId = repository.insertNote(noteToInsert)
                if (generatedId > 0L) {
                    currentNoteId = generatedId
                    _noteState.value = _noteState.value.copy(id = generatedId)
                }
            } else {
                val noteToUpdate = current.copy(id = idToSave)
                repository.updateNote(noteToUpdate)
                currentNoteId = idToSave
                if (_noteState.value.id == 0L) {
                    _noteState.value = _noteState.value.copy(id = idToSave)
                }
            }
            _isSaved.value = true
        }
    }

    private fun saveHistoryState() {
        val current = _noteState.value
        if (undoStack.isEmpty() || undoStack.last().title != current.title || undoStack.last().content != current.content) {
            undoStack.add(TextHistoryItem(current.title, current.content))
            if (undoStack.size > 30) undoStack.removeAt(0)
            redoStack.clear()
            updateUndoRedoStates()
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val current = _noteState.value
            redoStack.add(TextHistoryItem(current.title, current.content))

            val lastState = undoStack.removeAt(undoStack.size - 1)
            _noteState.value = _noteState.value.copy(
                title = lastState.title,
                content = lastState.content,
                updatedAt = System.currentTimeMillis()
            )
            updateUndoRedoStates()
            triggerDebouncedAutoSave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val current = _noteState.value
            undoStack.add(TextHistoryItem(current.title, current.content))

            val nextState = redoStack.removeAt(redoStack.size - 1)
            _noteState.value = _noteState.value.copy(
                title = nextState.title,
                content = nextState.content,
                updatedAt = System.currentTimeMillis()
            )
            updateUndoRedoStates()
            triggerDebouncedAutoSave()
        }
    }

    private fun updateUndoRedoStates() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    fun togglePin() {
        updateStyle(isPinned = !_noteState.value.isPinned)
    }

    fun toggleLock() {
        updateStyle(isLocked = !_noteState.value.isLocked)
    }

    fun deleteCurrentNote() {
        viewModelScope.launch {
            val id = _noteState.value.id
            if (id > 0) {
                repository.softDeleteNote(id)
            }
        }
    }
}
