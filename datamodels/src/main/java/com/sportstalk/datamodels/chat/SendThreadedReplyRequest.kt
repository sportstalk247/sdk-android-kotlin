package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class SendThreadedReplyRequest(
        val body: String,
        val userid: String,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null
)