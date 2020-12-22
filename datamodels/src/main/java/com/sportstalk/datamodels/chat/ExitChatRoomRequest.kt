package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExitChatRoomRequest(
        val userid: String
)