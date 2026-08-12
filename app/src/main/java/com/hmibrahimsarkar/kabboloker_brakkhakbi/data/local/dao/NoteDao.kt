package com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hmibrahimsarkar.kabboloker_brakkhakbi.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes WHERE isTrashed = 0 AND isHidden = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllActiveNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isPinned = 1 AND isTrashed = 0 AND isHidden = 0 ORDER BY updatedAt DESC")
    fun getPinnedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isHidden = 1 AND isTrashed = 0 ORDER BY updatedAt DESC")
    fun getHiddenNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isTrashed = 1 ORDER BY updatedAt DESC")
    fun getTrashedNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE groupId = :groupId AND isTrashed = 0 AND isHidden = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getNotesByGroup(groupId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') AND isTrashed = 0 AND isHidden = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    fun getNoteById(id: Long): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteByIdSync(id: Long): NoteEntity?

    @Query("SELECT COUNT(*) FROM notes WHERE isTrashed = 0 AND isHidden = 0")
    fun getActiveNoteCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE updatedAt >= :startOfDayTimestamp AND isTrashed = 0")
    suspend fun countNotesUpdatedSince(startOfDayTimestamp: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE notes SET isTrashed = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun softDeleteNote(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE notes SET isTrashed = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun restoreNote(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM notes WHERE isTrashed = 1")
    suspend fun emptyTrash()

    @Query("SELECT * FROM notes")
    suspend fun getAllNotesForBackup(): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<NoteEntity>)
}
