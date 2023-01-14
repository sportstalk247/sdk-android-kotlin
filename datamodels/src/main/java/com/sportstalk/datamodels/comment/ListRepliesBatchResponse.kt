package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ListRepliesBatchResponse(
    val kind: String? = null, // "list.repliesbyparentid"
    val repliesgroupedbyparentid: List<CommentReplyGroup> = listOf(),
) {

    @Serializable
    data class CommentReplyGroup(
        val kind: String? = null, // "list.commentreplygroup"
        val parentid: String? = null,
        val comments: List<Comment> = listOf(),
    )

}
