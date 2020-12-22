package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCommentRequest(
        val userid: String,
        val body: String
)