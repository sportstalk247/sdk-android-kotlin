package com.sportstalk.api.conversation;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.conversation.Comment;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationResponse;

import java.util.List;

public interface IConversationModerationManager {

    public List<Comment> getModerationQueue();
    public ApiResult rejectComment(Comment comment);
    public ApiResult approveComment(Comment comment);


}
