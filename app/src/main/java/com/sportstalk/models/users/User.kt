package com.sportstalk.models.users

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
        val banned: Boolean? = null
): Parcelable