package com.sportstalk.api.conversation;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.common.IUserConfigurable;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentDeletionResponse;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Commentary;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ReactionResponse;
import com.sportstalk.models.conversation.Vote;

import java.util.List;

public interface ICommentManager extends ISportsTalkConfigurable, IUserConfigurable {

    public Conversation setConversation(Conversation conversation);
    public Comment create(Comment comment, Comment replyTo);
    public Comment get(Comment comment);
    public CommentDeletionResponse delete(Comment comment);
    public Comment update(Comment comment);
    public void vote(Comment comment, Vote vote);
    public void report(Comment comment, ReportType reportType);
    public ReactionResponse react(Comment comment, Reaction reaction, boolean enabled);
    public List<Comment> getReplies(Comment comment, CommentRequest commentRequest);
    public Commentary getComments(CommentRequest commentRequest, Conversation conversation);
    public Conversation getConversation();

}
