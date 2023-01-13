package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class DeleteConversationResponse(
        val kind: String? = null, // "delete.comment"
        val conversationid: String? = null,
        val userid: String? = null,
        val deletedconversations: Long? = null,
        val deletedcomments: Long? = null,
)