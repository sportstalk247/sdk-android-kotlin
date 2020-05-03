package com.sportstalk.models.chat

import com.sportstalk.models.users.User
import kotlinx.serialization.Serializable

@Serializable
data class ChatEvent(
        val kind: String? = null /* "chat.event" */,
        val id: String? = null,
        val roomid: String? = null,
        val body: String? = null,
        val added: Long? = null,
        val ts: Long? = null,
        val eventtype: String? = null /* "speech"|"action" */,
        val userid: String? = null,
        val user: User? = null,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val replyto: ChatEvent? = null,
        val reactions: List<String> = listOf(),
        val moderation: String? = null /* "na" */,
        val active: Boolean? = null,
        val reports: List<String> = listOf()
)