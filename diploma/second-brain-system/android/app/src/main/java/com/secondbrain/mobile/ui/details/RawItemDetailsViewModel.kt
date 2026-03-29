package com.secondbrain.mobile.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.data.RawNoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RawItemDetailsViewModel(
    private val repository: RawNoteRepository,
    private val rawItemId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(RawItemDetailsUiState(isLoading = true))
    val uiState: StateFlow<RawItemDetailsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val rawItem = repository.getRawItem(rawItemId)
                val fragments = repository.getFragments(rawItemId)
                val proposals = repository.getProposals(rawItemId)
                val actions = repository.getActions(rawItemId)
                val facts = repository.getFacts(rawItemId)
                val topics = repository.getTopics(rawItemId)
                val persons = repository.getPersons(rawItemId)

                _uiState.value = RawItemDetailsUiState(
                    isLoading = false,
                    rawItem = rawItem,
                    fragments = fragments,
                    proposals = proposals,
                    actions = actions,
                    facts = facts,
                    topics = topics,
                    persons = persons,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load details"
                )
            }
        }
    }

    fun acceptProposal(proposalId: Long) {
        viewModelScope.launch {
            try {
                repository.acceptProposal(proposalId)
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to accept: ${e.message}")
            }
        }
    }

    fun rejectProposal(proposalId: Long) {
        viewModelScope.launch {
            try {
                repository.rejectProposal(proposalId)
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to reject: ${e.message}")
            }
        }
    }

    fun markActionDone(actionId: Long) {
        viewModelScope.launch {
            try {
                repository.markActionDone(actionId)
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to mark done: ${e.message}")
            }
        }
    }
}