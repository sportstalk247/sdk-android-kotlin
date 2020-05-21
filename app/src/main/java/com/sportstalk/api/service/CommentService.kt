package com.sportstalk.api.service

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.comment.*
import java.util.concurrent.CompletableFuture

interface CommentService {

    /**
     * [POST] /{{api_appid}}/comment/conversations
     * - https://apiref.sportstalk247.com/?version=latest#e0a0a63c-6e68-49d4-ab5c-b6c19a173f06
     * - Creates a conversation (a context for comments)
     */
    fun createOrUpdateConversation(
            request: CreateOrUpdateConversationRequest
    ): CompletableFuture<ApiResponse<Conversation>>


    /**
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}
     * - https://apiref.sportstalk247.com/?version=latest#b23cafdf-35ce-4edc-b073-1215595a9de0
     * - Get Conversation by ID
     */
    fun getConversation(
            id: String
    ): CompletableFuture<ApiResponse<Conversation>>

    /**
     * [GET] /{{api_appid}}/comment/conversations/bycustomid?customid=
     * - https://apiref.sportstalk247.com/?version=latest#5c85f5cb-8bd0-4a9d-b78f-165bfc31a724
     * - Find Conversation by CustomID
     */
    fun getConversationByCustomId(
            customid: String
    ): CompletableFuture<ApiResponse<Conversation>>

    /**
     * [GET] /{{api_appid}}/comment/conversations/
     * - https://apiref.sportstalk247.com/?version=latest#dd62cc9e-c3be-4826-831d-40783531adb4
     * - Get a list of all conversations with optional filters
     */
    fun listConversations(
            propertyid: String? = null, // OPTIONAL
            cursor: String? = null, // OPTIONAL
            limit: Int? = null // OPTIONAL
    ): CompletableFuture<ApiResponse<ListConversations>>

    /**
     * [DEL] /{{api_appid}}/comment/conversations/{{conversation_id}}
     * - https://apiref.sportstalk247.com/?version=latest#a46d3b6b-2537-4200-9637-cefea9dce555
     * - DELETES a Conversation, all Comments and Replies
     */
    fun deleteConversation(
            id: String
    ): CompletableFuture<ApiResponse<ListConversations>>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments
     * - https://apiref.sportstalk247.com/?version=latest#1a6e6c69-c904-458e-ac87-c215091db098
     * - Creates a comment and publishes it
     */
    fun publishComment(
            conversationid: String,
            request: PublishCommentRequest
    ): CompletableFuture<ApiResponse<Comment>>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#1a6e6c69-c904-458e-ac87-c215091db098
     * - Creates a reply to a comment and publishes it
     */
    fun replyToComment(
            conversationid: String,
            replyto: String, // The unique comment ID we will reply to.
            request: PublishCommentRequest
    ): CompletableFuture<ApiResponse<Comment>>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#b02ee426-5a84-4203-93b4-989ad43fe227
     * - UPDATES the contents of an existing comment
     */
    fun updateComment(
            conversationid: String,
            commentid: String,
            request: UpdateCommentRequest
    ): CompletableFuture<ApiResponse<Comment>>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#b7c10a98-f5cd-4ed5-8fcc-aa3440cd4233
     * - Get a COMMENT by ID
     */
    fun getComment(
            conversationid: String,
            commentid: String
    ): CompletableFuture<ApiResponse<Comment>>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments?customid=
     * -
     * - Get a COMMENT by custom id
     */
    fun getCommentByCustomId(
            conversationid: String,
            customid: String
    ): CompletableFuture<ApiResponse<Comment>>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/
     * - https://apiref.sportstalk247.com/?version=latest#98744685-35c9-4293-a082-594cb7a6ec76
     * - Get a list of comments within a conversation
     */
    fun listComments(
            conversationid: String,
            cursor: String? = null, // OPTIONAL
            limit: Int? = null, // OPTIONAL
            direction: String? = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
            sort: String? = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies", or "backward"
            includechildren: Boolean? = null, // OPTIONAL, defaults to false
            includeinactive: Boolean? = null // OPTIONAL, defaults to false
    ): CompletableFuture<ApiResponse<ListComments>>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/replies
     * - https://apiref.sportstalk247.com/?version=latest#71e7a205-471a-4554-9897-da45a8b671ee
     * - Get a list of replies to a comment
     */
    fun listReplies(
            conversationid: String,
            commentid: String,
            cursor: String? = null, // OPTIONAL
            limit: Int? = null, // OPTIONAL
            direction: String? = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
            sort: String? = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies", or "backward"
            includechildren: Boolean? = null, // OPTIONAL, defaults to false
            includeinactive: Boolean? = null // OPTIONAL, defaults to false
    ): CompletableFuture<ApiResponse<ListComments>>

    /**
     * [DEL] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#5e14e5ea-e8b6-46e0-9cb8-263f695ea652
     * - Flag Comment As Deleted
     */
    fun permanentlyDeleteComment(
            conversationid: String,
            commentid: String
    ): CompletableFuture<ApiResponse<DeleteCommentResponse>>

    fun flagCommentLogicallyDeleted(
            conversationid: String,
            commentid: String
    ): CompletableFuture<ApiResponse<DeleteCommentResponse>>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/react
     * - https://apiref.sportstalk247.com/?version=latest#df659fc4-0bb8-4d93-845d-c61579a1f0f8
     * - Adds or removes a reaction to a comment
     */
    fun reactToComment(
            conversationid: String,
            commentid: String,
            request: ReactToCommentRequest
    ): CompletableFuture<ApiResponse<Comment>>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/vote
     * - https://apiref.sportstalk247.com/?version=latest#82ffbc3a-01fe-4f1d-a7b1-62440179dfa5
     * - UPVOTE, DOWNVOTE, or REMOVE VOTE
     */
    fun reportComment(
            conversationid: String,
            commentid: String,
            request: ReportCommentRequest
    ): CompletableFuture<ApiResponse<Comment>>

}