package com.sportstalk.datamodels.comment

import com.sportstalk.datamodels.users.User
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
        val kind: String? = null, // "comment.comment"
        val id: String? = null,
        val appid: String? = null,
        val conversationid: String? = null,
        val commenttype: String? = null, // [CommentType]
        val added: String? = null, // OPTIONAL, Example value: "2020-05-02T08:51:53.8140055Z"
        val whenmodified: String? = null, // OPTIONAL, Example value: "2020-05-02T08:51:53.8140055Z"
        val userid: String? = null,
        val user: User? = null,
        val body: String? = null,
        /*val hashtags: List<String> = listOf(),*/
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        val customtags: List<String> = listOf(),
        val customfield1: String? = null,
        val customfield2: String? = null,
        val edited: Boolean? = null,
        val deleted: Boolean? = null,
        val parentid: String? = null,
        /*val hierarchy: List<String> = listOf(),*/
        /*val mentions: List<String> = listOf(),*/
        /*val reactions: List<String> = listOf(),*/
        val likecount: Long? = null,
        val replycount: Long? = null,
        val votecount: Long? = null,
        val votescore: Long? = null,
        /*val votes: List<Any> = listOf(),*/
        val moderation: String? = null,
        val active: Boolean? = null/*,
        val reports: List<Any> = listOf()*/
)

object CommentType {
    const val COMMENT = "comment"
}