package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class DeleteUserResponse(
        /** [Kind] */
        val kind: String? = null /* "deleted.appuser" */,
        val user: User? = null
)