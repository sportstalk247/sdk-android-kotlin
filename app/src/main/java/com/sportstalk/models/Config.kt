package com.sportstalk.models

import com.sportstalk.models.users.User

data class ClientConfig(
    val appId: String,
    val apiToken: String,
    val endpoint: String
)