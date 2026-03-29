package com.secondbrain.mobile.ui.topicnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.data.RawNoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TopicNoteViewModel(
    private val repository: RawNoteRepository,
    private val topicId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<TopicNoteUiState>(TopicNoteUiState.Loading)
    val uiState: StateFlow<TopicNoteUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = TopicNoteUiState.Loading
            try {
                val note = repository.getTopicNote(topicId)
                _uiState.value = TopicNoteUiState.Success(note)
            } catch (e: Exception) {
                _uiState.value = TopicNoteUiState.Error(e.message ?: "Failed to load topic note")
            }
        }
    }
}