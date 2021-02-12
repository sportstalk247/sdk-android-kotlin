package com.sportstalk.reactive.rx2.impl.restapi.retrofit.services

import com.sportstalk.datamodels.users.*
import com.sportstalk.datamodels.ApiResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface UsersRetrofitService {

    @POST("{appId}/user/users/{userId}")
    fun createOrUpdateUser(
            @Path("appId") appId: String,
            @Path("userId") userId: String,
            @Body request: CreateUpdateUserRequest
    ): Single<Response<ApiResponse<User>>>

    @DELETE("{appId}/user/users/{userId}")
    fun deleteUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String
    ): Single<Response<ApiResponse<DeleteUserResponse>>>

    @GET("{appId}/user/users/{userId}")
    fun getUserDetails(
            @Path("appId") appId: String,
            @Path("userId") userId: String
    ): Single<Response<ApiResponse<User>>>

    @GET("{appId}/user/users/")
    fun listUsers(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Single<Response<ApiResponse<ListUsersResponse>>>

    @POST("{appId}/user/users/{userId}/ban")
    fun setBanStatus(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: BanUserRequest
    ): Single<Response<ApiResponse<User>>>

    @POST("{appId}/user/search")
    fun searchUsers(
            @Path("appId") appId: String,
            @Body request: SearchUsersRequest
    ): Single<Response<ApiResponse<ListUsersResponse>>>

    @POST("{appId}/user/users/{userId}/shadowban")
    fun setShadowBanStatus(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: SetShadowBanStatusRequest
    ): Single<Response<ApiResponse<User>>>

    @POST("{appId}/user/users/{userId}/globalpurge")
    fun globallyPurgeUserContent(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: GloballyPurgeUserContentRequest
    ): Single<Response<ApiResponse<GloballyPurgeUserContentResponse>>>

    @POST("{appId}/user/users/{userId}/report")
    fun reportUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: ReportUserRequest
    ): Single<Response<ApiResponse<ReportUserResponse>>>

    @GET("{appId}/user/users/{userId}/notification/listnotifications")
    fun listUserNotifications(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Query("limit") limit: Int,
            @Query("filterNotificationTypes") filterNotificationTypes: List<String>? = null,
            @Query("cursor") cursor: String? = null,
            @Query("includeread") includeread: Boolean? = null,
            @Query("filterChatRoomId") filterChatRoomId: String? = null,
            @Query("filterChatRoomCustomId") filterChatRoomCustomId: String? = null
    ): Single<Response<ApiResponse<ListUserNotificationsResponse>>>

    @PUT("{appId}/user/users/{userId}/notification/notifications/{notificationId}/update")
    fun setUserNotificationAsRead(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Path(value = "notificationId", encoded = true) notificationId: String,
            @Query("read") read: Boolean
    ): Single<Response<ApiResponse<UserNotification>>>

    @PUT("{appId}/user/users/{userId}/notification/notificationsbyid/chateventid/{chatEventId}/update")
    fun setUserNotificationAsReadByChatEvent(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Path(value = "chatEventId", encoded = true) chatEventId: String,
            @Query("read") read: Boolean
    ): Single<Response<ApiResponse<UserNotification>>>

    @PUT("{appId}/user/users/{userId}/notification/notifications_all/markread")
    fun markAllUserNotificationsAsRead(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userid: String,
            @Query("delete") delete: Boolean
    ): Single<Response<ApiResponse<String>>>

}