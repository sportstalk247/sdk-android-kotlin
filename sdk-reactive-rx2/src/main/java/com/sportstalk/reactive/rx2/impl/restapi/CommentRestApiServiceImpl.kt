package com.sportstalk.reactive.rx2.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.comment.*
import com.sportstalk.reactive.rx2.impl.handleSdkResponse
import com.sportstalk.reactive.rx2.impl.restapi.retrofit.services.CommentRetrofitService
import com.sportstalk.reactive.rx2.service.CommentService
import io.reactivex.Single
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create

class CommentRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP) constructor(
    private val appId: String, private val json: Json, mRetrofit: Retrofit
) : CommentService {

    private val service: CommentRetrofitService = mRetrofit.create()

    override fun createOrUpdateConversation(request: CreateOrUpdateConversationRequest): Single<Conversation> =
        service.createOrUpdateConversation(
            appId = appId,
            request = request,
        )
            .handleSdkResponse(json)

    override fun getConversation(conversationid: String): Single<Conversation> =
        service.getConversation(appId, conversationid)
            .handleSdkResponse(json)

    override fun getConversationByCustomId(customid: String): Single<Conversation> =
        service.getConversationByCustomId(appId, customid)
            .handleSdkResponse(json)

    override fun listConversations(
        propertyid: String?,
        cursor: String?,
        limit: Int?,
        sort: SortType?
    ): Single<ListConversations> =
        service.listConversations(
            appId = appId,
            propertyId = propertyid,
            cursor = cursor,
            limit = limit,
            sort = sort?.rawValue,
        )
            .handleSdkResponse(json)

    override fun batchGetConversationDetails(
        ids: List<String>?,
        cid: List<String>?,
        entities: List<BatchGetConversationEntity>?
    ): Single<BatchGetConversationDetailsResponse> =
        service.batchGetConversationDetails(
            appId = appId,
            ids = ids?.joinToString(",") { it.trim() },
            cid = cid,
            entities = entities?.map { it.rawValue },
        )
            .handleSdkResponse(json)

    override fun reactToConversationTopic(
        conversationid: String,
        request: ReactToConversationTopicRequest
    ): Single<Conversation> =
        service.reactToConversationTopic(
            appId = appId,
            conversationId = conversationid,
            request = request,
        )
            .handleSdkResponse(json)

    override fun createComment(
        conversationid: String,
        request: CreateCommentRequest
    ): Single<Comment> =
        service.createComment(
            appId = appId,
            conversationId = conversationid,
            request = request,
        )
            .handleSdkResponse(json)

    override fun replyToComment(
        conversationid: String,
        replyto: String,
        request: CreateCommentRequest
    ): Single<Comment> =
        service.replyToComment(
            appId = appId,
            conversationId = conversationid,
            replyToCommentId = replyto,
            request = request,
        )
            .handleSdkResponse(json)

    override fun listReplies(
        conversationid: String,
        commentid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): Single<ListComments> =
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
        )
            .handleSdkResponse(json)

    override fun getComment(conversationid: String, commentid: String): Single<Comment> =
        service.getComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
        )
            .handleSdkResponse(json)

    override fun listComments(
        conversationid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): Single<ListComments> =
        service.listComments(
            appId = appId,
            conversationId = conversationid,
            cursor = cursor,
            limit = limit,
            direction = direction?.rawValue,
            sort = sort?.rawValue,
            includeChildren = includechildren,
            includeInactive = includeinactive,
        )
            .handleSdkResponse(json)

    override fun listRepliesBatch(
        conversationid: String,
        childlimit: Int?,
        parentids: List<String>,
        includeinactive: Boolean?
    ): Single<ListRepliesBatchResponse> =
        service.listRepliesBatch(
            appId = appId,
            conversationId = conversationid,
            childlimit = childlimit,
            parentids = parentids,
            includeInactive = includeinactive,
        )
            .handleSdkResponse(json)

    override fun reactToComment(
        conversationid: String,
        commentid: String,
        request: ReactToCommentRequest
    ): Single<Comment> =
        service.reactToComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        )
            .handleSdkResponse(json)

    override fun voteOnComment(
        conversationid: String,
        commentid: String,
        request: VoteOnCommentRequest
    ): Single<Comment> =
        service.voteOnComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        )
            .handleSdkResponse(json)

    override fun reportComment(
        conversationid: String,
        commentid: String,
        request: ReportCommentRequest
    ): Single<Comment> =
        service.reportComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request
        )
            .handleSdkResponse(json)

    override fun updateComment(
        conversationid: String,
        commentid: String,
        request: UpdateCommentRequest
    ): Single<Comment> =
        service.updateComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            request = request,
        )
            .handleSdkResponse(json)

    override fun flagCommentLogicallyDeleted(
        conversationid: String,
        commentid: String,
        userid: String,
        deleted: Boolean,
        permanentifnoreplies: Boolean?
    ): Single<DeleteCommentResponse> =
        service.flagCommentLogicallyDeleted(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
            userid = userid,
            deleted = deleted,
            permanentIfNoReplies = permanentifnoreplies ?: false,   // Default to false
        )
            .handleSdkResponse(json)

    override fun permanentlyDeleteComment(
        conversationid: String,
        commentid: String
    ): Single<DeleteCommentResponse> =
        service.permanentlyDeleteComment(
            appId = appId,
            conversationId = conversationid,
            commentId = commentid,
        )
            .handleSdkResponse(json)

    override fun deleteConversation(conversationid: String): Single<DeleteConversationResponse> =
        service.deleteConversation(appId, conversationid)
            .handleSdkResponse(json)
}