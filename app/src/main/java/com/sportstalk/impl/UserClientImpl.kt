package com.sportstalk.impl

import androidx.annotation.RestrictTo
import com.sportstalk.ServiceFactory
import com.sportstalk.api.UserClient
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.users.*

class UserClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val config: ClientConfig
) : UserClient {

    private val userService = ServiceFactory.RestApi.User.get(config)

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

    override suspend fun shadowBanUser(userId: String, shadowban: Boolean, expireseconds: Long?): User =
            userService.shadowBanUser(userId, shadowban, expireseconds)

    override suspend fun globalPurge(userId: String, banned: Boolean): GlobalPurgeResponse =
            userService.globalPurge(userId, banned)

    override suspend fun reportUser(userId: String, reporttype: String): ReportUserResponse =
            userService.reportUser(userId, reporttype)
}