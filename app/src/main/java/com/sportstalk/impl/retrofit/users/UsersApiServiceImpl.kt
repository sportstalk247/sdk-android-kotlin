package com.sportstalk.impl.retrofit.users

import com.sportstalk.api.users.UsersApiService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.User
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
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

}