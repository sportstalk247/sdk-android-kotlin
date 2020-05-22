package com.sportstalk.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.api.service.UserService
import com.sportstalk.impl.handleSdkResponse
import com.sportstalk.impl.restapi.retrofit.services.UsersRetrofitService
import com.sportstalk.models.users.*
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class UserRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        private val json: Json,
        mRetrofit: Retrofit
) : UserService {

    private val service: UsersRetrofitService = mRetrofit.create()

    override fun createOrUpdateUser(request: CreateUpdateUserRequest): CompletableFuture<User> =
            service.createOrUpdateUser(
                    appId = appId,
                    userId = request.userid,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun deleteUser(userId: String): CompletableFuture<DeleteUserResponse> =
            service.deleteUser(
                    appId = appId,
                    userId = URLEncoder.encode(userId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override fun getUserDetails(userId: String): CompletableFuture<User> =
            service.getUserDetails(
                    appId = appId,
                    userId = userId
            )
                    .handleSdkResponse(json)

    override fun listUsers(limit: Int?, cursor: String?): CompletableFuture<ListUsersResponse> =
            service.listUsers(
                    appId = appId,
                    cursor = cursor,
                    limit = limit
            )
                    .handleSdkResponse(json)

    override fun setBanStatus(userId: String, banned: Boolean): CompletableFuture<User> =
            service.setBanStatus(
                    appId = appId,
                    userId = userId,
                    request = BanUserRequest(banned)
            )
                    .handleSdkResponse(json)

    override fun searchUsers(
            handle: String?,
            name: String?,
            userid: String?,
            limit: Int?,
            cursor: String?
    ): CompletableFuture<ListUsersResponse> =
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
                    .handleSdkResponse(json)
}