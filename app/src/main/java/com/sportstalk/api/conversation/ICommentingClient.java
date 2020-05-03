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

public interface ICommentingClient extends ISportsTalkConfigurable, IUserConfigurable {
    SportsTalkConfig getConfig();

    Conversation createConversation(Conversation conversation);

    Conversation setConversation(Conversation conversation);

    Conversation getConversation(Conversation conversation);

    List<Conversation> getConversationsByProperty(String property);

    ConversationDeletionResponse deleteConversation(Conversation conversation);

    Comment publishComment(Comment comment);
    Comment publishComment(Comment comment, String replyTo);

    Comment getComment(Comment comment);

    void deleteComment(Comment comment);

    Comment updateComment(Comment comment);

    Comment reactToComment(Comment comment, Reaction reaction);

    Comment voteOnComment(Comment comment, Vote vote);

    Comment reportComment(Comment comment, ReportType reportType);

    List<Comment> getCommentReplies(Comment comment, CommentRequest request);

    List<Comment> getComments(CommentRequest commentRequest, Conversation conversation);
}

