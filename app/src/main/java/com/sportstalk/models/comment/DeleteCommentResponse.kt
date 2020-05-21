package com.sportstalk.models.comment

import kotlinx.serialization.Serializable

@Serializable
data class DeleteCommentResponse(
        val kind: String? = null, // "delete.comment"
        val permanentdelete: Boolean? = null,
        val comment: Comment? = null
)