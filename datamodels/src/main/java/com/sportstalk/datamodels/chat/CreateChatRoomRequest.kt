package com.sportstalk.datamodels.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateChatRoomRequest(
        val customid: String? = null,
        val userid: String? = null,
        val name: String? = null,
        val description: String? = null,
        val moderation: String? = null /* "pre"/"post" */,
        val enableactions: Boolean? = null,
        val enableenterandexit: Boolean? = null,
        val enableprofanityfilter: Boolean? = null,
        val enableautoexpiresessions: Boolean? = null,
        val delaymessageseconds: Long? = null,
        val roomisopen: Boolean? = null,
        val maxreports: Long? = null,
        val customtype: String? = null, // (optional) Custom type string
        val custompayload: String? = null,  // (optional) Custom payload string.
        val customfield1: String? = null,   // (optional) User custom field 1. Store any string value you want here, limit 1024 bytes.
        val customfield2: String? = null, // (optional) User custom field 2. Store any string value you want here, limit 1024 bytes.
        val customtags: List<String>? = null,   // (optional) A comma delimited list of tags
        @SerialName("private")
        val `private`: Boolean? = null  // (false | optional) If set to true a user cannot join this room unless explicitly granted with a call to the Set User Permission API.
)
