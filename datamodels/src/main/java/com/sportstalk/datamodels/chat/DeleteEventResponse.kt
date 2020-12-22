package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class DeleteEventResponse(
        /**
         *  TODO:: [Kind.DELETED_COMMENT]
         */
        val kind: String? = null,
        val permanentdelete: Boolean? = null,
        val event: ChatEvent? = null
)