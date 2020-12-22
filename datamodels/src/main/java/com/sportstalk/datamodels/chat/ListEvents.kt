package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListEvents(
        /** [Kind] */
        val kind: String? = null /* "list.chatevents" */,
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Long? = null,
        val room: ChatRoom? = null,
        val events: List<ChatEvent> = listOf()
)