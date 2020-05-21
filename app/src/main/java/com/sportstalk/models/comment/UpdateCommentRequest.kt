package com.sportstalk.models.comment

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCommentRequest(
        val userid: String,
        val body: String
)