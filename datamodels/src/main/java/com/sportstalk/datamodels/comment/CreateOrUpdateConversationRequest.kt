package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrUpdateConversationRequest(
        val conversationid: String? = null,
        val property: String,
        val moderation: String, /* "pre"/"post" */
        val maxreports: Long? = null, // OPTIONAL, defaults to 3
        val enableprofanityfilter: Boolean? = null, // OPTIONAL, defaults to true
        val title: String? = null,
        val maxcommentlen: Long? = null,
        val open: Boolean? = null, // OPTIONAL, defaults to true
        val added: String? = null, // OPTIONAL, Example value: "2020-05-02T08:51:53.8140055Z"
        val whenmodified: String? = null, // OPTIONAL, Example value: "2020-05-02T08:51:53.8140055Z"
        val customtype: String? = null,
        val customid: String? = null,
        val customtags: List<String>? = null,
        val custompayload: String? = null,
        val customfield1: String? = null,
        val customfield2: String? = null
)