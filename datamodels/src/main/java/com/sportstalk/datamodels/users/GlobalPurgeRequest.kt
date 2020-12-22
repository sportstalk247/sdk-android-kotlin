package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class GlobalPurgeRequest(
        private val banned: Boolean
)