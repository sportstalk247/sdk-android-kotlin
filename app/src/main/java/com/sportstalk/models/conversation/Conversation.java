package com.sportstalk.models.conversation;

import com.sportstalk.models.common.ModerationType;

import java.util.List;

public class Conversation extends HasConversationID {

    private String ownerUserId;
 private String property;
 private ModerationType moderationType;
 private int maxReports;
 private String title;
 private int maxCommentLen;
 private boolean conversationIsOpen;
 private List<String> tags;
 private String udf1;
 private String udf2;
 private String customId;

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public ModerationType getModerationType() {
        return moderationType;
    }

    public void setModerationType(ModerationType moderationType) {
        this.moderationType = moderationType;
    }

    public int getMaxReports() {
        return maxReports;
    }

    public void setMaxReports(int maxReports) {
        this.maxReports = maxReports;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxCommentLen() {
        return maxCommentLen;
    }

    public void setMaxCommentLen(int maxCommentLen) {
        this.maxCommentLen = maxCommentLen;
    }

    public boolean isConversationIsOpen() {
        return conversationIsOpen;
    }

    public void setConversationIsOpen(boolean conversationIsOpen) {
        this.conversationIsOpen = conversationIsOpen;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUdf1() {
        return udf1;
    }

    public void setUdf1(String udf1) {
        this.udf1 = udf1;
    }

    public String getUdf2() {
        return udf2;
    }

    public void setUdf2(String udf2) {
        this.udf2 = udf2;
    }

}
