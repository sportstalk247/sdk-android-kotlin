package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class JoinChatRoomRequest(
        val userid: String,
        val handle: String? = null,
        val displayname: String? = null,
        val pictureurl: String? = null,
        val profileurl: String? = null,
        /**
         * Defaults to 50. This limits the number of previous messages returned when joining the room.
         */
        val limit: Int = 50
)