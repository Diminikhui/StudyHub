package com.secondbrain.mobile.ui.ask

import com.secondbrain.mobile.model.UiAnswerResponse

sealed interface AskUiState {
    data object Idle : AskUiState
    data object Loading : AskUiState
    data class Success(val response: UiAnswerResponse) : AskUiState
    data class Error(val message: String) : AskUiState
}