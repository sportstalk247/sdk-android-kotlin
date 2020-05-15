package com.sportstalk.models.chat

import kotlinx.serialization.Serializable

@Serializable
data class JoinChatRoomRequest(
        @Transient
        val roomid: String? = null,
        val userid: String,
        val handle: String? = null,
        val displayname: String? = null,
        val pictureurl: String? = null,
        val profileurl: String? = null
)