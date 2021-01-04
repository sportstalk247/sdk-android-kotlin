package com.sportstalk.reactive.rx2.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.users.*
import com.sportstalk.reactive.rx2.impl.handleSdkResponse
import com.sportstalk.reactive.rx2.impl.restapi.retrofit.services.UsersRetrofitService
import com.sportstalk.reactive.rx2.service.UserService
import io.reactivex.Single
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
): UserService {

    private val service: UsersRetrofitService = mRetrofit.create()

    override fun createOrUpdateUser(request: CreateUpdateUserRequest): Single<User> =
            service.createOrUpdateUser(
                    appId = appId,
                    userId = request.userid,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun deleteUser(userId: String): Single<DeleteUserResponse> =
            service.deleteUser(
                    appId = appId,
                    userId = URLEncoder.encode(userId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override fun getUserDetails(userId: String): Single<User> =
            service.getUserDetails(
                    appId = appId,
                    userId = userId
            )
                    .handleSdkResponse(json)

    override fun listUsers(limit: Int?, cursor: String?): Single<ListUsersResponse> =
            service.listUsers(
                    appId = appId,
                    cursor = cursor,
                    limit = limit
            )
                    .handleSdkResponse(json)

    override fun setBanStatus(userId: String, banned: Boolean): Single<User> =
            service.setBanStatus(
                    appId = appId,
                    userId = URLEncoder.encode(userId, Charsets.UTF_8.name()),
                    request = BanUserRequest(banned)
            )
                    .handleSdkResponse(json)

    override fun searchUsers(handle: String?, name: String?, userid: String?, limit: Int?, cursor: String?): Single<ListUsersResponse> =
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

    override fun shadowBanUser(userId: String, shadowban: Boolean, expireseconds: Long?): Single<User> =
            service.shadowBanUser(
                    appId = appId,
                    userId = userId,
                    request = ShadowBanUserRequest(
                            shadowban = shadowban,
                            expireseconds = expireseconds
                    )
            )
                    .handleSdkResponse(json)

    override fun globalPurge(userId: String, banned: Boolean): Single<GlobalPurgeResponse> =
            service.globalPurge(
                    appId = appId,
                    userId = userId,
                    request = GlobalPurgeRequest(banned = banned)
            )
                    .map { response ->
                        val respBody = response.body()

                        if (response.isSuccessful) {
                            GlobalPurgeResponse()
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
                    }

    override fun reportUser(userId: String, reporttype: String): Single<ReportUserResponse> =
            service.reportUser(
                    appId = appId,
                    userId = userId,
                    request = ReportUserRequest(userid = userId, reporttype = reporttype)
            )
                    .handleSdkResponse(json)
}