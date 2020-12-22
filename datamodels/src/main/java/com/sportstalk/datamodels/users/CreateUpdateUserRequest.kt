package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class CreateUpdateUserRequest(
        val userid: String,
        val handle: String? = null,
        val displayname: String? = null,
        val pictureurl: String? = null,
        val profileurl: String? = null
)