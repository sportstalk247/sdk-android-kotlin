package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class SendQuotedReplyRequest(
        val userid: String,
        val body: String,
        val customid: String? = null,
        val custompayload: String? = null,
        val customfield1: String? = null,
        val customfield2: String? = null,
        val customtags: List<String>? = null
)