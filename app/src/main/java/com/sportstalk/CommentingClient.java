package com.sportstalk;
import com.sportstalk.api.conversation.ICommentService;
import com.sportstalk.api.conversation.ICommentingClient;
import com.sportstalk.api.conversation.IConversationService;
import com.sportstalk.impl.rest.RestfulCommentService;
import com.sportstalk.impl.rest.RestfulConversationService;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.ReactionResponse;
import com.sportstalk.models.conversation.Vote;

import java.util.List;

/**
 * This is the primary class for commenting.
 * For most end-user cases, you will be using this API.
 */
public class CommentingClient implements ICommentingClient {

    /**
     * Holds the configuration
     */
    private SportsTalkConfig sportsTalkConfig;

    /**
     * This service maintains the state of specific conversation and manages commenting on it.
     * The client defers commenting to this service.
     */
    private ICommentService commentService;

    /**
     * Conversation Service handles the creation, listing, and mutating of conversations.
     */
    private IConversationService conversationService;


    /**
     * Constructor is private to avoid creating dependencies on specific implementations of this API.
     * Please use CommentingClient.create()
     * @param sportsTalkConfig
     */
    private CommentingClient(SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    /**
     * Use this to create new Commenting Clients.
     * @param sportsTalkConfig The configuration object
     * @param initialConversation optional - if not null, the default conversation will be set to this conversation for commenting.
     * @return
     */
    public static CommentingClient create(SportsTalkConfig sportsTalkConfig, Conversation initialConversation) {
        CommentingClient conversationClient = new CommentingClient(sportsTalkConfig);
        if (initialConversation != null)
            conversationClient.setConversation(initialConversation);
        return conversationClient;
    }

    /**
     * Use this to create new Commenting Clients.
     * @param sportsTalkConfig The configuration object
     * @return
     */
    public static CommentingClient create(SportsTalkConfig sportsTalkConfig) {
        return new CommentingClient(sportsTalkConfig);
    }

    /**
     * Get the current sportstalk configuration
     * @return
     */
    @Override
    public SportsTalkConfig getConfig() {
        return sportsTalkConfig;
    }

    /**
     * Update the configuration.  The client will pass this configuration to sub-services which may cause those services to reset.
     * @param config
     */
    @Override
    public void setConfig(SportsTalkConfig config) {
        this.sportsTalkConfig = config;
        if (commentService == null)
            commentService = new RestfulCommentService(config);
        if (conversationService == null)
            conversationService = new RestfulConversationService(config);
        conversationService.setConfig(this.sportsTalkConfig);
        commentService.setConfig(this.sportsTalkConfig);
    }

    /**
     * Create a new conversation
     * @param conversation
     * @return
     */
    @Override
    public Conversation createConversation(Conversation conversation) {
        return conversationService.createConversation(conversation);
    }

    /**
     *
     * @param conversation
     * @return
     */
    @Override
    public Conversation setConversation(Conversation conversation) {
        commentService.setConversation(conversation);
        return this.commentService.getConversation();
    }

    /**
     * Retrieves a conversation from the server.
     * @param conversation
     * @return
     */
    @Override
    public Conversation getConversation(Conversation conversation) {
        return conversationService.getConversation(conversation);
    }

    /**
     * Get a conversation list based on the property, e.g. UAT, DEMO, DEV.
     * Property names depend on your company's naming policy. Please check your account in the dashboard.
     * @param property
     * @return
     */
    @Override
    public List<Conversation> getConversationsByProperty(String property) {
        return conversationService.getConversationsByProperty(property);
    }

    /**
     * Deletes a converation.
     * @param conversation
     * @return A response from the server, if the request was successful and updates. See ConversationDeletionResponse
     */
    @Override
    public ConversationDeletionResponse deleteConversation(Conversation conversation) {
        return conversationService.deleteConversation(conversation);
    }

    /**
     * Make a comment.
     * @param comment
     * @return
     */
    @Override
    public Comment publishComment(Comment comment) {
        return commentService.create(comment,null);
    }

    /**
     * Make a comment.
     * @param comment
     * @param replyTo Optional, the ID of the comment we want to reply to.
     * @return
     */
    @Override
    public Comment publishComment(Comment comment, String replyTo) {
        Comment replyToComment = new Comment();
        if(replyTo!=null) {
            replyToComment.setReplyTo(replyTo);
        }
        return commentService.create(comment, replyToComment);
    }

    /**
     * Get a specific comment.
     * @param comment
     * @return
     */
    @Override
    public Comment getComment(Comment comment) {
        return commentService.get(comment);
    }

    /**
     * Deletes an existing comment.
     * @param comment
     */
    @Override
    public void deleteComment(Comment comment) {
        commentService.delete(comment);
    }

    /**
     * Update a comment - "edit"
     * @param comment
     * @return
     */
    @Override
    public Comment updateComment(Comment comment) {
        return commentService.update(comment);
    }

    /**
     * React to a comment.
     * @param comment
     * @param reaction
     * @return
     */
    @Override
    public Comment reactToComment(Comment comment, Reaction reaction) {
        ReactionResponse reactionResponse = commentService.react(comment, reaction, true);
        return comment;
    }

    /**
     * Vote on a comment.
     * @param comment
     * @param vote
     * @return
     */
    @Override
    public Comment voteOnComment(Comment comment, Vote vote) {
        commentService.vote(comment, vote);
        return comment;
    }

    /**
     * Report a comment for violating community policies.
     * @param comment
     * @param reportType
     * @return
     */
    @Override
    public Comment reportComment(Comment comment, ReportType reportType) {
        commentService.report(comment, reportType);
        return comment;
    }

    /**
     * Gets the replies to a specific comment.
     * @param comment
     * @param request
     * @return
     */
    @Override
    public List<Comment> getCommentReplies(Comment comment, CommentRequest request) {
        return commentService.getReplies(comment, request);
    }

    /**
     * getComments for a specific conversation
     * @param commentRequest
     * @param conversation
     * @return
     */
    @Override
    public List<Comment> getComments(CommentRequest commentRequest, Conversation conversation) {
        return commentService.getComments(commentRequest, conversation).getComments();
    }

    /**
     * List available conversations.
     * @return
     */
    public ConversationListResponse listConversations() {
        return conversationService.listConversations();
    }

    /**
     * Set the user
     * @param user
     */
    @Override
    public void setUser(User user) {
        this.commentService.setUser(user);
    }

}

