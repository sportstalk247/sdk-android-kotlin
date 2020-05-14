package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListRoomsResponse(
        val kind: String? = null /* "list.chatrooms" */,
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Long? = null,
        val rooms: List<ChatRoom> = listOf()
)