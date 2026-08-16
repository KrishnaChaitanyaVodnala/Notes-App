package com.example.notesapp.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesRepositoryImpl: NotesRepository {

    private val _list = MutableStateFlow<List<Note>>(emptyList())

    override val list: StateFlow<List<Note>> = _list.asStateFlow()

    override fun addNote(title: String, notes: String, id: Int) {
        val note = Note(
            id = id,
            title = title,
            notes = notes
        )

        _list.value += note
    }

    override fun deleteNote(id: Int) {
        _list.value = _list.value.filter {
            it.id != id
        }
    }
}