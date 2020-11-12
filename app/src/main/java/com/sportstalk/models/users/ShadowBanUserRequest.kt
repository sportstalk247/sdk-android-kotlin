package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class ShadowBanUserRequest(
        val shadowban: Boolean,
        val expireseconds: Long? = null     // in seconds
)