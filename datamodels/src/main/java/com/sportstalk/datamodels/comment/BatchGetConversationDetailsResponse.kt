package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class BatchGetConversationDetailsResponse(
    val kind: String? = null, /* "list.comment.conversation.details" */
    val itemcount: Long? = null,
    val conversations: List<Conversation> = listOf(),
)
