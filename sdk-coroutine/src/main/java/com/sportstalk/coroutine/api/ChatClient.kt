package com.sportstalk.coroutine.api

import com.sportstalk.coroutine.service.ChatModerationService
import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.datamodels.chat.ChatRoom

interface ChatClient: ChatService, ChatModerationService {
    var currentRoom: ChatRoom?
    var defaultImageBannerUrl: String?
}