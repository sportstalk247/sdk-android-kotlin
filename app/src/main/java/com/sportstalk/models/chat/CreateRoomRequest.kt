package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomRequest(
        val name: String,
        val slug: String? = null,
        val description: String? = null,
        val moderation: String? = null /* "pre"/"post" */,
        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val enableprofanityfilter: Boolean? = null,
        val delaymessageseconds: Long? = null,
        val roomisopen: Boolean? = null,
        val maxreports: Long? = null
)
