package com.sportstalk.datamodels.chat

import android.os.Parcelable
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.reactions.Reaction
import com.sportstalk.datamodels.reports.Report
import com.sportstalk.datamodels.users.User
import kotlinx.parcelize.Parcelize
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
    val modified: String? = null,
    val ts: Long? = null,
    /** [EventType] */
    val eventtype: String? = null,
    val userid: String? = null,
    val user: User? = null,
    val customtype: String? = null,
    val customid: String? = null,
    val custompayload: String? = null,
    val customtags: List<String>? = null,
    val customfield1: String? = null,
    val customfield2: String? = null,
    val replyto: ChatEvent? = null,
    val parentid: String? = null,
    val hierarchy: List<String>? = null,
    val depth: Int? = null,
    val edited: Boolean? = null,
    /*
    * If a moderator changes the contents of the event, this is set to true.
    */
    val editedbymoderator: Boolean? = null,
    val deleted: Boolean? = null,
    val active: Boolean? = null,
    /*val mutedby: List<String>? = null,*/
    val shadowban: Boolean? = null,
    val hashtags: List<String>? = null,
        /*
        * If internal censor modifies the body this flag is set to true.
        */
        val censored: Boolean? = null,
    val likecount: Long? = null,
    val replycount: Long? = null,
    val reactions: List<Reaction> = listOf(),
    /** [ModerationType] */
        val moderation: String? = null /* "na" */,
    val reports: List<Report> = listOf()
) : Parcelable {
    /**
     * Time bound cursor that you can use with listPreviousEvents or getUpdates
     */
    fun getEventCursor(): String =
            ts?.toString(10) ?: ""
}

object EventType {
    const val SPEECH = "speech"
    const val PURGE = "purge"
    const val REACTION = "reaction"
    const val REPLACE = "replace"
    const val REMOVE = "remove"
    const val ROOM_CLOSED = "roomclosed"
    const val ROOM_OPENED = "roomopened"
    const val ACTION = "action"
    const val REPLY = "reply"
    const val QUOTE = "quote"
    const val GOAL = "goal" // custom type
    const val ADVERTISEMENT = "advertisement" // custom type
    const val CUSTOM = "custom" // This indicates you will be using a custom event type.
    const val AD = "ad" // Use this event type to push an advertisement. Use the CustomPayload property to specify parameters for your add.
    const val ANNOUNCEMENT = "announcement" // This indicates the event is of type announcement.
    const val BOUNCE = "bounce" // This indicates the a user has been bounced out from the room.

    const val MOD_FLAGGED = "modflagged"
    const val MOD_APPROVED = "modapproved"
    const val MOD_REJECTED = "modrejected"
}

