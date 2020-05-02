package com.sportstalk.impl.retrofit.users

import com.sportstalk.api.users.UsersApiService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.*
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

class UsersApiServiceImpl(
        private val appId: String,
        mRetrofit: Retrofit
) : UsersApiService {

    private val service: UsersRetrofitService = mRetrofit.create()

    override fun createUpdateUser(request: CreateUpdateUserRequest): CompletableFuture<ApiResponse<User>> =
            service.createUpdateUser(
                    appId = appId,
                    userId = request.userid,
                    request = request
            )

    override fun deleteUser(userId: String): CompletableFuture<ApiResponse<DeleteUserResponse>> =
            service.deleteUser(
                    appId = appId,
                    userId = userId
            )

    override fun getUserDetails(userId: String): CompletableFuture<ApiResponse<User>> =
            service.getUserDetails(
                    appId = appId,
                    userId = userId
            )

    override fun listUsers(limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListUsersResponse>> =
            service.listUsers(
                    appId = appId,
                    cursor = cursor,
                    limit = limit
            )

    override fun banUser(userId: String, banned: Boolean): CompletableFuture<ApiResponse<User>> =
            service.banUser(
                    appId = appId,
                    userId = userId,
                    request = BanUserRequest(banned)
            )
}

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

}