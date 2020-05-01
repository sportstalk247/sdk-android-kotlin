package com.sportstalk.models.conversation;

import com.sportstalk.models.common.Kind;

public class CommentDeletionResponse {

    public Kind getDeleteComment() {
        return deleteComment;
    }

    public void setDeleteComment(Kind deleteComment) {
        this.deleteComment = deleteComment;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    private Kind deleteComment;
    private String conversationId;

}
