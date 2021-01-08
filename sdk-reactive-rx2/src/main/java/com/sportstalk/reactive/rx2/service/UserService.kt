package com.sportstalk.reactive.rx2.service

import com.sportstalk.datamodels.users.*
import io.reactivex.Single

interface UserService {

    /**
     * [POST] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2
     * - Invoke this API method if you want to create a user or update an existing user.
     */
    fun createOrUpdateUser(request: CreateUpdateUserRequest): Single<User>

    /**
     * [DEL] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2
     * - Deletes the specified user
     */
    fun deleteUser(userId: String): Single<DeleteUserResponse>

    /**
     * [GET] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#3323caa9-cc3d-4569-826c-69070ca51758
     * - This will return all the information about the user.
     */
    fun getUserDetails(userId: String): Single<User>

    /**
     * [GET] /{{api_appid}}/user/users/?limit={{limit}}&cursor={{cursor}}
     * - https://apiref.sportstalk247.com/?version=latest#51718594-63ac-4c28-b249-8f47c3cb02b1
     * - Gets a list of users
     */
    fun listUsers(
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): Single<ListUsersResponse>

    /**
     * [POST] /{{api_appid}}/user/users/{{userId}}/ban
     * - https://apiref.sportstalk247.com/?version=latest#211d5614-b251-4815-bf76-d8f6f66f97ab
     * - Will toggle the user's banned flag
     */
    fun setBanStatus(userId: String, banned: Boolean): Single<User>

    /**
     * [POST] /{{api_appid}}/user/search
     * - https://apiref.sportstalk247.com/?version=latest#dea07871-86bb-4c12-bef3-d7290d762a06
     * - Searches the users in an app
     */
    fun searchUsers(
            handle: String? = null,
            name: String? = null,
            userid: String? = null,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): Single<ListUsersResponse>

    /**
     * [POST] /{{api_appid}}/user/users/{userId}/shadowban
     * - https://apiref.sportstalk247.com/?version=latest#211a5696-59ce-4988-82c9-7c614cab3efb
     * - Will toggle the user's shadow banned flag
     */
    fun setShadowBanStatus(
            userId: String,
            shadowban: Boolean,
            expireseconds: Long? = null
    ): Single<User>

    /**
     * [POST] /{{api_appid}}/user/users/{userId}/globalpurge
     * - https://apiref.sportstalk247.com/?version=latest#c36d94e2-4fd9-4c9f-8009-f1d8ae9da6f5
     * - Will purge all chat content published by the specified user
     */
    fun globallyPurgeUserContent(
            userId: String,
            banned: Boolean
    ): Single<GloballyPurgeUserContentResponse>

    /**
     * [POST] /{{api_appid}}/user/users/{userId}/report
     * - https://apiref.sportstalk247.com/?version=latest#5bfd5d93-dbfb-445c-84ff-c69f184e4277
     * - REPORTS a USER to the moderation team.
     */
    fun reportUser(
            userId: String,
            /** [ReportType] */
            reporttype: String,
    ): Single<ReportUserResponse>

    /**
     * [GET] /{{api_appid}}/user/users/{userId}/notification/listnotifications?filterNotificationTypes=&limit=&includeread=
     * - https://apiref.sportstalk247.com/?version=latest#f09d36c2-de40-4866-8818-74527b2a6df5
     * - Returns a list of user notifications
     */
    fun listUserNotifications(
            userId: String,
            filterNotificationTypes: List<UserNotification.Type>? = null,
            limit: Int,
            includeread: Boolean
    ): Single<ListUserNotificationsResponse>

    /**
     * [GET] /{{api_appid}}/user/users/{userId}/notification/notifications/{notificationId}/update?read=
     * - https://apiref.sportstalk247.com/?version=latest#e0c669ff-4722-46b0-ab3e-d1d74d9d340a
     * - This marks a notification as being in READ status.
     */
    fun setUserNotificationAsRead(
            userId: String,
            notificationId: String,
            read: Boolean
    ): Single<UserNotification>

}