package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ListUserSubscribedRoomsResponse(
    /** [Kind] */
    val kind: String? = null /* "chat.list.userroomsubscriptions" */,
    val cursor: String? = null,
    val more: Boolean? = null,
    val itemcount: Long? = null,
    val subscriptions: List<ChatSubscription> = emptyList()
)
