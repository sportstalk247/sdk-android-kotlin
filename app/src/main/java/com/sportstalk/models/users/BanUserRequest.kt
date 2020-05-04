package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class BanUserRequest(
        val banned: Boolean
)