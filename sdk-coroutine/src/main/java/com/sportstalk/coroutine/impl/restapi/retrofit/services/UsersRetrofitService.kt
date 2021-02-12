package com.sportstalk.coroutine.impl.restapi.retrofit.services

import com.sportstalk.datamodels.users.*
import com.sportstalk.datamodels.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface UsersRetrofitService {

    @POST("{appId}/user/users/{userId}")
    suspend fun createOrUpdateUser(
            @Path("appId") appId: String,
            @Path("userId") userId: String,
            @Body request: CreateUpdateUserRequest
    ): Response<ApiResponse<User>>

    @DELETE("{appId}/user/users/{userId}")
    suspend fun deleteUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String
    ): Response<ApiResponse<DeleteUserResponse>>

    @GET("{appId}/user/users/{userId}")
    suspend fun getUserDetails(
            @Path("appId") appId: String,
            @Path("userId") userId: String
    ): Response<ApiResponse<User>>

    @GET("{appId}/user/users/")
    suspend fun listUsers(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Response<ApiResponse<ListUsersResponse>>

    @POST("{appId}/user/users/{userId}/ban")
    suspend fun setBanStatus(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: BanUserRequest
    ): Response<ApiResponse<User>>

    @POST("{appId}/user/search")
    suspend fun searchUsers(
            @Path("appId") appId: String,
            @Body request: SearchUsersRequest
    ): Response<ApiResponse<ListUsersResponse>>

    @POST("{appId}/user/users/{userId}/shadowban")
    suspend fun setShadowBanStatus(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: SetShadowBanStatusRequest
    ): Response<ApiResponse<User>>

    @POST("{appId}/user/users/{userId}/globalpurge")
    suspend fun globallyPurgeUserContent(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: GloballyPurgeUserContentRequest
    ): Response<ApiResponse<GloballyPurgeUserContentResponse>>

    @POST("{appId}/user/users/{userId}/report")
    suspend fun reportUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: ReportUserRequest
    ): Response<ApiResponse<ReportUserResponse>>

    @GET("{appId}/user/users/{userId}/notification/listnotifications")
    suspend fun listUserNotifications(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Query("limit") limit: Int,
            @Query("filterNotificationTypes") filterNotificationTypes: List<String>? = null,
            @Query("cursor") cursor: String? = null,
            @Query("includeread") includeread: Boolean? = null,
            @Query("filterChatRoomId") filterChatRoomId: String? = null,
            @Query("filterChatRoomCustomId") filterChatRoomCustomId: String? = null
    ): Response<ApiResponse<ListUserNotificationsResponse>>

    @PUT("{appId}/user/users/{userId}/notification/notifications/{notificationId}/update")
    suspend fun setUserNotificationAsRead(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Path(value = "notificationId", encoded = true) notificationId: String,
            @Query("read") read: Boolean
    ): Response<ApiResponse<UserNotification>>

    @PUT("{appId}/user/users/{userId}/notification/notificationsbyid/chateventid/{chatEventId}/update")
    suspend fun setUserNotificationAsReadByChatEvent(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Path(value = "chatEventId", encoded = true) chatEventId: String,
            @Query("read") read: Boolean
    ): Response<ApiResponse<UserNotification>>

    @DELETE("{appId}/user/users/{userId}/notification/notifications/{notificationId}")
    suspend fun deleteUserNotification(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Path(value = "notificationId", encoded = true) notificationId: String
    ): Response<ApiResponse<UserNotification>>

    @PUT("{appId}/user/users/{userId}/notification/notifications_all/markread")
    suspend fun markAllUserNotificationsAsRead(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userid: String,
            @Query("delete") delete: Boolean
    ): Response<ApiResponse<String>>

}