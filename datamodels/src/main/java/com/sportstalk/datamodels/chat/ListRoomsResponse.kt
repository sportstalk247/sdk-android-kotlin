package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListRoomsResponse(
        /** [Kind] */
        val kind: String? = null /* "list.chatrooms" */,
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Long? = null,
        val rooms: List<ChatRoom> = listOf()
)