package com.sportstalk.impl.restapi.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.*
import retrofit2.Response
import retrofit2.http.*
import java.util.concurrent.CompletableFuture


interface UsersRetrofitService {

    @POST("{appId}/user/users/{userId}")
    fun createOrUpdateUser(
            @Path("appId") appId: String,
            @Path("userId") userId: String,
            @Body request: CreateUpdateUserRequest
    ): CompletableFuture<Response<ApiResponse<User>>>

    @DELETE("{appId}/user/users/{userId}")
    fun deleteUser(
            @Path("appId") appId: String,
            @Path(value = "userId", encoded = true) userId: String
    ): CompletableFuture<Response<ApiResponse<DeleteUserResponse>>>

    @GET("{appId}/user/users/{userId}")
    fun getUserDetails(
            @Path("appId") appId: String,
            @Path("userId") userId: String
    ): CompletableFuture<Response<ApiResponse<User>>>

    @GET("{appId}/user/users/")
    fun listUsers(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<Response<ApiResponse<ListUsersResponse>>>

    @POST("{appId}/user/users/{userId}/ban")
    fun setBanStatus(
            @Path("appId") appId: String,
            @Path("userId") userId: String,
            @Body request: BanUserRequest
    ): CompletableFuture<Response<ApiResponse<User>>>

    @POST("{appId}/user/search")
    fun searchUsers(
            @Path("appId") appId: String,
            @Body request: SearchUsersRequest
    ): CompletableFuture<Response<ApiResponse<ListUsersResponse>>>

}