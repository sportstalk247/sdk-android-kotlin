package com.sportstalk.datamodels.reactions

import android.os.Parcelable
import com.sportstalk.datamodels.users.User
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Reaction(
    val type: String? = null,
    val count: Long? = null,
    val users: List<User> = listOf()
): Parcelable

object ReactionType {
    const val LIKE = "like"
}
