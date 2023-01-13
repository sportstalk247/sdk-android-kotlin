package com.sportstalk.reactive.rx2.impl

import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.comment.*
import com.sportstalk.reactive.rx2.ServiceFactory
import com.sportstalk.reactive.rx2.api.CommentClient
import com.sportstalk.reactive.rx2.service.CommentModerationService
import com.sportstalk.reactive.rx2.service.CommentService
import io.reactivex.Single

class CommentClientImpl(
    private val config: ClientConfig
): CommentClient {

    private val commentService: CommentService = ServiceFactory.Comment.get(config)
    private val moderationService: CommentModerationService = ServiceFactory.CommentModeration.get(config)

    override fun createOrUpdateConversation(request: CreateOrUpdateConversationRequest): Single<Conversation> =
        commentService.createOrUpdateConversation(request)

    override fun getConversation(conversationid: String): Single<Conversation> =
        commentService.getConversation(conversationid)

    override fun getConversationByCustomId(customid: String): Single<Conversation> =
        commentService.getConversationByCustomId(customid)

    override fun listConversations(
        propertyid: String?,
        cursor: String?,
        limit: Int?,
        sort: SortType?
    ): Single<ListConversations> =
        commentService.listConversations(propertyid, cursor, limit, sort)

    override fun batchGetConversationDetails(
        ids: List<String>?,
        cid: List<String>?,
        entities: List<BatchGetConversationEntity>?
    ): Single<BatchGetConversationDetailsResponse> =
        commentService.batchGetConversationDetails(ids, cid, entities)

    override fun reactToConversationTopic(
        conversationid: String,
        request: ReactToConversationTopicRequest
    ): Single<Conversation> =
        commentService.reactToConversationTopic(conversationid, request)

    override fun createComment(
        conversationid: String,
        request: CreateCommentRequest
    ): Single<Comment> =
        commentService.createComment(conversationid, request)

    override fun replyToComment(
        conversationid: String,
        replyto: String,
        request: CreateCommentRequest
    ): Single<Comment> =
        commentService.replyToComment(conversationid, replyto, request)

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
        commentService.listReplies(conversationid, commentid, cursor, limit, direction, sort, includechildren, includeinactive)

    override fun getComment(conversationid: String, commentid: String): Single<Comment> =
        commentService.getComment(conversationid, commentid)

    override fun listComments(
        conversationid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): Single<ListComments> =
        commentService.listComments(conversationid, cursor, limit, direction, sort, includechildren, includeinactive)

    override fun listRepliesBatch(
        conversationid: String,
        childlimit: Int?,
        parentids: List<String>,
        includeinactive: Boolean?
    ): Single<ListRepliesBatchResponse> =
        commentService.listRepliesBatch(conversationid, childlimit, parentids, includeinactive)

    override fun reactToComment(
        conversationid: String,
        commentid: String,
        request: ReactToCommentRequest
    ): Single<Comment> =
        commentService.reactToComment(conversationid, commentid, request)

    override fun voteOnComment(
        conversationid: String,
        commentid: String,
        request: VoteOnCommentRequest
    ): Single<Comment> =
        commentService.voteOnComment(conversationid, commentid, request)

    override fun reportComment(
        conversationid: String,
        commentid: String,
        request: ReportCommentRequest
    ): Single<Comment> =
        commentService.reportComment(conversationid, commentid, request)

    override fun updateComment(
        conversationid: String,
        commentid: String,
        request: UpdateCommentRequest
    ): Single<Comment> =
        commentService.updateComment(conversationid, commentid, request)

    override fun flagCommentLogicallyDeleted(
        conversationid: String,
        commentid: String,
        userid: String,
        deleted: Boolean,
        permanentifnoreplies: Boolean?
    ): Single<DeleteCommentResponse> =
        commentService.flagCommentLogicallyDeleted(conversationid, commentid, userid, deleted, permanentifnoreplies)

    override fun permanentlyDeleteComment(
        conversationid: String,
        commentid: String
    ): Single<DeleteCommentResponse> =
        commentService.permanentlyDeleteComment(conversationid, commentid)

    override fun deleteConversation(conversationid: String): Single<DeleteConversationResponse> =
        commentService.deleteConversation(conversationid)

    override fun listCommentsInModerationQueue(
        limit: Int?,
        cursor: String?,
        conversationid: String?,
        filterHandle: String?,
        filterKeyword: String?,
        filterModerationState: CommentFilterModerationState?
    ): Single<ListComments> =
        moderationService.listCommentsInModerationQueue(limit, cursor, conversationid, filterHandle, filterKeyword, filterModerationState)

    override fun approveMessageInQueue(
        commentid: String,
        request: ApproveMessageRequest
    ): Single<Comment> =
        moderationService.approveMessageInQueue(commentid, request)
}