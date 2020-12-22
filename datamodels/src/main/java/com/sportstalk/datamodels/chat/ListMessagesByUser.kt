package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListMessagesByUser(
        val kind: String? = null,
        val cursor: String? = null,
        val events: List<ChatEvent> = listOf()
)