package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRoomRequest(
        val customid: String? = null,
        val userid: String? = null,
        val name: String,
        val description: String? = null,
        val moderation: String? = null /* "pre"/"post" */,
        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val enableprofanityfilter: Boolean? = null,
        val delaymessageseconds: Long? = null,
        val roomisopen: Boolean? = null,
        val maxreports: Long? = null
)
