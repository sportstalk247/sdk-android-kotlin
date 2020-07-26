package com.sportstalk.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.api.service.UserService
import com.sportstalk.impl.handleSdkResponse
import com.sportstalk.impl.restapi.retrofit.services.UsersRetrofitService
import com.sportstalk.models.SportsTalkException
import com.sportstalk.models.users.*
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create
import java.net.URLEncoder

class UserRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        private val json: Json,
        mRetrofit: Retrofit
) : UserService {

    private val service: UsersRetrofitService = mRetrofit.create()

    override suspend fun createOrUpdateUser(request: CreateUpdateUserRequest): User =
            try {
                service.createOrUpdateUser(
                        appId = appId,
                        userId = request.userid,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun deleteUser(userId: String): DeleteUserResponse =
            try {
                service.deleteUser(
                        appId = appId,
                        userId = URLEncoder.encode(userId, Charsets.UTF_8.name())
                )
                        .handleSdkResponse(json)
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun getUserDetails(userId: String): User =
            try {
                service.getUserDetails(
                        appId = appId,
                        userId = userId
                )
                        .handleSdkResponse(json)
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listUsers(limit: Int?, cursor: String?): ListUsersResponse =
            try {
                service.listUsers(
                        appId = appId,
                        cursor = cursor,
                        limit = limit
                )
                        .handleSdkResponse(json)
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun setBanStatus(userId: String, banned: Boolean): User =
            try {
                service.setBanStatus(
                        appId = appId,
                        userId = URLEncoder.encode(userId, Charsets.UTF_8.name()),
                        request = BanUserRequest(banned)
                )
                        .handleSdkResponse(json)
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun searchUsers(
            handle: String?,
            name: String?,
            userid: String?,
            limit: Int?,
            cursor: String?
    ): ListUsersResponse =
            try {
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
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }
}