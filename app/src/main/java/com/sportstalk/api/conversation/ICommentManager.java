package com.sportstalk.api.conversation;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.common.IUserConfigurable;
import com.sportstalk.error.RequireUserException;
import com.sportstalk.error.SettingsException;
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

public interface ICommentManager extends ISportsTalkConfigurable, IUserConfigurable {

    Conversation setConversation(Conversation conversation);

    Comment create(Comment comment, Comment replyTo);

    Comment get(Comment comment);

    CommentDeletionResponse delete(Comment comment);

    Comment update(Comment comment);

    Comment vote(Comment comment, Vote vote);

    Comment report(Comment comment, ReportType reportType);

    ReactionResponse react(Comment comment, Reaction reaction, boolean enabled) throws SettingsException, RequireUserException;

    List<Comment> getReplies(Comment comment, CommentRequest commentRequest);

    Commentary getComments(CommentRequest commentRequest, Conversation conversation);

    Conversation getConversation();

}
