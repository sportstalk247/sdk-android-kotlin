package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExecuteChatCommandResponse(
        val kind: String? = null /* "chat.executecommand" */,
        val op: String? = null /* "speech"|"action" */,
        val room: ChatRoom? = null,
        val speech: ChatEvent? = null,
        val action: ChatEvent? = null
)