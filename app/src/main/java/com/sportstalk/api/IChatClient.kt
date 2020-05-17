package com.sportstalk.api

import com.sportstalk.models.chat.ChatRoom

interface IChatClient: ChatService {
    var currentRoom: ChatRoom?
    var defaultImageBannerUrl: String?
}