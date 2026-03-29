package com.secondbrain.mobile.ui.capture

sealed class CaptureUiState {
    object Idle : CaptureUiState()
    object Loading : CaptureUiState()
    data class Success(val rawItemId: String) : CaptureUiState()
    data class Error(val message: String) : CaptureUiState()
}
