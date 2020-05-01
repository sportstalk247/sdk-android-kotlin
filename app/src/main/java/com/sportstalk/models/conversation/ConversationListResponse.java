package com.sportstalk.models.conversation;

import java.util.List;

/**
 * This is the class that describes the server response for conversation lists.
 */
public class ConversationListResponse extends ListResponse {
    List<Conversation> conversations;

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
