package com.sportstalk.models.chat;

public class CommandOptions {

    private String customType;
    private String customId;
    private String replyTo;
    private String customPayload;

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getCustomPayload() {
        return customPayload;
    }

    public void setCustomPayload(String customPayload) {
        this.customPayload = customPayload;
    }
}
