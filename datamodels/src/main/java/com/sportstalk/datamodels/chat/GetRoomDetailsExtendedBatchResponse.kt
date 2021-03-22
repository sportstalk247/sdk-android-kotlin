package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class GetRoomDetailsExtendedBatchResponse(
        /** [Kind] */
        val kind: String? = null /* "chat.room.list.extendeddetails" */,
        val details: List<Detail> = listOf()
) {

    @Serializable
    data class Detail(
            val room: ChatRoom? = null,
            val mostrecentmessagetime: String? = null,  /* ISODateTime Format */
            val inroom: Long? = null
    )
}