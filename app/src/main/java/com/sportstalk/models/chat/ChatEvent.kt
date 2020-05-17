package com.sportstalk.models.chat

import android.os.Parcelable
import com.sportstalk.models.Kind
import com.sportstalk.models.users.User
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChatEvent(
        /** [Kind] */
        val kind: String? = null /* "chat.event" */,
        val id: String? = null,
        val roomid: String? = null,
        val body: String? = null,
        val added: Long? = null,
        val ts: Long? = null,
        /** [EventType] */
        val eventtype: String? = null,
        val userid: String? = null,
        val user: User? = null,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val replyto: ChatEvent? = null,
        val reactions: List<ChatEventReaction> = listOf(),
        /** [ModerationType] */
        val moderation: String? = null /* "na" */,
        val active: Boolean? = null,
        val reports: List<ChatEventReport> = listOf()
) : Parcelable

@Parcelize
@Serializable
data class ChatEventReaction(
        /** [Reaction] */
        val type: String? = null,
        val count: Long? = null,
        val users: List<User> = listOf()
) : Parcelable

@Parcelize
@Serializable
data class ChatEventReport(
        val userid: String? = null,
        /** [ReportType] */
        val reason: String? = null
) : Parcelable

object EventType {
    const val SPEECH = "speech"
    const val PURGE = "purge"
    const val REACTION = "reaction"
    const val ROOM_CLOSED = "roomClosed"
    const val ROOM_OPEN = "roomopen"
    const val ACTION = "action"
    const val REPLY = "reply"
    const val GOAL = "goal" // custom type
    const val ADVERTISEMENT = "advertisement" // custom type
}

object Reaction {
    const val LIKE = "like"
}