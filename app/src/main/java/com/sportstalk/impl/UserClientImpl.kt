package com.sportstalk.impl

import androidx.annotation.RestrictTo
import com.sportstalk.ServiceFactory
import com.sportstalk.api.UserClient
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.ListUsersResponse
import com.sportstalk.models.users.User
import java.util.concurrent.CompletableFuture

class UserClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val config: ClientConfig
) : UserClient {

    private val userService = ServiceFactory.RestApi.User.get(config)

    override fun createOrUpdateUser(request: CreateUpdateUserRequest): CompletableFuture<User> =
            userService.createOrUpdateUser(request)

    override fun deleteUser(userId: String): CompletableFuture<DeleteUserResponse> =
            userService.deleteUser(userId)

    override fun getUserDetails(userId: String): CompletableFuture<User> =
            userService.getUserDetails(userId)

    override fun listUsers(limit: Int?, cursor: String?): CompletableFuture<ListUsersResponse> =
            userService.listUsers(limit)

    override fun setBanStatus(userId: String, banned: Boolean): CompletableFuture<User> =
            userService.setBanStatus(userId, banned)

    override fun searchUsers(handle: String?, name: String?, userid: String?, limit: Int?, cursor: String?): CompletableFuture<ListUsersResponse> =
            userService.searchUsers(handle, name, userid, limit, cursor)
}