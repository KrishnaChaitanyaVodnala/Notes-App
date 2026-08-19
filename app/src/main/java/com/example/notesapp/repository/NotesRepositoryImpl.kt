package com.example.notesapp.repository

import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesDao
import kotlinx.coroutines.flow.Flow

class NotesRepositoryImpl(val notesDao: NotesDao): NotesRepository {

    override val notes: Flow<List<Note>> = notesDao.getAllItems()

    override suspend fun addNote(note: Note) {
        notesDao.insert(note)
    }

    override suspend fun updateNote(note: Note) {
        notesDao.update(note)
    }

    override suspend fun deleteNote(note: Note) {
        notesDao.delete(note)
    }
}