package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class GetUpdatesResponse(
        /** [Kind] */
        val kind: String? = null /* "list.chatevents" */,
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Long? = null,
        val events: List<ChatEvent> = listOf()
)