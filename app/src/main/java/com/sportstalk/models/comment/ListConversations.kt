package com.sportstalk.models.comment

import kotlinx.serialization.Serializable

@Serializable
data class ListConversations(
        val kind: String? = null, /* "list.commentconversations" */
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Int? = null,
        val conversations: List<Conversation> = listOf()
)