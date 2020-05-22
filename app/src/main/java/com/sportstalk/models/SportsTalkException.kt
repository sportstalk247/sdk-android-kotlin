package com.sportstalk.models

import kotlinx.serialization.Serializable

@Serializable
data class SportsTalkException(
        val kind: String? = null,
        override val message: String? = null,
        val code: Int? = null,
        val data: Map<String, String?>? = null
): Throwable(message)