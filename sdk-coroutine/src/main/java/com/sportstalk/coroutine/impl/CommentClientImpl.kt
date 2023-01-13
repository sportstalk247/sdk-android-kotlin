package com.sportstalk.coroutine.impl

import com.sportstalk.coroutine.ServiceFactory
import com.sportstalk.coroutine.api.CommentClient
import com.sportstalk.coroutine.service.CommentModerationService
import com.sportstalk.coroutine.service.CommentService
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.comment.*

class CommentClientImpl(
    private val config: ClientConfig
) : CommentClient {

    private val commentService: CommentService = ServiceFactory.Comment.get(config)
    private val moderationService: CommentModerationService =
        ServiceFactory.CommentModeration.get(config)

    override suspend fun createOrUpdateConversation(request: CreateOrUpdateConversationRequest): Conversation =
        commentService.createOrUpdateConversation(request)

    override suspend fun getConversation(conversationid: String): Conversation =
        commentService.getConversation(conversationid)

    override suspend fun getConversationByCustomId(customid: String): Conversation =
        commentService.getConversationByCustomId(customid)

    override suspend fun listConversations(
        propertyid: String?, cursor: String?, limit: Int?, sort: SortType?
    ): ListConversations = commentService.listConversations(
        propertyid = propertyid,
        cursor = cursor,
        limit = limit,
        sort = sort,
    )

    override suspend fun batchGetConversationDetails(
        ids: List<String>?, cid: List<String>?, entities: List<BatchGetConversationEntity>?
    ): BatchGetConversationDetailsResponse = commentService.batchGetConversationDetails(
        ids = ids,
        cid = cid,
        entities = entities,
    )

    override suspend fun reactToConversationTopic(
        conversationid: String, request: ReactToConversationTopicRequest
    ): Conversation = commentService.reactToConversationTopic(
        conversationid = conversationid,
        request = request,
    )

    override suspend fun createComment(
        conversationid: String, request: CreateCommentRequest
    ): Comment = commentService.createComment(
        conversationid = conversationid,
        request = request,
    )

    override suspend fun replyToComment(
        conversationid: String, replyto: String, request: CreateCommentRequest
    ): Comment = commentService.replyToComment(
        conversationid = conversationid,
        replyto = replyto,
        request = request,
    )

    override suspend fun listReplies(
        conversationid: String,
        commentid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): ListComments = commentService.listReplies(
        conversationid = conversationid,
        commentid = commentid,
        cursor = cursor,
        limit = limit,
        direction = direction,
        sort = sort,
        includechildren = includechildren,
        includeinactive = includeinactive,
    )

    override suspend fun getComment(conversationid: String, commentid: String): Comment =
        commentService.getComment(conversationid, commentid)

    override suspend fun listComments(
        conversationid: String,
        cursor: String?,
        limit: Int?,
        direction: DirectionType?,
        sort: SortType?,
        includechildren: Boolean?,
        includeinactive: Boolean?
    ): ListComments = commentService.listComments(
        conversationid = conversationid,
        cursor = cursor,
        limit = limit,
        direction = direction,
        sort = sort,
        includechildren = includechildren,
        includeinactive = includeinactive,
    )

    override suspend fun listRepliesBatch(
        conversationid: String, childlimit: Int?, parentids: List<String>, includeinactive: Boolean?
    ): ListRepliesBatchResponse = commentService.listRepliesBatch(
        conversationid = conversationid,
        childlimit = childlimit,
        parentids = parentids,
        includeinactive = includeinactive,
    )

    override suspend fun reactToComment(
        conversationid: String, commentid: String, request: ReactToCommentRequest
    ): Comment = commentService.reactToComment(
        conversationid = conversationid,
        commentid = commentid,
        request = request,
    )

    override suspend fun voteOnComment(
        conversationid: String, commentid: String, request: VoteOnCommentRequest
    ): Comment = commentService.voteOnComment(
        conversationid = conversationid,
        commentid = commentid,
        request = request,
    )

    override suspend fun reportComment(
        conversationid: String, commentid: String, request: ReportCommentRequest
    ): Comment = commentService.reportComment(
        conversationid = conversationid,
        commentid = commentid,
        request = request,
    )

    override suspend fun updateComment(
        conversationid: String, commentid: String, request: UpdateCommentRequest
    ): Comment = commentService.updateComment(
        conversationid = conversationid,
        commentid = commentid,
        request = request,
    )

    override suspend fun flagCommentLogicallyDeleted(
        conversationid: String,
        commentid: String,
        userid: String,
        deleted: Boolean,
        permanentifnoreplies: Boolean?
    ): DeleteCommentResponse = commentService.flagCommentLogicallyDeleted(
        conversationid = conversationid,
        commentid = commentid,
        userid = userid,
        deleted = deleted,
        permanentifnoreplies = permanentifnoreplies,
    )

    override suspend fun permanentlyDeleteComment(
        conversationid: String, commentid: String
    ): DeleteCommentResponse = commentService.permanentlyDeleteComment(
        conversationid = conversationid,
        commentid = commentid,
    )

    override suspend fun deleteConversation(conversationid: String): DeleteConversationResponse =
        commentService.deleteConversation(conversationid)

    override suspend fun listCommentsInModerationQueue(
        limit: Int?,
        cursor: String?,
        conversationid: String?,
        filterHandle: String?,
        filterKeyword: String?,
        filterModerationState: CommentFilterModerationState?
    ): ListComments = moderationService.listCommentsInModerationQueue(
        limit = limit,
        cursor = cursor,
        conversationid = conversationid,
        filterHandle = filterHandle,
        filterKeyword = filterKeyword,
        filterModerationState = filterModerationState,
    )

    override suspend fun approveMessageInQueue(
        commentid: String, request: ApproveMessageRequest
    ): Comment = moderationService.approveMessageInQueue(
        commentid = commentid,
        request = request,
    )
}