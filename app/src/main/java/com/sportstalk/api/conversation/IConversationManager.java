package com.sportstalk.api.conversation;

import com.sportstalk.api.common.IConfigurable;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.ConversationRequest;
import com.sportstalk.models.conversation.ConversationResponse;
import com.sportstalk.models.conversation.ConversationDeletionResponse;

import java.util.List;

public interface IConversationManager extends IConfigurable {
    public ConversationResponse createConversation(Conversation conversation);
    public ConversationResponse getConversation(Conversation conversation);
    public List<Conversation> getConversationsByProperty(String property);
    public ConversationListResponse listConversations();
    public ConversationDeletionResponse deleteConversation(Conversation conversation);
}
