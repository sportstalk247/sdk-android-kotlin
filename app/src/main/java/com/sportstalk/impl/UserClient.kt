package com.sportstalk.impl

import com.sportstalk.ServiceFactory
import com.sportstalk.api.UserService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.ListUsersResponse
import com.sportstalk.models.users.User
import java.util.concurrent.CompletableFuture

class UserClient(
        private val config: ClientConfig
) : UserService {

    private val userService = ServiceFactory.RestApi.User.get(config)

    override fun createOrUpdateUser(request: CreateUpdateUserRequest): CompletableFuture<ApiResponse<User>> =
            userService.createOrUpdateUser(request)

    override fun deleteUser(userId: String): CompletableFuture<ApiResponse<DeleteUserResponse>> =
            userService.deleteUser(userId)

    override fun getUserDetails(userId: String): CompletableFuture<ApiResponse<User>> =
            userService.getUserDetails(userId)

    override fun listUsers(limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListUsersResponse>> =
            userService.listUsers(limit)

    override fun setBanStatus(userId: String, banned: Boolean): CompletableFuture<ApiResponse<User>> =
            userService.setBanStatus(userId, banned)

    override fun searchUsers(handle: String?, name: String?, userid: String?, limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListUsersResponse>> =
            userService.searchUsers(handle, name, userid, limit, cursor)
}