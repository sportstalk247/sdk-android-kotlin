package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class ListUsersResponse(
        /** [Kind] */
        val kind: String? = null /* "list.users" */,
        val cursor: String? = null /* "{userId}|" */,
        val users: List<User> = listOf()
)