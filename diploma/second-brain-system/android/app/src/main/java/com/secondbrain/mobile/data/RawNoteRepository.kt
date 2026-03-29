package com.secondbrain.mobile.data

import com.secondbrain.mobile.model.ActionItemResponse
import com.secondbrain.mobile.model.CreateRawItemRequest
import com.secondbrain.mobile.model.FactResponse
import com.secondbrain.mobile.model.InboxPageResponse
import com.secondbrain.mobile.model.PersonNoteResponse
import com.secondbrain.mobile.model.PersonResponse
import com.secondbrain.mobile.model.ProposalResponse
import com.secondbrain.mobile.model.RawFragmentResponse
import com.secondbrain.mobile.model.RawItemAttachmentResponse
import com.secondbrain.mobile.model.RawItemResponse
import com.secondbrain.mobile.model.SearchResponse
import com.secondbrain.mobile.model.TopicNoteResponse
import com.secondbrain.mobile.model.TopicResponse
import com.secondbrain.mobile.model.UiAnswerResponse
import com.secondbrain.mobile.model.LoginResponse
import java.io.File

interface RawNoteRepository {

    suspend fun createRawItem(request: CreateRawItemRequest): RawItemResponse

    suspend fun uploadRawItem(
        file: File,
        sourceType: String,
        contentText: String? = null
    ): RawItemResponse

    suspend fun getInbox(page: Int, size: Int): InboxPageResponse

    suspend fun getRawItem(id: String): RawItemResponse

    suspend fun getFragments(id: String): List<RawFragmentResponse>

    suspend fun getProposals(id: String): List<ProposalResponse>

    suspend fun acceptProposal(id: Long)

    suspend fun rejectProposal(id: Long)

    suspend fun getActions(id: String): List<ActionItemResponse>

    suspend fun markActionDone(id: Long)

    suspend fun getFacts(rawItemId: String): List<FactResponse>

    suspend fun getTopics(rawItemId: String): List<TopicResponse>

    suspend fun getPersons(rawItemId: String): List<PersonResponse>

    suspend fun getAttachments(rawItemId: String): List<RawItemAttachmentResponse>

    suspend fun search(query: String): SearchResponse

    suspend fun ask(query: String, limit: Int = 5): UiAnswerResponse

    suspend fun getTopicNote(id: Long): TopicNoteResponse

    suspend fun getPersonNote(id: Long): PersonNoteResponse

    suspend fun login(username: String, password: String): LoginResponse
}