package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class User(
        val kind: String? = null /* "app.user" */,
        val userid: String? = null,
        val handle: String? = null,
        val handlelowercase: String? = null,
        val displayname: String? = null,
        val pictureurl: String? = null,
        val profileurl: String? = null,
        val banned: Boolean? = null
)