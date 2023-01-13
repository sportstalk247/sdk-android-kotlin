package com.sportstalk.datamodels.comment

import kotlinx.serialization.Serializable
import com.sportstalk.datamodels.reports.ReportType

@Serializable
data class ReportCommentRequest(
    val userid: String,
    /** [ReportType] */
    val reporttype: String,
) {

}