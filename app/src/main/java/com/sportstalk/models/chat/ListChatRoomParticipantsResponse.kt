package com.sportstalk.models.chat

import com.sportstalk.models.users.User
import kotlinx.serialization.Serializable

@Serializable
data class ListChatRoomParticipantsResponse(
        val kind: String? = null /* "list.chatparticipants" */,
        val cursor: String? = null,
        val participants: List<ChatRoomParticipant> = listOf()
)

@Serializable
data class ChatRoomParticipant(
        val kind: String? = null /* "chat.participant" */,
        val user: User? = null
)