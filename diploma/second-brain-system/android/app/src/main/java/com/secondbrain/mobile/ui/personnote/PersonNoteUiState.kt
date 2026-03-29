package com.secondbrain.mobile.ui.personnote

import com.secondbrain.mobile.model.PersonNoteResponse

sealed interface PersonNoteUiState {
    data object Loading : PersonNoteUiState
    data class Success(val note: PersonNoteResponse) : PersonNoteUiState
    data class Error(val message: String) : PersonNoteUiState
}