package com.example.notesapp

import android.app.Application
import com.example.notesapp.data.NotesDatabase
import com.example.notesapp.repository.NotesRepository
import com.example.notesapp.repository.NotesRepositoryImpl

class NotesApplication: Application() {
    val repository: NotesRepository by lazy {
        NotesRepositoryImpl(NotesDatabase.getDatabase(this).notesDao())
    }
}