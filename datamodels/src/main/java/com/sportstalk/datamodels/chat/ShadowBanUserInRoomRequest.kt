package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ShadowBanUserInRoomRequest(
        val userid: String,
        val applyeffect: Boolean,
        val expireseconds: Long? = null     // in seconds
)