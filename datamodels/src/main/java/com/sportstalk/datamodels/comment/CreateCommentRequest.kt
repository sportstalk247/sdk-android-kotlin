package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
        val userid: String,
        val displayname: String? = null,
        /**
         * ex.
         * "Hello, World! This is a comment with international characters and emojis"
         * With mentions: "Hello @honestabe!"
         *
         */
        val body: String,
        val customtype: String? = null, // ex. "my-custom-type"
        val customfield1: String? = null, // ex. "/sample/userdefined1/emojis/"
        val customfield2: String? = null, // ex. "/sample/userdefined2/intl/characters/"
        val customtags: List<String>? = null, // ex. ["taga", "tagb"]
        val custompayload: String? = null, // ex. "{ num: 0 }"
)