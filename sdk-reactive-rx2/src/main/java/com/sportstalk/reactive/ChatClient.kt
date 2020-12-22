package com.sportstalk.reactive

import com.sportstalk.datamodels.chat.ChatRoom
import com.sportstalk.reactive.service.ChatService

interface ChatClient: ChatService {
    var currentRoom: ChatRoom?
    var defaultImageBannerUrl: String?
}