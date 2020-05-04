package com.sportstalk.impl.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.*
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

interface UsersRetrofitService {

    @POST("{appId}/user/users/{userId}")
    fun createUpdateUser(
            @Path("appId") appId: String,
            @Path("userId") userId: String,
            @Body request: CreateUpdateUserRequest
    ): CompletableFuture<ApiResponse<User>>

    @DELETE("{appId}/user/users/{userId}")
    fun deleteUser(
            @Path("appId") appId: String,
            @Path("userId") userId: String
    ): CompletableFuture<ApiResponse<DeleteUserResponse>>

    @GET("{appId}/user/users/{userId}")
    fun getUserDetails(
            @Path("appId") appId: String,
            @Path("userId") userId: String
    ): CompletableFuture<ApiResponse<User>>

    @GET("{appId}/user/users/")
    fun listUsers(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<ApiResponse<ListUsersResponse>>

    @POST("{appId}/user/users/{userId}/ban")
    fun banUser(
            @Path("appId") appId: String,
            @Path("userId") userId: String,
            @Body request: BanUserRequest
    ): CompletableFuture<ApiResponse<User>>

    @POST("{appId}/user/search")
    fun searchUsers(
            @Path("appId") appId: String,
            @Body request: SearchUsersRequest
    ): CompletableFuture<ApiResponse<ListUsersResponse>>

}