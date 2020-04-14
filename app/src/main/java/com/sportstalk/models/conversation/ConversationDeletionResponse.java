package com.sportstalk.models.conversation;

import com.sportstalk.models.common.Kind;

public class ConversationDeletionResponse extends HasConversationID {
    private Kind deletedConversation;
    private String userid;
    private int deletedConversations;
    private int deletedComments;

    public Kind getDeletedConversation() {
        return deletedConversation;
    }

    public void setDeletedConversation(Kind deletedConversation) {
        this.deletedConversation = deletedConversation;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getDeletedConversations() {
        return deletedConversations;
    }

    public void setDeletedConversations(int deletedConversations) {
        this.deletedConversations = deletedConversations;
    }

    public int getDeletedComments() {
        return deletedComments;
    }

    public void setDeletedComments(int deletedComments) {
        this.deletedComments = deletedComments;
    }

}
