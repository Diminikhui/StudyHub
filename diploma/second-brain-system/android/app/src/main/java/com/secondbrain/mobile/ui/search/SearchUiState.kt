package com.secondbrain.mobile.ui.search

import com.secondbrain.mobile.model.UiAnswerResponse

data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val answer: UiAnswerResponse? = null,
    val error: String? = null,
    val hasSearched: Boolean = false
)