package com.sportstalk.models.chat.moderation

import kotlinx.serialization.Serializable

@Serializable
data class ApproveMessageRequest(
        val approve: Boolean
)