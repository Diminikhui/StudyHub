package com.secondbrain.mobile.ui.inbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secondbrain.mobile.data.RawNoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InboxViewModel(
    private val repository: RawNoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InboxUiState())
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val inboxPage = repository.getInbox(page = 0, size = 50)
                val drawerItems = inboxPage.items.map {
                    val title = when {
                        !it.contentText.isNullOrBlank() -> it.contentText.trim().replace("\n", " ").take(48)
                        it.sourceType == "IMAGE" -> "Изображение"
                        it.sourceType == "AUDIO" -> "Аудио"
                        it.sourceType == "FILE" -> "Файл"
                        else -> "Материал"
                    }

                    InboxDrawerItem(
                        id = it.id,
                        title = title,
                        createdAt = it.createdAt.take(16).replace("T", " ")
                    )
                }

                val topicMap = linkedMapOf<Long, InboxTopicItem>()
                for (item in inboxPage.items) {
                    val topics = runCatching { repository.getTopics(item.id) }.getOrDefault(emptyList())
                    for (topic in topics) {
                        val existing = topicMap[topic.id]
                        topicMap[topic.id] = if (existing == null) {
                            InboxTopicItem(
                                id = topic.id,
                                name = topic.name,
                                mentions = 1
                            )
                        } else {
                            existing.copy(mentions = existing.mentions + 1)
                        }
                    }
                }

                val topics = topicMap.values.sortedWith(
                    compareByDescending<InboxTopicItem> { it.mentions }.thenBy { it.name }
                )

                _uiState.value = InboxUiState(
                    isLoading = false,
                    topics = topics,
                    drawerItems = drawerItems,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = InboxUiState(
                    isLoading = false,
                    topics = emptyList(),
                    drawerItems = emptyList(),
                    error = e.message ?: "Failed to load data"
                )
            }
        }
    }
}