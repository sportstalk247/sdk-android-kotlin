package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class DeleteChatRoomResponse(
        /** [Kind] */
        val kind: String? = null /* "deleted.room" */,
        val deletedEventsCount: Long? = null,
        val room: ChatRoom? = null
)