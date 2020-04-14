package com.sportstalk.models.conversation;

import java.util.List;

public class ConversationListResponse extends ListResponse  {
    List<Conversation> conversations;

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
