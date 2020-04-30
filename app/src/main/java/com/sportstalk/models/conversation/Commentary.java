package com.sportstalk.models.conversation;

import java.util.List;

public class Commentary {

    private Conversation conversation;
    private List<Comment> comments;

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
