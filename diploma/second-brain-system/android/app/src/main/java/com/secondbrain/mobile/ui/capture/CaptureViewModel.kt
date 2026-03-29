package com.secondbrain.mobile.ui.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.data.RawNoteRepository
import com.secondbrain.mobile.model.CreateRawItemRequest
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CaptureViewModel(private val repository: RawNoteRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CaptureUiState>(CaptureUiState.Idle)
    val uiState: StateFlow<CaptureUiState> = _uiState.asStateFlow()

    fun createRawItem(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _uiState.value = CaptureUiState.Loading
            try {
                val response = repository.createRawItem(
                    CreateRawItemRequest(contentText = text.trim())
                )
                _uiState.value = CaptureUiState.Success(response.id)
            } catch (e: Exception) {
                _uiState.value = CaptureUiState.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun uploadRawItem(
        file: File,
        sourceType: String,
        contentText: String?
    ) {
        viewModelScope.launch {
            _uiState.value = CaptureUiState.Loading
            try {
                val response = repository.uploadRawItem(
                    file = file,
                    sourceType = sourceType,
                    contentText = contentText?.trim()?.takeIf { it.isNotBlank() }
                )
                _uiState.value = CaptureUiState.Success(response.id)
            } catch (e: Exception) {
                _uiState.value = CaptureUiState.Error(e.message ?: "Failed to upload")
            }
        }
    }
}