package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ReactToAMessageRequest(
        val userid: String,
        val reaction: String,
        val reacted: Boolean
)