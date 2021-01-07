package com.sportstalk.coroutine.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.service.UserService
import com.sportstalk.coroutine.impl.handleSdkResponse
import com.sportstalk.coroutine.impl.restapi.retrofit.services.UsersRetrofitService
import com.sportstalk.datamodels.*
import com.sportstalk.datamodels.users.*
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
            } catch (err: SportsTalkException) {
                throw err
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
            } catch (err: SportsTalkException) {
                throw err
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
            } catch (err: SportsTalkException) {
                throw err
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
            } catch (err: SportsTalkException) {
                throw err
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
            } catch (err: SportsTalkException) {
                throw err
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
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun setShadowBanStatus(
            userId: String,
            shadowban: Boolean,
            expireseconds: Long?
    ): User =
            try {
                service.setShadowBanStatus(
                        appId = appId,
                        userId = userId,
                        request = SetShadowBanStatusRequest(
                                shadowban = shadowban,
                                expireseconds = expireseconds
                        )
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun globallyPurgeUserContent(userId: String, banned: Boolean): GloballyPurgeUserContentResponse =
            try {
                val response = service.globallyPurgeUserContent(
                        appId = appId,
                        userId = userId,
                        request = GloballyPurgeUserContentRequest(banned = banned)
                )

                val respBody = response.body()

                if (response.isSuccessful) {
                    GloballyPurgeUserContentResponse()
                } else {
                    throw response.errorBody()?.string()?.let { errBodyStr ->
                        json.decodeFromString(SportsTalkException.serializer(), errBodyStr)
                    }
                            ?: SportsTalkException(
                                    kind = respBody?.kind ?: Kind.API,
                                    message = respBody?.message ?: response.message(),
                                    code = respBody?.code ?: response.code()
                            )
                }
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun reportUser(userId: String, reporttype: String): ReportUserResponse =
            try {
                service.reportUser(
                        appId = appId,
                        userId = userId,
                        request = ReportUserRequest(userid = userId, reporttype = reporttype)
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }
}