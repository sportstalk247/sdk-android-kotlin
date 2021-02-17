package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class MuteUserInRoomRequest(
        val userid: String,
        val applyeffect: Boolean,
        val expireseconds: Long? = null     // in seconds
)