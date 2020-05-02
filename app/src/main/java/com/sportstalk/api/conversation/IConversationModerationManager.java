package com.sportstalk.api.conversation;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.conversation.Comment;

import java.util.List;

public interface IConversationModerationManager {

    List<Comment> getModerationQueue();

    ApiResult rejectComment(Comment comment);

    ApiResult approveComment(Comment comment);


}
