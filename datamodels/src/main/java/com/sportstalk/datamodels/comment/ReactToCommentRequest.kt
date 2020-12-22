package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ReactToCommentRequest(
        val userid: String,
        val reaction: String, // [CommentReaction]
        val reacted: Boolean
)

object CommentReaction {
    const val LIKE = "like"
}