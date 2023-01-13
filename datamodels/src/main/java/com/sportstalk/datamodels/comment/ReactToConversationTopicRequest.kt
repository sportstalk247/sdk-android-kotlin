package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ReactToConversationTopicRequest(
    val userid: String,
    val reaction: String,   // ex. "like"(ReactionType.LIKE)
    val reacted: Boolean,
)
