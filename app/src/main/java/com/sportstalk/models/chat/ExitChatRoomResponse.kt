package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExitChatRoomResponse(
        /** [Kind] */
        val kind: String? = null /* "chat.joinroom" */
)