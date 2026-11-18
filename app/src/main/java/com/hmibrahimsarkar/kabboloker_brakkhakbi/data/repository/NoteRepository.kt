package com.hmibrahimsarkar.kabboloker_brakkhakbi.data.repository

import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.dao.GroupDao
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.dao.NoteDao
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.GroupEntity
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(
    private val noteDao: NoteDao,
    private val groupDao: GroupDao
) {
    val activeNotes: Flow<List<NoteEntity>> = noteDao.getAllActiveNotes()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val pinnedNotes: Flow<List<NoteEntity>> = noteDao.getPinnedNotes()
    val lockedNotes: Flow<List<NoteEntity>> = noteDao.getLockedNotes()
    val hiddenNotes: Flow<List<NoteEntity>> = noteDao.getHiddenNotes()
    val trashedNotes: Flow<List<NoteEntity>> = noteDao.getTrashedNotes()
    val activeNoteCount: Flow<Int> = noteDao.getActiveNoteCount()
    val allGroups: Flow<List<GroupEntity>> = groupDao.getAllGroups()

    fun getNotesByGroup(groupId: Long): Flow<List<NoteEntity>> = noteDao.getNotesByGroup(groupId)

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)

    fun getNoteById(id: Long): Flow<NoteEntity?> = noteDao.getNoteById(id)

    suspend fun getNoteByIdSync(id: Long): NoteEntity? = noteDao.getNoteByIdSync(id)

    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    suspend fun softDeleteNote(id: Long) = noteDao.softDeleteNote(id)

    suspend fun restoreNote(id: Long) = noteDao.restoreNote(id)

    suspend fun emptyTrash() = noteDao.emptyTrash()

    // Default Writing Style updates to existing notes
    suspend fun updateAllNotesFontSize(fontSizeSp: Float) = noteDao.updateAllNotesFontSize(fontSizeSp)
    suspend fun updateAllNotesFontFamily(fontFamilyKey: String) = noteDao.updateAllNotesFontFamily(fontFamilyKey)
    suspend fun updateAllNotesTextAlign(textAlign: String) = noteDao.updateAllNotesTextAlign(textAlign)
    suspend fun updateAllNotesWritingStyle(fontSizeSp: Float, fontFamilyKey: String, textAlign: String) =
        noteDao.updateAllNotesWritingStyle(fontSizeSp, fontFamilyKey, textAlign)

    // Group methods
    suspend fun insertGroup(group: GroupEntity): Long = groupDao.insertGroup(group)

    suspend fun updateGroup(group: GroupEntity) = groupDao.updateGroup(group)

    suspend fun deleteGroup(group: GroupEntity) = groupDao.deleteGroup(group)

    suspend fun deleteGroupById(id: Long) = groupDao.deleteGroupById(id)

    // Backup & Restore
    suspend fun getAllNotesForBackup(): List<NoteEntity> = noteDao.getAllNotesForBackup()

    suspend fun getAllGroupsForBackup(): List<GroupEntity> = groupDao.getAllGroupsForBackup()

    suspend fun restoreBackup(notes: List<NoteEntity>, groups: List<GroupEntity>) {
        importBackupMerge(notes, groups)
    }

    suspend fun importBackupMerge(importedNotes: List<NoteEntity>, importedGroups: List<GroupEntity>) {
        val existingGroups = groupDao.getAllGroupsForBackup()
        val groupNameMap = existingGroups.associateBy { it.name.trim().lowercase() }
        val oldGroupIdToNewGroupIdMap = mutableMapOf<Long, Long>()

        importedGroups.forEach { impGroup ->
            val oldId = impGroup.id
            val matchedGroup = groupNameMap[impGroup.name.trim().lowercase()]
            if (matchedGroup != null) {
                if (oldId > 0) {
                    oldGroupIdToNewGroupIdMap[oldId] = matchedGroup.id
                }
            } else {
                val newGroupId = groupDao.insertGroup(impGroup.copy(id = 0))
                if (oldId > 0) {
                    oldGroupIdToNewGroupIdMap[oldId] = newGroupId
                }
            }
        }

        // Set id = 0 on all imported notes so Room creates new records without replacing or deleting existing notes
        val notesToInsert = importedNotes.map { note ->
            val mappedGroupId = if (note.groupId != null) {
                oldGroupIdToNewGroupIdMap[note.groupId] ?: note.groupId
            } else null

            note.copy(
                id = 0,
                groupId = mappedGroupId,
                isTrashed = false
            )
        }

        if (notesToInsert.isNotEmpty()) {
            noteDao.insertAllNotes(notesToInsert)
        }
    }
}
