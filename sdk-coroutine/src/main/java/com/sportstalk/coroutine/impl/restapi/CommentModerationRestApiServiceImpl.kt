package com.sportstalk.coroutine.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.impl.handleSdkResponse
import com.sportstalk.coroutine.impl.restapi.retrofit.services.CommentModerationRetrofitService
import com.sportstalk.coroutine.service.CommentModerationService
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.comment.ApproveMessageRequest
import com.sportstalk.datamodels.comment.Comment
import com.sportstalk.datamodels.comment.CommentFilterModerationState
import com.sportstalk.datamodels.comment.ListComments
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create

class CommentModerationRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) constructor(
    private val appId: String, private val json: Json, mRetrofit: Retrofit
): CommentModerationService {

    private val service: CommentModerationRetrofitService = mRetrofit.create()

    override suspend fun listCommentsInModerationQueue(
        limit: Int?,
        cursor: String?,
        conversationid: String?,
        filterHandle: String?,
        filterKeyword: String?,
        filterModerationState: CommentFilterModerationState?
    ): ListComments =
        try {
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
        } catch (err: SportsTalkException) {
            throw err
        } catch (err: Throwable) {
            throw SportsTalkException(
                message = err.message,
                err = err,
            )
        }

    override suspend fun approveMessageInQueue(
        commentid: String,
        request: ApproveMessageRequest
    ): Comment =
        try {
            service.approveMessageInQueue(
                appId = appId,
                commentId = commentid,
                request = request,
            )
                .handleSdkResponse(json)
        } catch (err: SportsTalkException) {
            throw err
        } catch (err: Throwable) {
            throw SportsTalkException(
                message = err.message,
                err = err,
            )
        }
}