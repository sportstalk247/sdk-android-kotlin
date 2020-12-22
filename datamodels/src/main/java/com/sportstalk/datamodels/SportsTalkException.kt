package com.sportstalk.datamodels

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UseContextualSerialization
import java.lang.Exception

@Serializable
data class SportsTalkException(
        val kind: String? = null,
        override val message: String? = null,
        val code: Int? = null,
        val data: Map<String, String?>? = null,
        @Transient
        val err: Throwable? = null
): Exception(message, err)