package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class GlobalPurgeRequest(
        private val banned: Boolean
)