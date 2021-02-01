package com.sportstalk.datamodels.users

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class User(
        val kind: String? = null /* "app.user" */,
        val userid: String? = null,
        val handle: String? = null,
        val handlelowercase: String? = null,
        val displayname: String? = null,
        val pictureurl: String? = null,
        val profileurl: String? = null,
        /**
         * [Role]
         */
        val role: String? = null,
        val customtags: List<String> = listOf(),
        val banned: Boolean? = null,
        val shadowbanned: Boolean? = null,
        val shadowbanexpires: String? = null,   // ISODate ex. "2020-11-11T11:35:07.657812Z"
        val moderation: String? = null,   // "unknown"
        val reports: List<UserReport> = listOf()
): Parcelable {
        object Role {
                const val USER = "user"
                const val MODERATOR = "moderator"
                const val ADMIN = "admin"
        }
}

@Parcelize
@Serializable
data class UserReport(
        val userid: String? = null,
        /** [ReportType] */
        val reason: String? = null
): Parcelable