package com.sportstalk.coroutine.impl

import com.sportstalk.coroutine.api.CommentClient
import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.comment.*
import java.util.concurrent.CompletableFuture

class CommentClientImpl(
        private val config: ClientConfig
): CommentClient {

    override var currentConversation: Conversation?
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun createOrUpdateConversation(request: CreateOrUpdateConversationRequest): CompletableFuture<ApiResponse<Conversation>> {
        TODO("Not yet implemented")
    }

    override fun getConversation(id: String): CompletableFuture<ApiResponse<Conversation>> {
        TODO("Not yet implemented")
    }

    override fun getConversationByCustomId(customid: String): CompletableFuture<ApiResponse<Conversation>> {
        TODO("Not yet implemented")
    }

    override fun listConversations(propertyid: String?, cursor: String?, limit: Int?): CompletableFuture<ApiResponse<ListConversations>> {
        TODO("Not yet implemented")
    }

    override fun deleteConversation(id: String): CompletableFuture<ApiResponse<ListConversations>> {
        TODO("Not yet implemented")
    }

    override fun publishComment(conversationid: String, request: PublishCommentRequest): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun replyToComment(conversationid: String, replyto: String, request: PublishCommentRequest): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun updateComment(conversationid: String, commentid: String, request: UpdateCommentRequest): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getComment(conversationid: String, commentid: String): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun getCommentByCustomId(conversationid: String, customid: String): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun listComments(conversationid: String, cursor: String?, limit: Int?, direction: String?, sort: String?, includechildren: Boolean?, includeinactive: Boolean?): CompletableFuture<ApiResponse<ListComments>> {
        TODO("Not yet implemented")
    }

    override fun listReplies(conversationid: String, commentid: String, cursor: String?, limit: Int?, direction: String?, sort: String?, includechildren: Boolean?, includeinactive: Boolean?): CompletableFuture<ApiResponse<ListComments>> {
        TODO("Not yet implemented")
    }

    override fun permanentlyDeleteComment(conversationid: String, commentid: String): CompletableFuture<ApiResponse<DeleteCommentResponse>> {
        TODO("Not yet implemented")
    }

    override fun flagCommentLogicallyDeleted(conversationid: String, commentid: String): CompletableFuture<ApiResponse<DeleteCommentResponse>> {
        TODO("Not yet implemented")
    }

    override fun reactToComment(conversationid: String, commentid: String, request: ReactToCommentRequest): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun reportComment(conversationid: String, commentid: String, request: ReportCommentRequest): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun listCommentsInModerationQueue(limit: Int?, cursor: String?, conversationid: String?, filterHandle: String?, filterKeyword: String?, filterModerationState: String?): CompletableFuture<ApiResponse<ListComments>> {
        TODO("Not yet implemented")
    }

    override fun approveComment(conversationid: String, commentid: String): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }

    override fun rejectComment(conversationid: String, commentid: String): CompletableFuture<ApiResponse<Comment>> {
        TODO("Not yet implemented")
    }
}