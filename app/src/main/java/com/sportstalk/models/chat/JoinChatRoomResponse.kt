package com.sportstalk.models.chat

import com.sportstalk.models.users.User
import kotlinx.serialization.Serializable

@Serializable
data class JoinChatRoomResponse(
        val kind: String? = null /* "chat.joinroom" */,
        val user: User? = null,
        val room: ChatRoom? = null
)