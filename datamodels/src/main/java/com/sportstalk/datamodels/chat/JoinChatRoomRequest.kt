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
         * This limits the number of previous messages returned when joining the room.
         * - If null, API response defaults to 20 itemcount.
         */
        val limit: Int? = null
)