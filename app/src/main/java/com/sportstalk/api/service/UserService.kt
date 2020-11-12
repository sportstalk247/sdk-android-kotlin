package com.sportstalk.api.service

import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.ListUsersResponse
import com.sportstalk.models.users.User

interface UserService {

    /**
     * [POST] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2
     * - Invoke this API method if you want to create a user or update an existing user.
     */
    suspend fun createOrUpdateUser(request: CreateUpdateUserRequest): User

    /**
     * [DEL] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2
     * - Deletes the specified user
     */
    suspend fun deleteUser(userId: String): DeleteUserResponse

    /**
     * [GET] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#3323caa9-cc3d-4569-826c-69070ca51758
     * - This will return all the information about the user.
     */
    suspend fun getUserDetails(userId: String): User

    /**
     * [GET] /{{api_appid}}/user/users/?limit={{limit}}&cursor={{cursor}}
     * - https://apiref.sportstalk247.com/?version=latest#51718594-63ac-4c28-b249-8f47c3cb02b1
     * - Gets a list of users
     */
    suspend fun listUsers(
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): ListUsersResponse

    /**
     * [POST] /{{api_appid}}/user/users/{{userId}}/ban
     * - https://apiref.sportstalk247.com/?version=latest#211d5614-b251-4815-bf76-d8f6f66f97ab
     * - Will toggle the user's banned flag
     */
    suspend fun setBanStatus(userId: String, banned: Boolean): User

    /**
     * [POST] /{{api_appid}}/user/search
     * - https://apiref.sportstalk247.com/?version=latest#dea07871-86bb-4c12-bef3-d7290d762a06
     * - Searches the users in an app
     */
    suspend fun searchUsers(
            handle: String? = null,
            name: String? = null,
            userid: String? = null,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): ListUsersResponse

    /**
     * [POST] /{{api_appid}}/user/users/{userId}/shadowban
     * - https://apiref.sportstalk247.com/?version=latest#211a5696-59ce-4988-82c9-7c614cab3efb
     * - Will toggle the user's shadow banned flag
     */
    suspend fun shadowBanUser(
            userId: String,
            shadowban: Boolean,
            expireseconds: Long? = null
    ): User

}