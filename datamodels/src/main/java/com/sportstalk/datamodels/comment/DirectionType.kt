package com.sportstalk.datamodels.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DirectionType(val rawValue: String) {

    @SerialName("forward")
    Forward("forward"),

    @SerialName("backward")
    Backward("backward"),

}