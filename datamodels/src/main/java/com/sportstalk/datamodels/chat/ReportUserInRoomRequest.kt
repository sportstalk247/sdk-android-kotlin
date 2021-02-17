package com.sportstalk.datamodels.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportUserInRoomRequest(
        /** the userid of the person doing the report */
        @SerialName("userid")
        val reporterid: String,
        /* [ReportType] */
        val reporttype: String
)