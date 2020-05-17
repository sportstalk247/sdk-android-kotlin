package com.sportstalk.models.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
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

        val iframeurl: String? = null,

        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val customtags: List<String> = listOf(),
        val customfield1: String? = null,
        val customfield2: String? = null,

        val slug: String? = null,

        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val open: Boolean? = null,
        val inroom: Long? = null,
        val added: Long? = null,
        val whenmodified: Long? = null,
        /** [ModerationType] */
        val moderation: String? = null /* "pre"/"post" */,
        val maxreports: Long? = null,
        val enableprofanityfilter: Boolean? = null,
        val delaymessageseconds: Long? = null
): Parcelable