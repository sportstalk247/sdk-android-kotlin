package com.sportstalk.api.conversation;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.models.conversation.Conversation;
import com.sportstalk.models.conversation.ConversationDeletionResponse;
import com.sportstalk.models.conversation.ConversationListResponse;
import com.sportstalk.models.conversation.ConversationResponse;

import java.util.List;

public interface IConversationService extends ISportsTalkConfigurable {
    ConversationResponse createConversation(Conversation conversation);

    ConversationResponse getConversation(Conversation conversation);

    List<Conversation> getConversationsByProperty(String property);

    ConversationListResponse listConversations();

    ConversationDeletionResponse deleteConversation(Conversation conversation);
}
