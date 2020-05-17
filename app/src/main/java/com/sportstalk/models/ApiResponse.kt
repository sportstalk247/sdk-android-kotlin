package com.sportstalk.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
        /** [Kind] */
        val kind: String? = null,
        val message: String? = null,
        val code: Int? = null,
        /*val errors: Map<String, Any> = mapOf(),*/
        val data: T? = null
)