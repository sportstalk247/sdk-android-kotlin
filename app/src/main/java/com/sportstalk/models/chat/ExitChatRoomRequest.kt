package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExitChatRoomRequest(
        val userid: String
)