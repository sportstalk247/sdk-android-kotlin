package com.sportstalk.reactive.rx2.impl.restapi.retrofit.services

import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.comment.ApproveMessageRequest
import com.sportstalk.datamodels.comment.Comment
import com.sportstalk.datamodels.comment.ListComments
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface CommentModerationRetrofitService {

    @GET("{appId}/comment/moderation/queues/comments")
    fun listCommentsInModerationQueue(
        @Path("appId") appId: String,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("conversationid") conversationId: String? = null,
        @Query("filterHandle") filterHandle: String? = null,
        @Query("filterKeyword") filterKeyword: String? = null,
        @Query("filterModerationState") filterModerationState: String? = null,  // [CommentFilterModerationState]
    ): Single<Response<ApiResponse<ListComments>>>

    @POST("{appId}/comment/moderation/queues/comments/{commentid}/applydecision")
    fun approveMessageInQueue(
        @Path("appId") appId: String,
        @Path("commentid") commentId: String,
        @Body request: ApproveMessageRequest,
    ): Single<Response<ApiResponse<Comment>>>

}