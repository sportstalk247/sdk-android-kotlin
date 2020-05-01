package com.sportstalk.models.conversation;

import com.sportstalk.api.conversation.HasConversationID;
import com.sportstalk.models.common.Kind;

/**
 * This is the class describing the data model for the server response that is sent when a conversation is deleted.
 *
 */
public class ConversationDeletionResponse implements HasConversationID {
    private Kind deletedConversation;
    private String userid;
    private int deletedConversations;
    private int deletedComments;
    private String conversationId;

    @Override
    public String getConversationId() {
        return conversationId;
    }

    @Override
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

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
