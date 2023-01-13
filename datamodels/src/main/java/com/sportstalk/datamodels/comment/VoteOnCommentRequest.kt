package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class VoteOnCommentRequest(
    val vote: VoteType,
    val userid: String,
)
