package com.secondbrain.mobile.ui.inbox

data class InboxTopicItem(
    val id: Long,
    val name: String,
    val mentions: Int
)

data class InboxDrawerItem(
    val id: String,
    val title: String,
    val createdAt: String
)

data class InboxUiState(
    val isLoading: Boolean = true,
    val topics: List<InboxTopicItem> = emptyList(),
    val drawerItems: List<InboxDrawerItem> = emptyList(),
    val error: String? = null
)