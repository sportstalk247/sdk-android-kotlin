package com.sportstalk.models.conversation;

import com.sportstalk.api.conversation.HasConversationID;
import com.sportstalk.models.common.ModerationType;

import java.util.List;

/**
 * Conversations are the containers for commenting.
 * You do not need to join a conversation to view its comments.
 */
public class Conversation implements HasConversationID {

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
    private String conversationId;

    @Override
    public String getConversationId() {
        return conversationId;
    }

    @Override
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    /**
     * Some conversations have customIDs, which can be used for querying based on customer-specific IDs
     * @return
     */
    public String getCustomId() {
        return customId;
    }
    /**
     * Some conversations have customIDs, which can be used for querying based on customer-specific IDs
     * This should be set on creation in the dashboard. Setting a customID here will NOT update the conversation on the server.
     * @return
     */
    public void setCustomId(String customId) {
        this.customId = customId;
    }

    /**
     * Get the owner ID of a conversation. May be null.
     * @return
     */
    public String getOwnerUserId() {
        return ownerUserId;
    }

    /**
     * Set the owner ID.  Does NOT update the server, unless you use the updateConversation method on the conversations service.
     * @param ownerUserId
     */
    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    /**
     * Get the property string.  To see the property strings used, check the sportstalk dashboard for your organization.
     * @return
     */
    public String getProperty() {
        return property;
    }

    /**
     * Set the property on this conversation.  This will not update the server or dashboard.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @param property
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Get the moderation type of this conversation
     * @return
     */
    public ModerationType getModerationType() {
        return moderationType;
    }

    /**
     * Set the moderation type.   This will not update the server or dashboard.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @param moderationType
     */
    public void setModerationType(ModerationType moderationType) {
        this.moderationType = moderationType;
    }

    /**
     * gets the max reports before a comment in this conversation is put into the moderation queue.  Default is 3. Minimum is 0.
     * Set the moderation type.   This will not update the server or dashboard.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
    public int getMaxReports() {
        return maxReports;
    }
    /**
     * Sets the max reports before a comment in this conversation is put into the moderation queue.  Default is 3. Minimum is 0.
     * Set the moderation type.   This will not update the server or dashboard.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
    public void setMaxReports(int maxReports) {
        this.maxReports = maxReports;
    }

    /**
     * Get the conversation title.
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the conversation title.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the maximum string length of comments.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
    public int getMaxCommentLen() {
        return maxCommentLen;
    }
    /**
     * Sets the maximum string length of comments.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
    public void setMaxCommentLen(int maxCommentLen) {
        this.maxCommentLen = maxCommentLen;
    }

    /**
     * If true, the conversation is accepting new comments.
     * @return
     */
    public boolean isConversationIsOpen() {
        return conversationIsOpen;
    }
    /**
     * If true, the conversation is accepting new comments.
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
    public void setConversationIsOpen(boolean conversationIsOpen) {
        this.conversationIsOpen = conversationIsOpen;
    }

    /**
     * Get the list of tags for this conversation
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
    public List<String> getTags() {
        return tags;
    }
    /**
     * Set the list of tags for this conversation
     * To update this or any other aspect of a conversation on the server, you must use the updateConversation method on an IConversationService
     * @return
     */
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
