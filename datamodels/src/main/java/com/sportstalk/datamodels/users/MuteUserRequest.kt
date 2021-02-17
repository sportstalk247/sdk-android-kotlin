package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class MuteUserRequest(
        val applyeffect: Boolean,
        val expireseconds: Long? = null
)