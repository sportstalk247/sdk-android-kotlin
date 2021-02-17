package com.sportstalk.reactive.rx2.impl

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.users.*
import com.sportstalk.reactive.rx2.ServiceFactory
import com.sportstalk.reactive.rx2.api.UserClient
import io.reactivex.Completable
import io.reactivex.Single

class UserClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val config: ClientConfig
): UserClient {

    private val userService = ServiceFactory.User.get(config)

    override fun createOrUpdateUser(request: CreateUpdateUserRequest): Single<User> =
            userService.createOrUpdateUser(request)

    override fun deleteUser(userId: String): Single<DeleteUserResponse> =
            userService.deleteUser(userId)

    override fun getUserDetails(userId: String): Single<User> =
            userService.getUserDetails(userId)

    override fun listUsers(limit: Int?, cursor: String?): Single<ListUsersResponse> =
            userService.listUsers(limit)

    override fun setBanStatus(userId: String, applyeffect: Boolean, expireseconds: Long?): Single<User> =
            userService.setBanStatus(userId, applyeffect, expireseconds)

    override fun searchUsers(handle: String?, name: String?, userid: String?, limit: Int?, cursor: String?): Single<ListUsersResponse> =
            userService.searchUsers(handle, name, userid, limit, cursor)

    override fun setShadowBanStatus(userId: String, shadowban: Boolean, expireseconds: Long?): Single<User> =
            userService.setShadowBanStatus(userId, shadowban, expireseconds)

    override fun globallyPurgeUserContent(userId: String, banned: Boolean): Single<GloballyPurgeUserContentResponse> =
            userService.globallyPurgeUserContent(userId, banned)

    override fun reportUser(userId: String, reporttype: String): Single<ReportUserResponse> =
            userService.reportUser(userId, reporttype)

    override fun listUserNotifications(userId: String, limit: Int, filterNotificationTypes: List<UserNotificationType>?, cursor: String?, includeread: Boolean?, filterChatRoomId: String?, filterChatRoomCustomId: String?): Single<ListUserNotificationsResponse> =
            userService.listUserNotifications(userId, limit, filterNotificationTypes, cursor, includeread, filterChatRoomId, filterChatRoomCustomId)

    override fun setUserNotificationAsRead(userId: String, notificationId: String, read: Boolean): Single<UserNotification> =
            userService.setUserNotificationAsRead(userId, notificationId, read)

    override fun setUserNotificationAsReadByChatEvent(userId: String, chatEventId: String, read: Boolean): Single<UserNotification> =
            userService.setUserNotificationAsReadByChatEvent(userId, chatEventId, read)

    override fun deleteUserNotification(userId: String, notificationId: String): Single<UserNotification> =
            userService.deleteUserNotification(userId, notificationId)

    override fun deleteUserNotificationByChatEvent(userId: String, chatEventId: String): Single<UserNotification> =
            userService.deleteUserNotificationByChatEvent(userId, chatEventId)

    override fun markAllUserNotificationsAsRead(userid: String, delete: Boolean): Completable =
            userService.markAllUserNotificationsAsRead(userid, delete)
}