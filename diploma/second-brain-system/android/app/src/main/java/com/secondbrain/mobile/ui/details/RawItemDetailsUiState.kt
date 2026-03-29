package com.secondbrain.mobile.ui.details

import com.secondbrain.mobile.model.ActionItemResponse
import com.secondbrain.mobile.model.FactResponse
import com.secondbrain.mobile.model.ProposalResponse
import com.secondbrain.mobile.model.RawFragmentResponse
import com.secondbrain.mobile.model.RawItemResponse
import com.secondbrain.mobile.model.TopicResponse
import com.secondbrain.mobile.model.PersonResponse

data class RawItemDetailsUiState(
    val isLoading: Boolean = false,
    val rawItem: RawItemResponse? = null,
    val fragments: List<RawFragmentResponse> = emptyList(),
    val proposals: List<ProposalResponse> = emptyList(),
    val actions: List<ActionItemResponse> = emptyList(),
    val facts: List<FactResponse> = emptyList(),
    val topics: List<TopicResponse> = emptyList(),
    val persons: List<PersonResponse> = emptyList(),
    val error: String? = null
)