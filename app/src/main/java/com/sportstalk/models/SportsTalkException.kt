package com.sportstalk.models

import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import java.util.concurrent.CompletionException

@Serializable
data class SportsTalkException(
        val kind: String? = null,
        override val message: String? = null,
        val code: Int? = null,
        val data: Map<String, String?>? = null,
        @ContextualSerialization(Throwable::class)
        @Transient
        val err: Throwable? = null
): CompletionException(message, err)