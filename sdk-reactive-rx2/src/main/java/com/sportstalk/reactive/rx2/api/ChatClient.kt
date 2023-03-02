package com.sportstalk.reactive.rx2.api

import com.sportstalk.datamodels.chat.ChatRoom
import com.sportstalk.reactive.rx2.service.ChatModerationService
import com.sportstalk.reactive.rx2.service.ChatService

interface ChatClient: ChatService, ChatModerationService {
    var currentRoom: ChatRoom?
    var defaultImageBannerUrl: String?
}