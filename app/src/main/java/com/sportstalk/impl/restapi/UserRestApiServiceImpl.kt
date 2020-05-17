package com.sportstalk.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.api.UserService
import com.sportstalk.impl.restapi.retrofit.services.UsersRetrofitService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.*
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.CompletableFuture

class UserRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        mRetrofit: Retrofit
) : UserService {

    private val service: UsersRetrofitService = mRetrofit.create()

    override fun createOrUpdateUser(request: CreateUpdateUserRequest): CompletableFuture<ApiResponse<User>> =
            service.createOrUpdateUser(
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

    override fun setBanStatus(userId: String, banned: Boolean): CompletableFuture<ApiResponse<User>> =
            service.setBanStatus(
                    appId = appId,
                    userId = userId,
                    request = BanUserRequest(banned)
            )

    override fun searchUsers(
            handle: String?,
            name: String?,
            userid: String?,
            limit: Int?,
            cursor: String?
    ): CompletableFuture<ApiResponse<ListUsersResponse>> =
            service.searchUsers(
                    appId = appId,
                    request = SearchUsersRequest(
                            handle = handle,
                            name = name,
                            userid = userid,
                            limit = limit,
                            cursor = cursor
                    )
            )
}