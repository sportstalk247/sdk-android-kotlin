package com.sportstalk.datamodels.users

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class UserNotification(
        val id: String? = null,
        /**
         * [Kind.NOTIFICATION] "notification"
         */
        val kind: String? = null,
        val chatroomid: String? = null,
        val added: String? = null, /* ISODateTime Format */
        val userid: String? = null,
        val ts: Long? = null,   /* Epoch time */
        val whenread: String? = null, /* ISODateTime Format */
        val isread: Boolean? = null,
        /**
         * [NotificationType]
         */
        val notificationtype: Type? = null,
        val chatroomcustomid: String? = null,
        val commentconversationid: String? = null,
        val commentconversationcustomid: String? = null,
        val chateventid: String? = null,
        val commentid: String? = null
): Parcelable {
    @Serializable
    enum class Type(val serialName: String) {
        @SerialName("chatreply") CHAT_REPLY("chatreply"),
        @SerialName("chatquote") CHAT_QUOTE("chatquote")
    }
}