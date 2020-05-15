package com.sportstalk

import android.content.Context
import com.sportstalk.api.ChatApiService
import com.sportstalk.api.ChatModerationApiService
import com.sportstalk.api.UsersApiService
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class SportsTalkManager private constructor(
        context: Context,
        apiUrlEndpoint: String? = null,
        authToken: String? = null,
        appId: String? = null
) {

    private var apiUrlEndpoint: String
        get() = Dependencies.ApiEndpoint.mInstance!!
        private set(_) {}

    private var authToken: String
        get() = Dependencies.AuthToken.mInstance!!
        private set(_) {}

    private var appId: String
        get() = Dependencies.AppId.mInstance!!
        private set(_) {}

    private var okHttpClient: OkHttpClient
        get() = Dependencies._OkHttpClient.mInstance!!
        private set(_) {}

    private var json: Json
        get() = Dependencies._Json.mInstance!!
        private set(_) {}

    private var retrofit: Retrofit
        get() = Dependencies._Retrofit.mInstance!!
        private set(_) {}

    var usersApiService: UsersApiService
        get() = Dependencies.ApiServices.Users.mInstance!!
        private set(_) {}

    var chatApiService: ChatApiService
        get() = Dependencies.ApiServices.Chat.mInstance!!
        private set(_) {}

    var chatModerationApiService: ChatModerationApiService
        get() = Dependencies.ApiServices.ChatModeration.mInstance!!
        private set(_) {}

    init {
        this.apiUrlEndpoint = apiUrlEndpoint ?: Dependencies.ApiEndpoint.getInstance(context)!!
        this.authToken = authToken ?: Dependencies.AuthToken.getInstance(context)!!
        this.appId = appId ?: Dependencies.AppId.getInstance(context)!!

        okHttpClient = Dependencies._OkHttpClient.getInstance(this.authToken)
        json = Dependencies._Json.getInstance()
        @Suppress("EXPERIMENTAL_API_USAGE")
        retrofit = Dependencies._Retrofit.getInstance(urlEndpoint = this.apiUrlEndpoint, okHttpClient = okHttpClient, json = json)
        usersApiService = Dependencies.ApiServices.Users.getInstance(appId = this.appId, retrofit = retrofit)
        chatApiService = Dependencies.ApiServices.Chat.getInstance(appId = this.appId, retrofit = retrofit)
        chatModerationApiService = Dependencies.ApiServices.ChatModeration.getInstance(appId = this.appId, retrofit = retrofit)
    }

    companion object {
        @JvmStatic
        lateinit var instance: SportsTalkManager

        /**
         * Called at the beginning of Application app-start.
         * - Allow developers to dynamically/explicitly provide SDK appId/authToken/apiUrlEndpoint
         */
        fun init(
                context: Context,
                appId: String? = null,
                authToken: String? = null,
                apiUrlEndpoint: String? = null
        ): SportsTalkManager {
            instance = SportsTalkManager(context, appId, authToken, apiUrlEndpoint)
            return instance
        }

        /**
         * This allows developers to dynamically/explicitly set SDK appId/authToken/apiUrlEndpoint
         */
        fun config(
                appId: String? = null,
                authToken: String? = null,
                apiUrlEndpoint: String? = null
        ) {
            appId?.let { instance.appId = it }
            authToken?.let { instance.authToken = it }
            apiUrlEndpoint?.let { instance.apiUrlEndpoint = it }
        }
    }

}