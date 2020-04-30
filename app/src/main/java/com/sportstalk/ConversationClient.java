package com.sportstalk;

import com.sportstalk.api.conversation.ICommentManager;
import com.sportstalk.api.conversation.IConversationClient;
import com.sportstalk.api.conversation.IConversationManager;
import com.sportstalk.impl.rest.RestfulCommentManager;
import com.sportstalk.impl.rest.RestfulConversationManager;
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

public class ConversationClient implements IConversationClient {

    private static ConversationClient conversationClient;

    private SportsTalkConfig sportsTalkConfig;

    private ICommentManager commentManager;

    private IConversationManager conversationManager;

    private User user;

    private Conversation conversation;

    private ConversationClient(SportsTalkConfig sportsTalkConfig, Conversation conversation) {
        this.conversation = conversation;
        setConfig(sportsTalkConfig);
    }

    public static ConversationClient create(SportsTalkConfig sportsTalkConfig, Conversation initialConversation, IConversationManager commentManager, IConversationManager conversationManager) {
        if (conversationClient == null)
            conversationClient = new ConversationClient(sportsTalkConfig, initialConversation);
        if (initialConversation != null)
            conversationClient.setConversation(initialConversation);
        return conversationClient;
    }

    public static ConversationClient create(SportsTalkConfig sportsTalkConfig) {
        if (conversationClient == null)
            conversationClient = new ConversationClient(sportsTalkConfig, null);
        return conversationClient;
    }

    @Override
    public SportsTalkConfig getConfig() {
        return sportsTalkConfig;
    }

    @Override
    public void setConfig(SportsTalkConfig config) {
        this.sportsTalkConfig = config;
        if (commentManager == null)
            commentManager = new RestfulCommentManager(conversation, config);
        if (conversationManager == null)
            conversationManager = new RestfulConversationManager(config);
        conversationManager.setConfig(this.sportsTalkConfig);
        commentManager.setConfig(this.sportsTalkConfig);
    }

    @Override
    public Conversation createConversation(Conversation conversation, boolean status) {
        return conversationManager.createConversation(conversation);
    }

    @Override
    public Conversation setConversation(Conversation conversation) {
        commentManager.setConversation(conversation);
        return this.commentManager.getConversation();
    }

    @Override
    public Conversation getConversation(Conversation conversation) {
        return conversationManager.getConversation(conversation);
    }

    @Override
    public List<Conversation> getConversationsByProperty(String property) {
        return conversationManager.getConversationsByProperty(property);
    }

    @Override
    public ConversationDeletionResponse deleteConversation(Conversation conversation) {
        return conversationManager.deleteConversation(conversation);
    }

    @Override
    public Comment makeComment(String replyTo, Comment comment) {
        Comment replyToComment = new Comment();
        replyToComment.setReplyTo(replyTo);
        return commentManager.create(comment, replyToComment);
    }

    @Override
    public Comment getComment(Comment comment) {
        return commentManager.get(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentManager.delete(comment);
    }

    @Override
    public Comment updateComment(Comment comment) {
        return commentManager.update(comment);
    }

    @Override
    public Comment reactToComment(Comment comment, Reaction reaction) {
        ReactionResponse reactionResponse = commentManager.react(comment, reaction, true);
        return comment;
    }

    @Override
    public Comment voteOnComment(Comment comment, Vote vote) {
        commentManager.vote(comment, vote);
        return comment;
    }

    @Override
    public Comment reportComment(Comment comment, ReportType reportType) {
        commentManager.report(comment, reportType);
        return comment;
    }

    @Override
    public List<Comment> getCommentReplies(Comment comment, CommentRequest request) {
        return commentManager.getReplies(comment, request);
    }

    @Override
    public List<Comment> getComments(CommentRequest commentRequest, Conversation conversation) {
        return commentManager.getComments(commentRequest, conversation).getComments();
    }

    public ConversationListResponse listConversations() {
        return conversationManager.listConversations();
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

}
