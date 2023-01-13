package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ApproveMessageRequest(
    val approve: Boolean,    // If set to true, approves the message. Otherwise, reject.
)
