package com.sportstalk.reactive.impl.restapi.retrofit.services

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
    fun shadowBanUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: ShadowBanUserRequest
    ): Single<Response<ApiResponse<User>>>

    @POST("{appId}/user/users/{userId}/globalpurge")
    fun globalPurge(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: GlobalPurgeRequest
    ): Single<Response<ApiResponse<GlobalPurgeResponse>>>

    @POST("{appId}/user/users/{userId}/report")
    fun reportUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String,
            @Body request: ReportUserRequest
    ): Single<Response<ApiResponse<ReportUserResponse>>>

}