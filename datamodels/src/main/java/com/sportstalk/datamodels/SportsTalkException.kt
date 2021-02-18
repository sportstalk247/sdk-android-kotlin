package com.sportstalk.datamodels

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.lang.Exception

@Serializable
data class SportsTalkException(
        val kind: String? = null,
        override val message: String? = null,
        val code: Int? = null,
        val data: JsonObject/*Map<String, String?>*/? = null,
        @Transient
        val err: Throwable? = null
): Exception(message, err)