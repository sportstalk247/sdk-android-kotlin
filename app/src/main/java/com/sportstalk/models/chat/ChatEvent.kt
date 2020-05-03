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
        val eventtype: String? = null /* "speech"|"action"|"reaction" */,
        val userid: String? = null,
        val user: User? = null,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val replyto: ChatEvent? = null,
        val reactions: List<ChatEventReaction> = listOf(),
        val moderation: String? = null /* "na" */,
        val active: Boolean? = null,
        val reports: List<ChatEventReport> = listOf()
)

@Serializable
data class ChatEventReaction(
        val type: String? = null,
        val count: Long? = null,
        val users: List<User> = listOf()
)

@Serializable
data class ChatEventReport(
        val userid: String? = null,
        val reason: String? = null
)
