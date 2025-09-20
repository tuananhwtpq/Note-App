package com.example.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.noteapp.model.Note
import com.example.noteapp.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application, private val noteRepository: NoteRepository) : AndroidViewModel(application) {

    private val _searchQuery = MutableLiveData<String?>(null)

    val notes: LiveData<List<Note>> = _searchQuery.switchMap { query ->
        if (query.isNullOrBlank()) {
            noteRepository.getAllNotes()
        } else {
            noteRepository.searchNote(query)
        }
    }

    fun addNote(note: Note) =
        viewModelScope.launch {
            noteRepository.insertNote(note)
        }

    fun deleteNote(note: Note) =
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }

    fun updateNote(note: Note) =
        viewModelScope.launch {
            noteRepository.updateNote(note)
        }

    fun getAllNote() =
        noteRepository.getAllNotes()

    fun searchNotes(query: String?) {
        val searchQuery = if (query.isNullOrBlank()) null else "%$query%"
        _searchQuery.value = searchQuery
    }

    fun clearSearch() {
        _searchQuery.value = null
    }
}