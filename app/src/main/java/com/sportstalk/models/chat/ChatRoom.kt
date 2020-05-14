package com.sportstalk.models.chat

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class ChatRoom(
        val kind: String? = null /* "chat.room" */,
        val id: String? = null,
        val appid: String? = null,
        val ownerid: String? = null,
        val name: String? = null,
        val description: String? = null,
        val iframeurl: String? = null,
        val slug: String? = null,
        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val open: Boolean? = null,
        val inroom: Long? = null,
        val added: Long? = null,
        val whenmodified: Long? = null,
        val moderation: String? = null /* "pre"/"post" */,
        val maxreports: Long? = null,
        val enableprofanityfilter: Boolean? = null,
        val delaymessageseconds: Long? = null
): Parcelable