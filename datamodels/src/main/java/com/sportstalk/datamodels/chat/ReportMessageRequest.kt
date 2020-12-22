package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ReportMessageRequest(
        val reporttype: String,
        val userid: String
)