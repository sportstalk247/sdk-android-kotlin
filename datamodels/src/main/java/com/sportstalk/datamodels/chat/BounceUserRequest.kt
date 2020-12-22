package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class BounceUserRequest(
        val userid: String,
        val bounce: Boolean,
        val announcement: String? = null
)