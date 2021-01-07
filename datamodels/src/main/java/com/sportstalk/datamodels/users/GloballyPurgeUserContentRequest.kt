package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class GloballyPurgeUserContentRequest(
        private val banned: Boolean
)