package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ChatSubscription(
    val kind: String? = null /* "chat.subscription" */,
    val id: String? = null,
    val roomid: String? = null,
    val roomcustomid: String? = null,
    val userid: String? = null,
    val updated: String? = null,    // ISODateTime Format
    val added: String? = null,    // ISODateTime Format
    val roomname: String? = null,
    val roomcustomtags: List<String>? = null,
    val persist: Boolean? = null
)
