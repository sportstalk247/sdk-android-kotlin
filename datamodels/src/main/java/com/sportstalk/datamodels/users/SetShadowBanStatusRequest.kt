package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class SetShadowBanStatusRequest(
        val shadowban: Boolean,
        val expireseconds: Long? = null     // in seconds
)