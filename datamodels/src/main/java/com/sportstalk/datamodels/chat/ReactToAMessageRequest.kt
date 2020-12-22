package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ReactToAMessageRequest(
        val userid: String,
        val reaction: String,
        val reacted: Boolean
)