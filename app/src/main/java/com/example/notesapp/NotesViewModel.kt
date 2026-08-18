package com.example.notesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.Note
import com.example.notesapp.repository.NotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(val repository: NotesRepository): ViewModel() {

    val notes: StateFlow<List<Note>> = repository.notes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private var idCnt = 0

    fun addNote(list: List<String>): Boolean {
        val title = list[0]
        if(title.isBlank()) return false

        val notes = list[1]

        viewModelScope.launch {
            repository.addNote(
                Note(idCnt, title, notes)
            )
        }

        idCnt++

        return true
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun searchNote(id: Int): Note? {
        return notes.value.find { it.id == id }
    }
}