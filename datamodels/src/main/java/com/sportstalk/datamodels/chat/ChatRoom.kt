package com.sportstalk.datamodels.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChatRoom(
        /** [Kind] */
        val kind: String? = null /* "chat.room" */,
        val id: String? = null,
        val appid: String? = null,
        val ownerid: String? = null,
        val name: String? = null,
        val description: String? = null,

        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val customtags: List<String>? = null,
        val customfield1: String? = null,
        val customfield2: String? = null,

        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val open: Boolean? = null,
        val inroom: Long? = null,
        val added: String? = null, /* ISODateTime Format */
        val whenmodified: String? = null, /* ISODateTime Format */
        /** [ModerationType] */
        val moderation: String? = null /* "pre"/"post" */,
        val maxreports: Long? = null,
        val enableautoexpiresessions: Boolean? = null,
        val enableprofanityfilter: Boolean? = null,
        val delaymessageseconds: Long? = null,
        val bouncedusers: List<String>? = null,       // List of user IDs
        val reportedusers: List<ReportedUser>? = null
): Parcelable

@Parcelize
@Serializable
data class ReportedUser(
        val userid: String? = null,
        val reportedbyuserid: String? = null,
        /** [ReportType] */
        val reason: String? = null,
        val added: String? = null /* ISODateTime Format */
): Parcelable