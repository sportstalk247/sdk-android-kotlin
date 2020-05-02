package com.sportstalk.api.users

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.User
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

interface UsersApiService {

    /**
     * [POST] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2
     * - Invoke this API method if you want to create a user or update an existing user.
     */
    fun createUpdateUser(request: CreateUpdateUserRequest): CompletableFuture<ApiResponse<User>>

    /**
     * [DEL] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2
     * - Deletes the specified user
     */
    fun deleteUser(userId: String): CompletableFuture<ApiResponse<DeleteUserResponse>>

    /**
     * [GET] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#3323caa9-cc3d-4569-826c-69070ca51758
     * - This will return all the information about the user.
     */
    fun getUserDetails(userId: String): CompletableFuture<ApiResponse<User>>

}