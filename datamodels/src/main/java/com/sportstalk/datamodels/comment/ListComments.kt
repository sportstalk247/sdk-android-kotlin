package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ListComments(
        val kind: String? = null, // "list.comments"
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Int? = null,
        val conversation: Conversation? = null,
        val comments: List<Comment> = listOf()
)