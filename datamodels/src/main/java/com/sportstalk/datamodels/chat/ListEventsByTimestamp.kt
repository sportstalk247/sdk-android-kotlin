package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListEventsByTimestamp(
        /** [Kind] */
        val kind: String? = null /* "list.chateventsbytimestamp" */,
        val cursorolder: String? = null,
        val cursornewer: String? = null,
        val timestampolder: Long? = null,
        val timestampnewer: Long? = null,
        val hasmoreolder: Boolean? = null,
        val hasmorenewer: Boolean? = null,
        val itemcount: Long? = null,
        val events: List<ChatEvent> = listOf()
)