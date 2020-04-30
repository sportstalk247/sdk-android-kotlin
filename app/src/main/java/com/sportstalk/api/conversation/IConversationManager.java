package com.sportstalk.api.conversation;

import com.sportstalk.api.common.IConfigurable;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.ConversationResponse;

import java.util.List;

public interface IConversationManager extends IConfigurable {
    ConversationResponse createConversation(Conversation conversation);

    ConversationResponse getConversation(Conversation conversation);

    List<Conversation> getConversationsByProperty(String property);

    ConversationListResponse listConversations();

    ConversationDeletionResponse deleteConversation(Conversation conversation);
}
