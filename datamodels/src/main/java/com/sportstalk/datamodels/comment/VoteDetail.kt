package com.sportstalk.datamodels.comment

import android.os.Parcelable
import com.sportstalk.datamodels.users.User
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class VoteDetail(
    val type: VoteType? = null,
    val count: Long? = null,
    val users: List<User>? = null,
): Parcelable
