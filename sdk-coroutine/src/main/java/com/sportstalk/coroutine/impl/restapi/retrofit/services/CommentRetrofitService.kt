package com.sportstalk.coroutine.impl.restapi.retrofit.services

import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.comment.*
import retrofit2.Response
import retrofit2.http.*

interface CommentRetrofitService {

    @POST("{appId}/comment/conversations")
    suspend fun createOrUpdateConversation(
        @Path("appId") appId: String, @Body request: CreateOrUpdateConversationRequest
    ): Response<ApiResponse<Conversation>>

    @GET("{appId}/comment/conversations/{conversationid}")
    suspend fun getConversation(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
    ): Response<ApiResponse<Conversation>>

    @GET("{appId}/comment/find/conversation/bycustomid")
    suspend fun getConversationByCustomId(
        @Path("appId") appId: String,
        @Query("customid", encoded = true) customId: String,
    ): Response<ApiResponse<Conversation>>

    @GET("{appId}/comment/conversations")
    suspend fun listConversations(
        @Path("appId") appId: String,
        @Query("propertyid") propertyId: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: String? = null,    // [SortType]
    ): Response<ApiResponse<ListConversations>>

    @GET("{appId}/comment/conversations/details/batch")
    suspend fun batchGetConversationDetails(
        @Path("appId") appId: String,
        @Query("ids") ids: String? = null,  // Comma-separated (ex. "convo-id1,convo-id2,convo-id3")
        @Query("cid", encoded = true) cid: List<String>? = null,
        @Query("entities") entities: List<String>? = null,
    ): Response<ApiResponse<BatchGetConversationDetailsResponse>>

    @POST("{appId}/comment/conversations/{conversationid}/react")
    suspend fun reactToConversationTopic(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Body request: ReactToConversationTopicRequest,
    ): Response<ApiResponse<Conversation>>

    @POST("{appId}/comment/conversations/{conversationid}/comments")
    suspend fun createComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Body request: CreateCommentRequest,
    ): Response<ApiResponse<Comment>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{replytocommentid}")
    suspend fun replyToComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("replytocommentid") replyToCommentId: String,
        @Body request: CreateCommentRequest,
    ): Response<ApiResponse<Comment>>

    @GET("{appId}/comment/conversations/{conversationid}/comments/{commentid}/replies")
    suspend fun listReplies(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("direction") direction: String? = null,  // DirectionType
        @Query("sort") sort: String? = null,    // SortType
        @Query("includechildren") includeChildren: Boolean? = null,
        @Query("includeinactive") includeInactive: Boolean? = null,
    ): Response<ApiResponse<ListComments>>

    @GET("{appId}/comment/conversations/{conversationid}/comments/{commentid}")
    suspend fun getComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
    ): Response<ApiResponse<Comment>>

    @GET("{appId}/comment/conversations/{conversationid}/comments")
    suspend fun listComments(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("direction") direction: String? = null,  // DirectionType
        @Query("sort") sort: String? = null,    // SortType
        @Query("includechildren") includeChildren: Boolean? = null,
        @Query("includeinactive") includeInactive: Boolean? = null,
    ): Response<ApiResponse<ListComments>>

    @GET("{appId}/comment/conversations/{conversationid}/repliesbyparentidbatch")
    suspend fun listRepliesBatch(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Query("childlimit") childlimit: Int? = null,
        @Query("parentids") parentids: String,    // A comma delimited list of parentids, up to 30.
        @Query("includeinactive") includeInactive: Boolean? = null,
    ): Response<ApiResponse<ListRepliesBatchResponse>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{commentid}/react")
    suspend fun reactToComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: ReactToCommentRequest,
    ): Response<ApiResponse<Comment>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{commentid}/vote")
    suspend fun voteOnComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: VoteOnCommentRequest,
    ): Response<ApiResponse<Comment>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{commentid}/report")
    suspend fun reportComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: ReportCommentRequest,
    ): Response<ApiResponse<Comment>>

    @PUT("{appId}/comment/conversations/{conversationid}/comments/{commentid}")
    suspend fun updateComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: UpdateCommentRequest,
    ): Response<ApiResponse<Comment>>

    @PUT("{appId}/comment/conversations/{conversationid}/comments/{commentid}/setdeleted")
    suspend fun flagCommentLogicallyDeleted(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Query("userid") userid: String,
        @Query("deleted") deleted: Boolean,
        @Query("permanentifnoreplies") permanentIfNoReplies: Boolean = false,
    ): Response<ApiResponse<DeleteCommentResponse>>

    @DELETE("{appId}/comment/conversations/{conversationid}/comments/{commentid}")
    suspend fun permanentlyDeleteComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
    ): Response<ApiResponse<DeleteCommentResponse>>

    @DELETE("{appId}/comment/conversations/{conversationid}")
    suspend fun deleteConversation(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
    ): Response<ApiResponse<DeleteConversationResponse>>


}