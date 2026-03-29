package com.secondbrain.mobile.ui.ask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.data.SecondBrainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AskViewModel(
    private val repository: SecondBrainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AskUiState>(AskUiState.Idle)
    val uiState: StateFlow<AskUiState> = _uiState.asStateFlow()

    fun ask(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.value = AskUiState.Loading
            try {
                val response = repository.ask(query.trim(), 5)
                _uiState.value = AskUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = AskUiState.Error(e.message ?: "Failed to get answer")
            }
        }
    }
}