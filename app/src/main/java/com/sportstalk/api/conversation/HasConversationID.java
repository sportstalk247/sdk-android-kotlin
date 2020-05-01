package com.sportstalk.api.conversation;

/**
 * Interface for objects that reference a specific conversation.
 */
public interface HasConversationID {
    public String getConversationId();
    public void setConversationId(String conversationId);
}
