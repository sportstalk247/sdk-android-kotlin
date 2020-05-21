package com.sportstalk.models.comment

import kotlinx.serialization.Serializable

@Serializable
data class PublishCommentRequest(
        val userid: String,
        val body: String,
        val added: String? = null // OPTIONAL, Example value: "2020-05-02T08:51:53.8140055Z"
)