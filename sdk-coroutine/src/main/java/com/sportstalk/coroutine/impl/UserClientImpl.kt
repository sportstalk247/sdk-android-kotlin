package com.sportstalk.coroutine.impl

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.ServiceFactory
import com.sportstalk.coroutine.api.UserClient
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.users.*

class UserClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val config: ClientConfig
) : UserClient {

    private val userService = ServiceFactory.User.get(config)

    override suspend fun createOrUpdateUser(request: CreateUpdateUserRequest): User =
            userService.createOrUpdateUser(request)

    override suspend fun deleteUser(userId: String): DeleteUserResponse =
            userService.deleteUser(userId)

    override suspend fun getUserDetails(userId: String): User =
            userService.getUserDetails(userId)

    override suspend fun listUsers(limit: Int?, cursor: String?): ListUsersResponse =
            userService.listUsers(limit)

    override suspend fun setBanStatus(userId: String, banned: Boolean): User =
            userService.setBanStatus(userId, banned)

    override suspend fun searchUsers(handle: String?, name: String?, userid: String?, limit: Int?, cursor: String?): ListUsersResponse =
            userService.searchUsers(handle, name, userid, limit, cursor)

    override suspend fun setShadowBanStatus(userId: String, shadowban: Boolean, expireseconds: Long?): User =
            userService.setShadowBanStatus(userId, shadowban, expireseconds)

    override suspend fun globallyPurgeUserContent(userId: String, banned: Boolean): GloballyPurgeUserContentResponse =
            userService.globallyPurgeUserContent(userId, banned)

    override suspend fun reportUser(userId: String, reporttype: String): ReportUserResponse =
            userService.reportUser(userId, reporttype)

    override suspend fun listUserNotifications(userId: String, limit: Int, filterNotificationTypes: List<UserNotificationType>?, cursor: String?, includeread: Boolean?, filterChatRoomId: String?, filterChatRoomCustomId: String?): ListUserNotificationsResponse =
            userService.listUserNotifications(userId, limit, filterNotificationTypes, cursor, includeread, filterChatRoomId, filterChatRoomCustomId)

    override suspend fun setUserNotificationAsRead(userId: String, notificationId: String, read: Boolean): UserNotification =
            userService.setUserNotificationAsRead(userId, notificationId, read)

    override suspend fun setUserNotificationAsReadByChatEvent(userId: String, chatEventId: String, read: Boolean): UserNotification =
            userService.setUserNotificationAsReadByChatEvent(userId, chatEventId, read)

    override suspend fun deleteUserNotification(userId: String, notificationId: String): UserNotification =
            userService.deleteUserNotification(userId, notificationId)

    override suspend fun markAllUserNotificationsAsRead(userid: String, delete: Boolean) =
            userService.markAllUserNotificationsAsRead(userid, delete)
}