package com.example.notesapp.repository

import com.example.notesapp.data.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NotesRepository {

    val notes: Flow<List<Note>>

    suspend fun addNote(note: Note)

    suspend fun deleteNote(note: Note)
}