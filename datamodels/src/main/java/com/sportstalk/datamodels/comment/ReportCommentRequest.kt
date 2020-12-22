package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable

@Serializable
data class ReportCommentRequest(
        val userid: String,
        val reporttype: String // [CommentReportType]
)

object CommentReportType {
    const val ABUSE = "abuse"
}