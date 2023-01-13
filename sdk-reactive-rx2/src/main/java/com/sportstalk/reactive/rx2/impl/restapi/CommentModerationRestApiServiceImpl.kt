package com.sportstalk.reactive.rx2.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.comment.ApproveMessageRequest
import com.sportstalk.datamodels.comment.Comment
import com.sportstalk.datamodels.comment.CommentFilterModerationState
import com.sportstalk.datamodels.comment.ListComments
import com.sportstalk.reactive.rx2.impl.handleSdkResponse
import com.sportstalk.reactive.rx2.impl.restapi.retrofit.services.CommentModerationRetrofitService
import com.sportstalk.reactive.rx2.service.CommentModerationService
import io.reactivex.Single
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create

class CommentModerationRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) constructor(
    private val appId: String, private val json: Json, mRetrofit: Retrofit
): CommentModerationService {

    private val service: CommentModerationRetrofitService = mRetrofit.create()

    override fun listCommentsInModerationQueue(
        limit: Int?,
        cursor: String?,
        conversationid: String?,
        filterHandle: String?,
        filterKeyword: String?,
        filterModerationState: CommentFilterModerationState?
    ): Single<ListComments> =
        service.listCommentsInModerationQueue(
            appId = appId,
            limit = limit,
            cursor = cursor,
            conversationId = conversationid,
            filterHandle = filterHandle,
            filterKeyword = filterKeyword,
            filterModerationState = filterModerationState?.rawValue,
        )
            .handleSdkResponse(json)

    override fun approveMessageInQueue(
        commentid: String,
        request: ApproveMessageRequest
    ): Single<Comment> =
        service.approveMessageInQueue(
            appId = appId,
            commentId = commentid,
            request = request
        )
            .handleSdkResponse(json)
}