package com.sportstalk.datamodels.chat

import com.sportstalk.datamodels.users.User
import kotlinx.serialization.Serializable

@Serializable
data class ListChatRoomParticipantsResponse(
        /** [Kind] */
        val kind: String? = null /* "list.chatparticipants" */,
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Long? = null,
        val participants: List<ChatRoomParticipant> = listOf()
)

@Serializable
data class ChatRoomParticipant(
        /** [Kind] */
        val kind: String? = null /* "chat.participant" */,
        val user: User? = null
)