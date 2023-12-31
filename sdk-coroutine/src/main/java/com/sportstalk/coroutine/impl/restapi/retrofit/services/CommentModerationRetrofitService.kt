package com.sportstalk.coroutine.impl.restapi.retrofit.services

import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.comment.ApproveMessageRequest
import com.sportstalk.datamodels.comment.Comment
import com.sportstalk.datamodels.comment.ListComments
import retrofit2.Response
import retrofit2.http.*

interface CommentModerationRetrofitService {

    @GET("{appId}/comment/moderation/queues/comments")
    suspend fun listCommentsInModerationQueue(
        @Path("appId") appId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("conversationid") conversationId: String? = null,
        @Query("filterHandle") filterHandle: String? = null,
        @Query("filterKeyword") filterKeyword: String? = null,
        @Query("filterModerationState") filterModerationState: String? = null,  // [CommentFilterModerationState]
    ): Response<ApiResponse<ListComments>>

    @POST("{appId}/comment/moderation/queues/comments/{commentid}/applydecision")
    suspend fun approveMessageInQueue(
        @Path("appId") appId: String,
        @Path("commentid") commentId: String,
        @Body request: ApproveMessageRequest,
    ): Response<ApiResponse<Comment>>

}