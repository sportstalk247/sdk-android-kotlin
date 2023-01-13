package com.sportstalk.datamodels.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class VoteType(val rawValue: String) {

    @SerialName("up")
    Up("up"),

    @SerialName("down")
    Down("down"),

    @SerialName("none")
    None("none"),

}