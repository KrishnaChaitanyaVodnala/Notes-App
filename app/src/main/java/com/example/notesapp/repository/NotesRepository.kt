package com.example.notesapp.repository

import kotlinx.coroutines.flow.StateFlow

interface NotesRepository {

    val list: StateFlow<List<Note>>

    fun addNote(title: String, notes: String, id: Int)

    fun deleteNote(id: Int)
}