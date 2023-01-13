package com.sportstalk.coroutine.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.impl.handleSdkResponse
import com.sportstalk.coroutine.impl.restapi.retrofit.services.CommentRetrofitService
import com.sportstalk.coroutine.service.CommentService
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.comment.*
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create

class CommentRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) constructor(
    private val appId: String, private val json: Json, mRetrofit: Retrofit
) : CommentService {

    private val service: CommentRetrofitService = mRetrofit.create()

    override suspend fun createOrUpdateConversation(request: CreateOrUpdateConversationRequest): Conversation =
        try {
            service.createOrUpdateConversation(
                appId = appId,
                request = request,
            ).handleSdkResponse(json)
        } catch (err: SportsTalkException) {
            throw err
        } catch (err: Throwable) {
            throw SportsTalkException(
                message = err.message,
                err = err,
            )
        }

    override suspend fun getConversation(conversationid: String): Conversation = try {
        service.getConversation(
            appId = appId,
            conversationId = conversationid,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun getConversationByCustomId(customid: String): Conversation = try {
        service.getConversationByCustomId(
            appId = appId,
            customId = customid,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun listConversations(
        propertyid: String?, cursor: String?, limit: Int?, sort: SortType?
    ): ListConversations = try {
        service.listConversations(
            appId = appId,
            propertyId = propertyid,
            cursor = cursor,
            limit = limit,
            sort = sort?.rawValue,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun batchGetConversationDetails(
        ids: List<String>?, cid: List<String>?, entities: List<BatchGetConversationEntity>?
    ): BatchGetConversationDetailsResponse = try {
        service.batchGetConversationDetails(
            appId = appId,
            ids = ids?.joinToString(",") { it.trim() },
            cid = cid,
            entities = entities?.map { it.rawValue },
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun reactToConversationTopic(
        conversationid: String, request: ReactToConversationTopicRequest
    ): Conversation = try {
        service.reactToConversationTopic(
            appId = appId,
            conversationId = conversationid,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun createComment(
        conversationid: String, request: CreateCommentRequest
    ): Comment = try {
        service.createComment(
            appId = appId,
            conversationId = conversationid,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun replyToComment(
        conversationid: String, replyto: String, request: CreateCommentRequest
    ): Comment = try {
        service.replyToComment(
            appId = appId,
            conversationId = conversationid,
            replyToCommentId = replyto,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun listReplies(
        conversationid: String,
        commentid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): ListComments = try {
        service.listReplies(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            cursor = cursor,
            limit = limit,
            direction = direction?.rawValue,
            sort = sort?.rawValue,
            includeChildren = includechildren,
            includeInactive = includeinactive,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun getComment(conversationid: String, commentid: String): Comment = try {
        service.getComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun listComments(
        conversationid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): ListComments = try {
        service.listComments(
            appId = appId,
            conversationId = conversationid,
            cursor = cursor,
            limit = limit,
            direction = direction?.rawValue,
            sort = sort?.rawValue,
            includeChildren = includechildren,
            includeInactive = includeinactive,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun listRepliesBatch(
        conversationid: String, childlimit: Int?, parentids: List<String>, includeinactive: Boolean?
    ): ListRepliesBatchResponse = try {
        service.listRepliesBatch(
            appId = appId,
            conversationId = conversationid,
            childlimit = childlimit,
            parentids = parentids,
            includeInactive = includeinactive,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun reactToComment(
        conversationid: String, commentid: String, request: ReactToCommentRequest
    ): Comment = try {
        service.reactToComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun voteOnComment(
        conversationid: String, commentid: String, request: VoteOnCommentRequest
    ): Comment = try {
        service.voteOnComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun reportComment(
        conversationid: String, commentid: String, request: ReportCommentRequest
    ): Comment = try {
        service.reportComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun updateComment(
        conversationid: String, commentid: String, request: UpdateCommentRequest
    ): Comment = try {
        service.updateComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun flagCommentLogicallyDeleted(
        conversationid: String,
        commentid: String,
        userid: String,
        deleted: Boolean,
        permanentifnoreplies: Boolean?
    ): DeleteCommentResponse = try {
        service.flagCommentLogicallyDeleted(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            userid = userid,
            deleted = deleted,
            permanentIfNoReplies = permanentifnoreplies ?: false,   // Default to false
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun permanentlyDeleteComment(
        conversationid: String, commentid: String
    ): DeleteCommentResponse = try {
        service.permanentlyDeleteComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
        ).handleSdkResponse(json)
    } catch (err: SportsTalkException) {
        throw err
    } catch (err: Throwable) {
        throw SportsTalkException(
            message = err.message,
            err = err,
        )
    }

    override suspend fun deleteConversation(conversationid: String): DeleteConversationResponse =
        try {
            service.deleteConversation(
                appId = appId, conversationId = conversationid
            ).handleSdkResponse(json)
        } catch (err: SportsTalkException) {
            throw err
        } catch (err: Throwable) {
            throw SportsTalkException(
                message = err.message,
                err = err,
            )
        }
}