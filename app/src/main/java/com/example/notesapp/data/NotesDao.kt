package com.example.notesapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * from notes WHERE id = :id") // :id - references an argument from its attached function
    fun getItem(id: Int): Flow<Note> // No need to make the fun suspend, because of the Flow return type, Room also runs the query on the background thread.

    @Query("SELECT * from notes")
    fun getAllItems(): Flow<List<Note>>
}