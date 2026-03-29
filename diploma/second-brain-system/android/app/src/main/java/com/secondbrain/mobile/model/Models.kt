package com.secondbrain.mobile.model

data class CreateRawItemRequest(
    val contentText: String,
    val sourceType: String = "TEXT"
)

data class RawItemResponse(
    val id: String,
    val contentText: String?,
    val sourceType: String,
    val status: String,
    val processingState: String,
    val createdAt: String,
    val updatedAt: String?
)

data class InboxItemResponse(
    val id: String,
    val contentText: String?,
    val sourceType: String,
    val status: String,
    val processingState: String,
    val createdAt: String
)

data class InboxPageResponse(
    val items: List<InboxItemResponse>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

data class RawFragmentResponse(
    val id: Long,
    val fragmentIndex: Int,
    val contentText: String
)

data class ProposalResponse(
    val id: Long,
    val proposalType: String,
    val status: String,
    val title: String?,
    val description: String?,
    val payloadJson: String?,
    val createdAt: String,
    val updatedAt: String?
)

data class ActionItemResponse(
    val id: Long,
    val title: String,
    val done: Boolean,
    val topicId: Long?,
    val topicName: String?,
    val personId: Long?,
    val personName: String?,
    val createdAt: String,
    val updatedAt: String?
)

data class FactResponse(
    val id: Long,
    val contentText: String,
    val topicId: Long?,
    val topicName: String?,
    val createdAt: String,
    val updatedAt: String
)

data class TopicResponse(
    val id: Long,
    val name: String,
    val normalizedName: String,
    val createdAt: String,
    val updatedAt: String
)

data class SearchResponse(
    val query: String,
    val facts: List<FactResponse>,
    val actions: List<ActionItemResponse>,
    val topics: List<TopicResponse>
)

data class SearchRequest(
    val query: String
)

data class PersonResponse(
    val id: Long,
    val displayName: String,
    val normalizedName: String,
    val createdAt: String,
    val updatedAt: String
)

data class AskRequest(
    val query: String,
    val limit: Int = 5
)

data class UiAnswerSourceResponse(
    val entityType: String,
    val entityId: Long,
    val sourceText: String
)

data class UiAnswerResponse(
    val status: String,
    val answer: String,
    val sources: List<UiAnswerSourceResponse>
)

data class NoteActionItemResponse(
    val id: Long,
    val title: String,
    val displayText: String,
    val done: Boolean,
    val topicId: Long?,
    val topicName: String?,
    val personId: Long?,
    val personName: String?
)

data class NoteFactResponse(
    val id: Long,
    val text: String,
    val topicId: Long?,
    val topicName: String?
)

data class NotePersonResponse(
    val id: Long,
    val name: String
)

data class NoteTopicResponse(
    val id: Long,
    val name: String
)

data class TopicNoteResponse(
    val topicId: Long,
    val title: String,
    val summary: String,
    val facts: List<NoteFactResponse>,
    val actions: List<NoteActionItemResponse>,
    val persons: List<NotePersonResponse>
)

data class PersonNoteResponse(
    val personId: Long,
    val name: String,
    val summary: String,
    val topics: List<NoteTopicResponse>,
    val facts: List<NoteFactResponse>,
    val actions: List<NoteActionItemResponse>
)

data class RawItemAttachmentResponse(
    val id: Long,
    val originalFileName: String,
    val storedFileName: String,
    val mimeType: String?,
    val fileSize: Long,
    val storagePath: String,
    val createdAt: String
)
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val username: String,
    val role: String
)