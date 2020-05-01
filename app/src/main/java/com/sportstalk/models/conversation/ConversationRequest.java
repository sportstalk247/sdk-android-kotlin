package com.sportstalk.models.conversation;

import com.sportstalk.models.common.ListRequest;

/**
 * This is the class describing different options for requesting conversations.
 * Currently, only requesting specific propertyIDs is allowed, however this may be extended in future updates to Sportstalk.
 */
public class ConversationRequest extends ListRequest {

    private String propertyId;

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
}
