package com.sportstalk.reactive.rx2.impl.restapi.retrofit.services

import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.comment.*
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface CommentRetrofitService {

    @POST("{appId}/comment/conversations")
    fun createOrUpdateConversation(
        @Path("appId") appId: String, @Body request: CreateOrUpdateConversationRequest
    ): Single<Response<ApiResponse<Conversation>>>

    @GET("{appId}/comment/conversations/{conversationid}")
    fun getConversation(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
    ): Single<Response<ApiResponse<Conversation>>>

    @GET("{appId}/comment/find/conversation/bycustomid")
    fun getConversationByCustomId(
        @Path("appId") appId: String,
        @Query("customid") customId: String,
    ): Single<Response<ApiResponse<Conversation>>>

    @GET("{appId}/comment/conversations")
    fun listConversations(
        @Path("appId") appId: String,
        @Query("propertyid") propertyId: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("sort") sort: String? = null,    // [SortType]
    ): Single<Response<ApiResponse<ListConversations>>>

    @GET("{appId}/comment/conversations/details/batch")
    fun batchGetConversationDetails(
        @Path("appId") appId: String,
        @Query("ids") ids: String? = null,  // Comma-separated (ex. "convo-id1,convo-id2,convo-id3")
        @Query("cid", encoded = true) cid: List<String>? = null,
        @Query("entities") entities: List<String>? = null,
    ): Single<Response<ApiResponse<BatchGetConversationDetailsResponse>>>

    @POST("{appId}/comment/conversations/{conversationid}/react")
    fun reactToConversationTopic(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Body request: ReactToConversationTopicRequest,
    ): Single<Response<ApiResponse<Conversation>>>

    @POST("{appId}/comment/conversations/{conversationid}/comments")
    fun createComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Body request: CreateCommentRequest,
    ): Single<Response<ApiResponse<Comment>>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{replytocommentid}")
    fun replyToComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("replytocommentid") replyToCommentId: String,
        @Body request: CreateCommentRequest,
    ): Single<Response<ApiResponse<Comment>>>

    @GET("{appId}/comment/conversations/{conversationid}/comments/{commentid}/replies")
    fun listReplies(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("direction") direction: String? = null,  // DirectionType
        @Query("sort") sort: String? = null,    // SortType
        @Query("includechildren") includeChildren: Boolean? = null,
        @Query("includeinactive") includeInactive: Boolean? = null,
    ): Single<Response<ApiResponse<ListComments>>>

    @GET("{appId}/comment/conversations/{conversationid}/comments/{commentid}")
    fun getComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
    ): Single<Response<ApiResponse<Comment>>>

    @GET("{appId}/comment/conversations/{conversationid}/comments")
    fun listComments(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("direction") direction: String? = null,  // DirectionType
        @Query("sort") sort: String? = null,    // SortType
        @Query("includechildren") includeChildren: Boolean? = null,
        @Query("includeinactive") includeInactive: Boolean? = null,
    ): Single<Response<ApiResponse<ListComments>>>

    @GET("{appId}/comment/conversations/{conversationid}/repliesbyparentidbatch")
    fun listRepliesBatch(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Query("childlimit") childlimit: Int? = null,
        @Query("parentids") parentids: String,    // A comma delimited list of parentids, up to 30.
        @Query("includeinactive") includeInactive: Boolean? = null,
    ): Single<Response<ApiResponse<ListRepliesBatchResponse>>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{commentid}/react")
    fun reactToComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: ReactToCommentRequest,
    ): Single<Response<ApiResponse<Comment>>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{commentid}/vote")
    fun voteOnComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: VoteOnCommentRequest,
    ): Single<Response<ApiResponse<Comment>>>

    @POST("{appId}/comment/conversations/{conversationid}/comments/{commentid}/report")
    fun reportComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: ReportCommentRequest,
    ): Single<Response<ApiResponse<Comment>>>

    @PUT("{appId}/comment/conversations/{conversationid}/comments/{commentid}")
    fun updateComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Body request: UpdateCommentRequest,
    ): Single<Response<ApiResponse<Comment>>>

    @PUT("{appId}/comment/conversations/{conversationid}/comments/{commentid}/setdeleted")
    fun flagCommentLogicallyDeleted(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
        @Query("userid") userid: String,
        @Query("deleted") deleted: Boolean,
        @Query("permanentifnoreplies") permanentIfNoReplies: Boolean = false,
    ): Single<Response<ApiResponse<DeleteCommentResponse>>>

    @DELETE("{appId}/comment/conversations/{conversationid}/comments/{commentid}")
    fun permanentlyDeleteComment(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
        @Path("commentid") commentId: String,
    ): Single<Response<ApiResponse<DeleteCommentResponse>>>

    @DELETE("{appId}/comment/conversations/{conversationid}")
    fun deleteConversation(
        @Path("appId") appId: String,
        @Path("conversationid") conversationId: String,
    ): Single<Response<ApiResponse<DeleteConversationResponse>>>

}