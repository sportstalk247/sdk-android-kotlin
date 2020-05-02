package com.sportstalk.models.conversation;
import java.util.List;

/**
 * Wrapper class for a list of comments and the conversation they are part of.
 */
public class Commentary {

    private Conversation conversation;
    private List<Comment> comments;

    /**
     * Gets the converation
     * @return
     */
    public Conversation getConversation() {
        return conversation;
    }

    /**
     * Sets the conversation. You probably don't need to do this.  This should be set by a server response.
     * @param conversation
     */
    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    /**
     * Get the comments that were part of the commentary.
     * @return
     */
    public List<Comment> getComments() {
        return comments;
    }

    /**
     * Set the comments. You probably don't need to do this, these comments are usually part of a server response and should have already been set.
     * @param comments
     */
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
