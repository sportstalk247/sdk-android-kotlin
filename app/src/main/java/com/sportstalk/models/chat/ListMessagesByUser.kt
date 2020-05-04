package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListMessagesByUser(
        val kind: String? = null,
        val cursor: String? = null,
        val events: List<ChatEvent> = listOf()
)