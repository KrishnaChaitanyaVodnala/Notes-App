package com.example.notesapp

import androidx.lifecycle.ViewModel
import com.example.notesapp.repository.NotesRepository
import com.example.notesapp.repository.NotesRepositoryImpl

class NotesViewModel(
    val repository: NotesRepository = NotesRepositoryImpl()
): ViewModel() {
    val notes = repository.list

    private var idCnt = 0

    fun addNote(list: List<String>): Boolean {
        val title = list[0]
        if(title.isBlank()) return false

        val notes = list[1]
        repository.addNote(title = title, notes = notes, id = idCnt)
        idCnt++

        return true
    }

    fun deleteNote(id: Int) {
        repository.deleteNote(id)
    }

    fun searchNote(id: Int): NotesRepository.Note? {
        return notes.value.find({ it.id == id })
    }
}