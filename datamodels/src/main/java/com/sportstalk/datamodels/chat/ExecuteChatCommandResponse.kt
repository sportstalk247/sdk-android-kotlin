package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExecuteChatCommandResponse(
        /** [Kind] */
        val kind: String? = null /* "chat.executecommand" */,
        val message: String? = null, // Purge User Messages
        val op: String? = null /* "speech"|"action" */,
        val room: ChatRoom? = null,
        val speech: ChatEvent? = null,
        val action: ChatEvent? = null
)