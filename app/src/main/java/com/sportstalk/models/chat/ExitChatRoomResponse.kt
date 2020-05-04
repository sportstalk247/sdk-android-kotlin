package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExitChatRoomResponse(
        val kind: String? = null /* "chat.joinroom" */
)