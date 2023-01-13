package com.sportstalk.datamodels.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class CommentFilterModerationState(val rawValue: String) {

    @SerialName("approved")
    Approved("approved"),

    @SerialName("rejected")
    Rejected("rejected"),

    @SerialName("pending")
    Pending("pending"),

    @SerialName("flagged")
    Flagged("flagged"),

}