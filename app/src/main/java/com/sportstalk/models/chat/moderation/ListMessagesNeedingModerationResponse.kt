package com.sportstalk.models.chat.moderation

import com.sportstalk.models.Kind
import com.sportstalk.models.chat.ChatEvent
import kotlinx.serialization.Serializable

@Serializable
data class ListMessagesNeedingModerationResponse(
        /** [Kind] */
        val kind: String? = null,
        val events: List<ChatEvent> = listOf()
)