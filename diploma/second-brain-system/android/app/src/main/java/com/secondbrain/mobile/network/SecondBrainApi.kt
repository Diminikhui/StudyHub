package com.secondbrain.mobile.network

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
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import com.secondbrain.mobile.model.LoginRequest
import com.secondbrain.mobile.model.LoginResponse

interface SecondBrainApi {

    @POST("api/raw-items")
    suspend fun createRawItem(
        @Body request: CreateRawItemRequest
    ): RawItemResponse

    @Multipart
    @POST("api/raw-items/upload")
    suspend fun uploadRawItem(
        @Part file: MultipartBody.Part,
        @Part("sourceType") sourceType: RequestBody,
        @Part("contentText") contentText: RequestBody? = null
    ): RawItemResponse

    @GET("api/inbox")
    suspend fun getInbox(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): InboxPageResponse

    @GET("api/raw-items/{id}")
    suspend fun getRawItem(
        @Path("id") id: String
    ): RawItemResponse

    @GET("api/raw-items/{id}/fragments")
    suspend fun getFragments(
        @Path("id") id: String
    ): List<RawFragmentResponse>

    @GET("api/raw-items/{id}/proposals")
    suspend fun getProposals(
        @Path("id") id: String
    ): List<ProposalResponse>

    @POST("api/proposals/{id}/accept")
    suspend fun acceptProposal(
        @Path("id") id: Long
    )

    @POST("api/proposals/{id}/reject")
    suspend fun rejectProposal(
        @Path("id") id: Long
    )

    @GET("api/raw-items/{id}/actions")
    suspend fun getActions(
        @Path("id") id: String
    ): List<ActionItemResponse>

    @POST("api/actions/{id}/done")
    suspend fun markActionDone(
        @Path("id") id: Long
    )

    @GET("api/raw-items/{rawItemId}/facts")
    suspend fun getRawItemFacts(
        @Path("rawItemId") rawItemId: String
    ): List<FactResponse>

    @GET("api/raw-items/{rawItemId}/topics")
    suspend fun getRawItemTopics(
        @Path("rawItemId") rawItemId: String
    ): List<TopicResponse>

    @GET("api/raw-items/{rawItemId}/persons")
    suspend fun getRawItemPersons(
        @Path("rawItemId") rawItemId: String
    ): List<PersonResponse>

    @GET("api/raw-items/{id}/attachments")
    suspend fun getRawItemAttachments(
        @Path("id") id: String
    ): List<RawItemAttachmentResponse>

    @POST("api/search")
    suspend fun search(
        @Body request: SearchRequest
    ): SearchResponse

    @POST("api/answer")
    suspend fun ask(
        @Body request: AskRequest
    ): UiAnswerResponse

    @GET("api/topics/{id}/note")
    suspend fun getTopicNote(
        @Path("id") id: Long
    ): TopicNoteResponse

    @GET("api/persons/{id}/note")
    suspend fun getPersonNote(
        @Path("id") id: Long
    ): PersonNoteResponse

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}