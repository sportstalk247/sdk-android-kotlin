package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExecuteChatCommandRequest(
        val command: String,
        val userid: String,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null
)