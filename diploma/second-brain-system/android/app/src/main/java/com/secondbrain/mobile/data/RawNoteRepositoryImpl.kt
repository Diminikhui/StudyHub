package com.secondbrain.mobile.data

import com.secondbrain.mobile.model.ActionItemResponse
import com.secondbrain.mobile.model.AskRequest
import com.secondbrain.mobile.model.CreateRawItemRequest
import com.secondbrain.mobile.model.FactResponse
import com.secondbrain.mobile.model.InboxPageResponse
import com.secondbrain.mobile.model.PersonNoteResponse
import com.secondbrain.mobile.model.PersonResponse
import com.secondbrain.mobile.model.ProposalResponse
import com.secondbrain.mobile.model.RawFragmentResponse
import com.secondbrain.mobile.model.RawItemAttachmentResponse
import com.secondbrain.mobile.model.RawItemResponse
import com.secondbrain.mobile.model.SearchRequest
import com.secondbrain.mobile.model.SearchResponse
import com.secondbrain.mobile.model.TopicNoteResponse
import com.secondbrain.mobile.model.TopicResponse
import com.secondbrain.mobile.model.UiAnswerResponse
import com.secondbrain.mobile.network.SecondBrainApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.secondbrain.mobile.model.LoginRequest
import com.secondbrain.mobile.model.LoginResponse

class RawNoteRepositoryImpl(
    private val api: SecondBrainApi
) : RawNoteRepository {

    override suspend fun createRawItem(request: CreateRawItemRequest): RawItemResponse =
        api.createRawItem(request)

    override suspend fun uploadRawItem(
        file: File,
        sourceType: String,
        contentText: String?
    ): RawItemResponse {
        val mediaType = guessMimeType(file.extension)
        val fileRequestBody = file.asRequestBody(mediaType.toMediaTypeOrNull())

        val filePart = MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = fileRequestBody
        )

        val sourceTypePart = sourceType.toRequestBody("text/plain".toMediaTypeOrNull())
        val contentTextPart = contentText
            ?.takeIf { it.isNotBlank() }
            ?.toRequestBody("text/plain".toMediaTypeOrNull())

        return api.uploadRawItem(
            file = filePart,
            sourceType = sourceTypePart,
            contentText = contentTextPart
        )
    }

    override suspend fun getInbox(page: Int, size: Int): InboxPageResponse =
        api.getInbox(page, size)

    override suspend fun getRawItem(id: String): RawItemResponse =
        api.getRawItem(id)

    override suspend fun getFragments(id: String): List<RawFragmentResponse> =
        api.getFragments(id)

    override suspend fun getProposals(id: String): List<ProposalResponse> =
        api.getProposals(id)

    override suspend fun acceptProposal(id: Long) {
        api.acceptProposal(id)
    }

    override suspend fun rejectProposal(id: Long) {
        api.rejectProposal(id)
    }

    override suspend fun getActions(id: String): List<ActionItemResponse> =
        api.getActions(id)

    override suspend fun markActionDone(id: Long) {
        api.markActionDone(id)
    }

    override suspend fun getFacts(rawItemId: String): List<FactResponse> =
        api.getRawItemFacts(rawItemId)

    override suspend fun getTopics(rawItemId: String): List<TopicResponse> =
        api.getRawItemTopics(rawItemId)

    override suspend fun getPersons(rawItemId: String): List<PersonResponse> =
        api.getRawItemPersons(rawItemId)

    override suspend fun getAttachments(rawItemId: String): List<RawItemAttachmentResponse> =
        api.getRawItemAttachments(rawItemId)

    override suspend fun search(query: String): SearchResponse =
        api.search(SearchRequest(query))

    override suspend fun ask(query: String, limit: Int): UiAnswerResponse =
        api.ask(AskRequest(query = query, limit = limit))

    override suspend fun getTopicNote(id: Long): TopicNoteResponse =
        api.getTopicNote(id)

    override suspend fun getPersonNote(id: Long): PersonNoteResponse =
        api.getPersonNote(id)

    override suspend fun login(username: String, password: String): LoginResponse =
        api.login(LoginRequest(username = username, password = password))

    private fun guessMimeType(extension: String): String {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "m4a" -> "audio/mp4"
            "pdf" -> "application/pdf"
            "txt" -> "text/plain"
            else -> "application/octet-stream"
        }
    }
}