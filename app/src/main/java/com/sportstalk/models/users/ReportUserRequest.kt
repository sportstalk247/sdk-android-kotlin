package com.sportstalk.models.users

import kotlinx.serialization.Serializable

@Serializable
data class ReportUserRequest(
        val userid: String? = null,
        /** [ReportType] */
        val reporttype: String? = null, // ex. "abuse", "spam"
)