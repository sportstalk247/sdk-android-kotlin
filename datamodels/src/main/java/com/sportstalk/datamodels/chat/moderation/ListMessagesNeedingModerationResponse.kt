package com.sportstalk.datamodels.chat.moderation

import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.chat.ChatEvent
import kotlinx.serialization.Serializable

@Serializable
data class ListMessagesNeedingModerationResponse(
        /** [Kind] */
        val kind: String? = null,
        val events: List<ChatEvent> = listOf()
)