package com.sportstalk

import android.content.Context
import com.sportstalk.api.ChatApiService
import com.sportstalk.api.ChatModerationApiService
import com.sportstalk.api.UsersApiService
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Retrofit

class SportsTalkManager private constructor(
        context: Context
) {

    var authToken: String
        get() = Dependencies.AuthToken.mInstance!!
        private set(_) {}

    var appId: String
        get() = Dependencies.AppId.mInstance!!
        private set(_) {}

    var okHttpClient: OkHttpClient
        get() = Dependencies._OkHttpClient.mInstance!!
        private set(_) {}

    var json: Json
        get() = Dependencies._Json.mInstance!!
        private set(_) {}

    var retrofit: Retrofit
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
        val apiUrlEndpoint = Dependencies.ApiEndpoint.getInstance(context)!!
        authToken = Dependencies.AuthToken.getInstance(context)!!
        appId = Dependencies.AppId.getInstance(context)!!
        Dependencies._OkHttpClient.getInstance(authToken)
        Dependencies._Json.getInstance()
        @Suppress("EXPERIMENTAL_API_USAGE")
        Dependencies._Retrofit.getInstance(urlEndpoint = apiUrlEndpoint, okHttpClient = okHttpClient, json = json)
        Dependencies.ApiServices.Users.getInstance(appId = appId, retrofit = retrofit)
        Dependencies.ApiServices.Chat.getInstance(appId = appId, retrofit = retrofit)
        Dependencies.ApiServices.ChatModeration.getInstance(appId = appId, retrofit = retrofit)
    }

    companion object {
        @JvmStatic
        lateinit var instance: SportsTalkManager

        fun init(context: Context): SportsTalkManager {
            instance = SportsTalkManager(context)
            return instance
        }
    }

}