package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class UserList(
        val kind: String? = null,
        val cursor: String? = null,
        val users: List<User> = listOf()
)