package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ReportUserInRoomRequest(
        val userid: String,
        /* [ReportType] */
        val reporttype: String
)