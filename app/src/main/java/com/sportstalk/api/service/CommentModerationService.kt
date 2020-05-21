package com.sportstalk.api.service

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.comment.Comment
import com.sportstalk.models.comment.ListComments
import java.util.concurrent.CompletableFuture

interface CommentModerationService {

    /*
     * [GET] /{{api_appid}}/comment/moderation/queues/comments
     * - https://apiref.sportstalk247.com/?version=latest#d98100c4-6be8-415c-9c08-f6bcbc039566
     * - List all the comments in the moderation queue
     */
    fun listCommentsInModerationQueue(
            limit: Int? = null, // OPTIONAL
            cursor: String? = null, // OPTIONAL
            conversationid: String? = null, // OPTIONAL, limit to comments under specified conversation ID
            filterHandle: String? = null, // OPTIONAL
            filterKeyword: String? = null, // OPTIONAL
            filterModerationState: String? = null // OPTIONAL, Must be "approved", "rejected", "pending", OR "flagged"
    ): CompletableFuture<ApiResponse<ListComments>>

    /*
     * [GET] /{{api_appid}}/comment/moderation/queues/comments/{{comment_id}}/applydecision
     * - https://apiref.sportstalk247.com/?version=latest#71eb7668-f9d1-4ecf-9e07-1f64699ff071
     * - APPROVES a message in the moderation queue.
     */
    fun approveComment(
            conversationid: String,
            commentid: String
    ): CompletableFuture<ApiResponse<Comment>>

    fun rejectComment(
            conversationid: String,
            commentid: String
    ): CompletableFuture<ApiResponse<Comment>>

}