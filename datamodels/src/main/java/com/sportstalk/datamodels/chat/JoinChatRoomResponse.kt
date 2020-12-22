package com.sportstalk.datamodels.chat

import com.sportstalk.datamodels.users.User
import kotlinx.serialization.Serializable

@Serializable
data class JoinChatRoomResponse(
        /** [Kind] */
        val kind: String? = null /* "chat.joinroom" */,
        val user: User? = null,
        val room: ChatRoom? = null,
        val eventscursor: GetUpdatesResponse? = null,
        /**
         * The cursor that will be used to fetch paginated previous list of chat events
         */
        val previouseventscursor: String? = null
)