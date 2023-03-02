package com.sportstalk.datamodels.chat

object ModerationType {
    // Input moderation
    const val pre = "pre"
    const val post = "post"
    // Resulting moderation from an action
    const val pending = "pending"
    const val approved = "approved"
    const val rejected = "rejected"
    const val na = "na"
}