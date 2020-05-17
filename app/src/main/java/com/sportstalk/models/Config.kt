package com.sportstalk.models

import com.sportstalk.models.users.User

open class ClientConfig(
    open val appId: String,
    open val apiToken: String,
    open val endpoint: String
)

data class SportsTalkConfig(
        override val appId: String,
        override val apiToken: String,
        override val endpoint: String,
        val user: User
): ClientConfig(appId, apiToken, endpoint)
