package com.sportstalk.datamodels.reports

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Report(
    val userid: String? = null,
    /** [ReportType] */
    val reason: String? = null
): Parcelable


object ReportType {
    const val ABUSE = "abuse"
    const val SPAM = "spam"
}