package com.secondbrain.mobile.ui.personnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.data.RawNoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonNoteViewModel(
    private val repository: RawNoteRepository,
    private val personId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<PersonNoteUiState>(PersonNoteUiState.Loading)
    val uiState: StateFlow<PersonNoteUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = PersonNoteUiState.Loading
            try {
                val note = repository.getPersonNote(personId)
                _uiState.value = PersonNoteUiState.Success(note)
            } catch (e: Exception) {
                _uiState.value = PersonNoteUiState.Error(e.message ?: "Failed to load person note")
            }
        }
    }
}