package com.sportstalk.datamodels.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SortType(val rawValue: String) {

    @SerialName("oldest")
    Oldest("oldest"),

    @SerialName("newest")
    Newest("newest"),

    @SerialName("likes")
    Likes("likes"),

    @SerialName("votescore")
    VoteScore("votescore"),

    @SerialName("mostreplies")
    MostReplies("mostreplies"),

}