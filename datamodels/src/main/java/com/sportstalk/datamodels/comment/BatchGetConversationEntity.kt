package com.sportstalk.datamodels.comment

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
enum class BatchGetConversationEntity(val rawValue: String) : Parcelable {
    @SerialName("reactions")
    Reactions("reactions"),

    @SerialName("likecount")
    LikeCount("likecount"),

    @SerialName("commentcount")
    CommentCount("commentcount"),
}