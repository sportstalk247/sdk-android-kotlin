package com.sportstalk.datamodels.chat.moderation

import kotlinx.serialization.Serializable

@Serializable
data class PurgeUserMessagesRequest(
    val userid: String,
    val byuserid: String,
)