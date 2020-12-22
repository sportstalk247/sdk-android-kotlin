package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class BounceUserResponse(
        val kind: String? = null, // "chat.bounceuser"
        val event: ChatEvent? = null,
        val room: ChatRoom? = null
)