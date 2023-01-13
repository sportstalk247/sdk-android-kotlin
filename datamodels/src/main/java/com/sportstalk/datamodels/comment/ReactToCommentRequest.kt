package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ReactToCommentRequest(
        val userid: String,
        val reaction: String,   // ex. "like"(Reaction.Like)
        val reacted: Boolean,
)