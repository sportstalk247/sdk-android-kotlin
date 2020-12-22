package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class UpdateChatRoomRequest(
        val customid: String? = null,
        val userid: String? = null,
        val name: String? = null,
        val description: String? = null,
        val moderation: String? = null /* "pre"/"post" */,
        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val enableprofanityfilter: Boolean? = null,
        val delaymessageseconds: Long? = null,
        val roomisopen: Boolean? = null,
        val maxreports: Long? = null
)