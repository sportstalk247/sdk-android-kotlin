package com.sportstalk.models.conversation;

import com.sportstalk.models.common.Kind;

public class ConversationResponse extends Conversation {

    private Kind kind;
    private String appId;
    private int commentCount;

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

}
