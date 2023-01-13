package com.sportstalk.reactive.rx2.service

import com.sportstalk.datamodels.comment.*
import io.reactivex.Single

interface CommentService {

    /**
     * [POST] /{{api_appid}}/comment/conversations
     * - https://apiref.sportstalk247.com/?version=latest#e0a0a63c-6e68-49d4-ab5c-b6c19a173f06
     * - Creates a conversation (a context for comments)
     */
    fun createOrUpdateConversation(
        request: CreateOrUpdateConversationRequest
    ): Single<Conversation>

    /**
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}
     * - https://apiref.sportstalk247.com/?version=latest#b23cafdf-35ce-4edc-b073-1215595a9de0
     * - Get Conversation by ID
     */
    fun getConversation(
        conversationid: String
    ): Single<Conversation>

    /**
     * [GET] /{{api_appid}}/comment/conversations/bycustomid?customid=
     * - https://apiref.sportstalk247.com/?version=latest#5c85f5cb-8bd0-4a9d-b78f-165bfc31a724
     * - Find Conversation by CustomID
     */
    fun getConversationByCustomId(
        customid: String
    ): Single<Conversation>

    /**
     * [GET] /{{api_appid}}/comment/conversations/
     * - https://apiref.sportstalk247.com/?version=latest#dd62cc9e-c3be-4826-831d-40783531adb4
     * - Get a list of all conversations with optional filters
     */
    fun listConversations(
        propertyid: String? = null, // OPTIONAL
        cursor: String? = null, // OPTIONAL
        limit: Int? = null, // OPTIONAL
        sort: SortType? = null, // OPTIONAL
    ): Single<ListConversations>

    /**
     * [GET] /{{api_appid}}/comment/conversations/details/batch?ids=&cid=&entities=&
     * - https://apiref.sportstalk247.com/?version=latest#7d249f48-de31-4647-9102-527c64fe37bb
     * - Batch Get Conversation Details
     */
    fun batchGetConversationDetails(
        ids: List<String>? = null,
        cid: List<String>? = null,
        entities: List<BatchGetConversationEntity>? = null,
    ): Single<BatchGetConversationDetailsResponse>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/react/
     * - https://apiref.sportstalk247.com/?version=latest#e25d90d0-9a4b-4a45-839f-0242f960c6b3
     * - Adds or removes a reaction to a topic
     */
    fun reactToConversationTopic(
        conversationid: String,
        request: ReactToConversationTopicRequest,
    ): Single<Conversation>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments
     * - https://apiref.sportstalk247.com/?version=latest#1a6e6c69-c904-458e-ac87-c215091db098
     * - Creates a comment and publishes it
     */
    fun createComment(
        conversationid: String,
        request: CreateCommentRequest
    ): Single<Comment>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#c0ccaa52-ac7c-4424-8f27-58cc58812001
     * - Creates a reply to a comment and publishes it
     */
    fun replyToComment(
        conversationid: String,
        replyto: String, // The unique comment ID we will reply to.
        request: CreateCommentRequest
    ): Single<Comment>

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
        direction: DirectionType? = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
        sort: SortType? = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies"
        includechildren: Boolean? = null, // OPTIONAL, defaults to false
        includeinactive: Boolean? = null // OPTIONAL, defaults to false
    ): Single<ListComments>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#b7c10a98-f5cd-4ed5-8fcc-aa3440cd4233
     * - Get a COMMENT by ID
     */
    fun getComment(
        conversationid: String,
        commentid: String
    ): Single<Comment>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/
     * - https://apiref.sportstalk247.com/?version=latest#98744685-35c9-4293-a082-594cb7a6ec76
     * - Get a list of comments within a conversation
     */
    fun listComments(
        conversationid: String, cursor: String? = null, // OPTIONAL
        limit: Int? = null, // OPTIONAL
        direction: DirectionType? = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
        sort: SortType? = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies"
        includechildren: Boolean? = null, // OPTIONAL, defaults to false
        includeinactive: Boolean? = null // OPTIONAL, defaults to false
    ): Single<ListComments>

    /*
     * [GET] /{{api_appid}}/comment/conversations/{{comment_conversation_id}}/repliesbyparentidbatch?childlimit=&parentids=&includeinactive=
     * - https://apiref.sportstalk247.com/?version=latest#b2d397cf-5d25-4752-b96e-71374b4799bb
     * - Get a list of replies to multiple parent Comments
     */
    fun listRepliesBatch(
        conversationid: String,
        childlimit: Int? = null,    // OPTIONAL, default = 50
        parentids: List<String>,    // A comma delimited list of parentids, up to 30.
        includeinactive: Boolean? = null,   // OPTIONAL, default = false. If true, replies that are flagged or rejected by a moderator are excluded from results. Set it to false to receive rejected and flagged replies.
    ): Single<ListRepliesBatchResponse>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/react
     * - https://apiref.sportstalk247.com/?version=latest#df659fc4-0bb8-4d93-845d-c61579a1f0f8
     * - Adds or removes a reaction to a comment
     */
    fun reactToComment(
        conversationid: String,
        commentid: String,
        request: ReactToCommentRequest
    ): Single<Comment>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/vote
     * - https://apiref.sportstalk247.com/?version=latest#82ffbc3a-01fe-4f1d-a7b1-62440179dfa5
     * - UPVOTE, DOWNVOTE, or REMOVE VOTE
     */
    fun voteOnComment(
        conversationid: String,
        commentid: String,
        request: VoteOnCommentRequest
    ): Single<Comment>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/report
     * - https://apiref.sportstalk247.com/?version=latest#8b48a6e4-8fda-4a3d-b172-f4db50fb0a40
     * - REPORTS a comment to the moderation team.
     */
    fun reportComment(
        conversationid: String,
        commentid: String,
        request: ReportCommentRequest
    ): Single<Comment>

    /**
     * [PUT] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#b02ee426-5a84-4203-93b4-989ad43fe227
     * - UPDATES the contents of an existing comment
     */
    fun updateComment(
        conversationid: String,
        commentid: String,
        request: UpdateCommentRequest
    ): Single<Comment>

    /**
     * [POST] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}/setdeleted?userid=&deleted=&permanentifnoreplies=
     * - https://apiref.sportstalk247.com/?version=latest#5e14e5ea-e8b6-46e0-9cb8-263f695ea652
     * - Set Deleted (LOGICAL DELETE)
     */
    fun flagCommentLogicallyDeleted(
        conversationid: String,
        commentid: String,
        userid: String,
        deleted: Boolean,   // Set to true or false to flag the comment as deleted.
        permanentifnoreplies: Boolean? = false,
    ): Single<DeleteCommentResponse>

    /**
     * [DEL] /{{api_appid}}/comment/conversations/{{conversation_id}}/comments/{{comment_id}}
     * - https://apiref.sportstalk247.com/?version=latest#5e14e5ea-e8b6-46e0-9cb8-263f695ea652
     * - Flag Comment As Deleted
     */
    fun permanentlyDeleteComment(
        conversationid: String,
        commentid: String
    ): Single<DeleteCommentResponse>

    /**
     * [DEL] /{{api_appid}}/comment/conversations/{{conversation_id}}
     * - https://apiref.sportstalk247.com/?version=latest#a46d3b6b-2537-4200-9637-cefea9dce555
     * - DELETES a Conversation, all Comments and Replies
     */
    fun deleteConversation(
        conversationid: String
    ): Single<DeleteConversationResponse>

}