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
        /*
        * If event is modified, the original body value will be saved here. Empty if unmodified.
        */
        val originalbody: String? = null,
        val added: String? = null, /* ISODateTime Format */
        /*
        * Updates when event changes.
        * - ISODateTime Format
        */
        val whenmodified: String? = null,
        val ts: Long? = null,
        /** [EventType] */
        val eventtype: String? = null,
        val userid: String? = null,
        val user: User? = null,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val customtags: List<String> = listOf(),
        val customfield1: String? = null,
        val customfield2: String? = null,
        val replyto: ChatEvent? = null,
        val parentid: String? = null,
        val hierarchy: List<String> = listOf(),
        val depth: Int? = null,
        val edited: Boolean? = null,
        /*
        * If a moderator changes the contents of the event, this is set to true.
        */
        val editedbymoderator: Boolean? = null,
        val deleted: Boolean? = null,
        val active: Boolean? = null,
        /*val mutedby: List<String> = listOf(),*/
        val shadowban: Boolean? = null,
        /*val hashtags: List<String> = listOf(),*/
        /*
        * If internal censor modifies the body this flag is set to true.
        */
        val censored: Boolean? = null,
        val likecount: Long? = null,
        val replycount: Long? = null,
        val reactions: List<ChatEventReaction> = listOf(),
        /** [ModerationType] */
        val moderation: String? = null /* "na" */,
        val reports: List<ChatEventReport> = listOf()
) : Parcelable {
    /**
     * Time bound cursor that you can use with listPreviousEvents or getUpdates
     */
    fun getEventCursor(): String =
            ts?.toString(10) ?: ""
}

@Parcelize
@Serializable
data class ChatEventReaction(
        /** [EventReaction] */
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
    const val ROOM_CLOSED = "roomclosed"
    const val ROOM_OPEN = "roomopened"
    const val ACTION = "action"
    const val REPLY = "reply"
    const val QUOTE = "quote"
    const val GOAL = "goal" // custom type
    const val ADVERTISEMENT = "advertisement" // custom type
    const val CUSTOM = "custom" // This indicates you will be using a custom event type.
    const val AD = "ad" // Use this event type to push an advertisement. Use the CustomPayload property to specify parameters for your add.
    const val ANNOUNCEMENT = "announcement" // This indicates the event is of type announcement.
}

object EventReaction {
    const val LIKE = "like"
}