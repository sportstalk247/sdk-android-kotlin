package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class ReportMessageRequest(
        val reporttype: String,
        val userid: String
)