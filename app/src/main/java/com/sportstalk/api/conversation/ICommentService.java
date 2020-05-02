package com.sportstalk.api.conversation;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.common.IUserConfigurable;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.CommentDeletionResponse;
import com.sportstalk.models.conversation.CommentRequest;
import com.sportstalk.models.conversation.Commentary;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ReactionResponse;
import com.sportstalk.models.conversation.Vote;

import java.util.List;

public interface ICommentService extends ISportsTalkConfigurable, IUserConfigurable {

    Conversation setConversation(Conversation conversation);

    Comment create(Comment comment, Comment replyTo);

    Comment get(Comment comment);

    CommentDeletionResponse delete(Comment comment);

    Comment update(Comment comment);

    void vote(Comment comment, Vote vote);

    void report(Comment comment, ReportType reportType);

    ReactionResponse react(Comment comment, Reaction reaction, boolean enabled);

    List<Comment> getReplies(Comment comment, CommentRequest commentRequest);

    Commentary getComments(CommentRequest commentRequest, Conversation conversation);

    Conversation getConversation();

}
