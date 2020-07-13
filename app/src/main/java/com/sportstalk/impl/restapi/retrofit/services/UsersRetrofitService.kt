package com.sportstalk.impl.restapi.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.*
import retrofit2.Response
import retrofit2.http.*
import java.util.concurrent.CompletableFuture


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

}