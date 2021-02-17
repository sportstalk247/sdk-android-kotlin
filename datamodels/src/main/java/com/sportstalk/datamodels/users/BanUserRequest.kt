package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class BanUserRequest(
        val applyeffect: Boolean,
        val expireseconds: Long? = null
)