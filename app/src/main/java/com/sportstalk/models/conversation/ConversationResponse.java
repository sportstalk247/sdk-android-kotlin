package com.sportstalk.models.conversation;

import com.sportstalk.models.common.Kind;

/**
 *
 */
public class ConversationResponse extends Conversation {

    private Kind kind;
    private String appId;
    private int commentCount;

    /**
     * Ket the kind of response.
     * @return
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Set the 'kind', there is no reason to adjust this, ever. It is set by the server.
     * @param kind
     */
    public void setKind(Kind kind) {
        this.kind = kind;
    }

    /**
     * Get the number of comments in a conversation.
     * @return
     */
    public int getCommentCount() {
        return commentCount;
    }

    /**
     * Set comment count.  This should be set by the server, but you may manually mutate it for display if desired.
     * @param commentCount
     */
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    /**
     * Get the appID for this conversation.  Mostly useful if you are managing conversations across multiple Sportstalk Applications.
     * @return
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Set the appID.  You should probably not do this as a client, as it is set by the server and cannot be updated.
     * Only used as part of parsing server responses.
     * @param appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

}
