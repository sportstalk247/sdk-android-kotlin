package com.sportstalk.api

import com.sportstalk.api.service.ChatModerationService
import com.sportstalk.api.service.ChatService
import com.sportstalk.models.chat.ChatRoom

interface ChatClient: ChatService, ChatModerationService {
    var currentRoom: ChatRoom?
    var defaultImageBannerUrl: String?
}