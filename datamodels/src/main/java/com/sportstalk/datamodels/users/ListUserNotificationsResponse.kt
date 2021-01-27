package com.sportstalk.datamodels.users

import kotlinx.serialization.Serializable

@Serializable
data class ListUserNotificationsResponse(
        /** [Kind.LIST_USER_NOTIFICATIONS] "list.usernotifications" */
        val kind: String? = null,
        val cursor: String? = null,
        val more: Boolean? = null,
        val itemcount: Int? = null,
        val notifications: List<UserNotification> = listOf()
)