package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class DeleteChatRoomResponse(
        val kind: String? = null /* "deleted.room" */,
        val deletedEventsCount: Long? = null /* "deleted.room" */,
        val room: ChatRoom? = null
)