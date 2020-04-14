package com.sportstalk.api.conversation;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.common.IUserConfigurable;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.Vote;

import java.util.List;

public interface IConversationClient extends ISportsTalkConfigurable, IUserConfigurable {
    public SportsTalkConfig getConfig();
    public Conversation createConversation(Conversation conversation, boolean status);
    public Conversation setConversation(Conversation conversation);
    public Conversation getConversation(Conversation conversation);
    public List<Conversation> getConversationsByProperty(String property);
    public ConversationDeletionResponse deleteConversation(Conversation conversation);
    public Comment makeComment(String replyTo, Comment comment);
    public Comment getComment(Comment comment);
    public void deleteComment(Comment comment);
    public Comment updateComment(Comment comment);
    public Comment reactToComment(Comment comment, Reaction reaction);
    public Comment voteOnComment(Comment comment, Vote vote);
    public Comment reportComment(Comment comment, ReportType reportType);
    public List<Comment> getCommentReplies(Comment comment, CommentRequest request);
    public List<Comment> getComments(CommentRequest commentRequest, Conversation conversation);
}
