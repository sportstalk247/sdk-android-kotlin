package com.sportstalk.reactive.rx2.impl

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.users.*
import com.sportstalk.reactive.rx2.ServiceFactory
import com.sportstalk.reactive.rx2.api.UserClient
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

    override fun setBanStatus(userId: String, banned: Boolean): Single<User> =
            userService.setBanStatus(userId, banned)

    override fun searchUsers(handle: String?, name: String?, userid: String?, limit: Int?, cursor: String?): Single<ListUsersResponse> =
            userService.searchUsers(handle, name, userid, limit, cursor)

    override fun setShadowBanStatus(userId: String, shadowban: Boolean, expireseconds: Long?): Single<User> =
            userService.setShadowBanStatus(userId, shadowban, expireseconds)

    override fun globallyPurgeUserContent(userId: String, banned: Boolean): Single<GloballyPurgeUserContentResponse> =
            userService.globallyPurgeUserContent(userId, banned)

    override fun reportUser(userId: String, reporttype: String): Single<ReportUserResponse> =
            userService.reportUser(userId, reporttype)

    override fun listUserNotifications(userId: String, filterNotificationTypes: List<UserNotificationType>?, limit: Int, includeread: Boolean): Single<ListUserNotificationsResponse> =
            userService.listUserNotifications(userId, filterNotificationTypes, limit, includeread)

    override fun setUserNotificationAsRead(userId: String, notificationId: String, read: Boolean): Single<UserNotification> =
            userService.setUserNotificationAsRead(userId, notificationId, read)
}