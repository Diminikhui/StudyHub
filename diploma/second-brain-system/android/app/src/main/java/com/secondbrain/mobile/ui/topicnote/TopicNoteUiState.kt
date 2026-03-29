package com.secondbrain.mobile.ui.topicnote

import com.secondbrain.mobile.model.TopicNoteResponse

sealed interface TopicNoteUiState {
    data object Loading : TopicNoteUiState
    data class Success(val note: TopicNoteResponse) : TopicNoteUiState
    data class Error(val message: String) : TopicNoteUiState
}