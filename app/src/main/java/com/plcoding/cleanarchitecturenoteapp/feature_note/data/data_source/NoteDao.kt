package com.plcoding.cleanarchitecturenoteapp.feature_note.data.data_source

import androidx.room.*
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query(value = "SELECT * FROM note")
    fun getAllNotes(): Flow<List<Note>>

    //usage of suspend: getNoteById is a sync db query. getAllNotes is meant to be observed (due to flow) so no suspend used here
    @Query(value = "SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //strategy: will replace id in case pk given already exists in db
    suspend fun inserNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}