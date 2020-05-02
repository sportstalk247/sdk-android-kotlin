package com.sportstalk.models.conversation;

import com.sportstalk.models.common.Kind;


public class CommentDeletionResponse {

    /**
     * return a delete comment type
     * @return
     */
    public Kind getDeleteComment() {
        return deleteComment;
    }

    /**
     * sets delete comment type
     * @param deleteComment
     */
    public void setDeleteComment(Kind deleteComment) {
        this.deleteComment = deleteComment;
    }

    /**
     * return  conversation id
     * @return
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * sets conversation id
     * @param conversationId
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    private Kind deleteComment;
    private String conversationId;

}
