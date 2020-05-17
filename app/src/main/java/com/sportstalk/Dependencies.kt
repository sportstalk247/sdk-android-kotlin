package com.sportstalk

import android.content.Context
import android.content.pm.PackageManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sportstalk.api.ChatService
import com.sportstalk.api.ChatModerationService
import com.sportstalk.api.UsersService
import com.sportstalk.impl.restapi.ChatRestApiServiceImpl
import com.sportstalk.impl.restapi.ChatModerationRestApiServiceImpl
import com.sportstalk.impl.restapi.UsersRestApiServiceImpl
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object Dependencies {

    object ApiEndpoint {
        @JvmStatic
        var mInstance: String? = null

        @JvmStatic
        fun getInstance(context: Context): String? {
            val appInfo =
                    try {
                        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                    } catch (err: Throwable) {
                        err.printStackTrace()
                        return null
                    }

            return appInfo.metaData.getString("sportstalk.api.url.endpoint")
                    .also {
                        // Assign to static mInstance
                        mInstance = it
                    }
        }
    }

    object AuthToken {
        @JvmStatic
        var mInstance: String? = null

        @JvmStatic
        fun getInstance(context: Context): String? {
            val appInfo =
                    try {
                        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                    } catch (err: Throwable) {
                        err.printStackTrace()
                        return null
                    }

            return appInfo.metaData.getString("sportstalk.api.auth_token")
                    .also {
                        // Assign to static mInstance
                        mInstance = it
                    }
        }
    }

    object AppId {
        @JvmStatic
        var mInstance: String? = null

        @JvmStatic
        fun getInstance(context: Context): String? {
            val appInfo =
                    try {
                        context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                    } catch (err: Throwable) {
                        err.printStackTrace()
                        return null
                    }

            return appInfo.metaData.getString("sportstalk.api.app_id")
                    .also {
                        // Assign to static mInstance
                        mInstance = it
                    }
        }
    }

    object _OkHttpClient {
        @JvmStatic
        var mInstance: OkHttpClient? = null

        @JvmStatic
        fun getInstance(
                authToken: String
        ): OkHttpClient {
            if (mInstance != null) {
                return mInstance!!
            }

            return OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(
                                chain.request().newBuilder()
                                        .addHeader("x-api-token", authToken)
                                        .build()
                        )
                    }
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()
                    .also {
                        // Assign to static mInstance
                        mInstance = it
                    }
        }
    }

    object _Json {
        @JvmStatic
        var mInstance: Json? = null

        @JvmStatic
        fun getInstance(): Json =
                if (mInstance != null) mInstance!!
                else Json(
                        JsonBuilder()
                                .apply {
                                    prettyPrint = true
                                    isLenient = true
                                    ignoreUnknownKeys = true
                                }
                                .buildConfiguration()
                ).also {
                    // Assign to static mInstance
                    mInstance = it
                }
    }

    @UnstableDefault
    object _Retrofit {
        @JvmStatic
        var mInstance: Retrofit? = null

        @JvmStatic
        fun getInstance(
                urlEndpoint: String,
                okHttpClient: OkHttpClient,
                json: Json
        ): Retrofit {
            if (mInstance != null) {
                return mInstance!!
            }

            return Retrofit.Builder()
                    .baseUrl(urlEndpoint)
                    .addConverterFactory(
                            json.asConverterFactory(MediaType.get("application/json"))
                    )
                    .client(okHttpClient)
                    .build()
                    .also {
                        // Assign to static mInstance
                        mInstance = it
                    }
        }
    }

    object ApiServices {

        object Users {
            @JvmStatic
            var mInstance: UsersService? = null

            @JvmStatic
            fun getInstance(
                    appId: String,
                    retrofit: retrofit2.Retrofit
            ): UsersService =
                    if(mInstance != null) mInstance!!
                    else UsersRestApiServiceImpl(
                            appId = appId,
                            mRetrofit = retrofit
                    )
                            .also {
                                // Assign to static mInstance
                                mInstance = it
                            }
        }

        object Chat {
            @JvmStatic
            var mInstance: ChatService? = null

            @JvmStatic
            fun getInstance(
                    appId: String,
                    retrofit: retrofit2.Retrofit
            ): ChatService =
                    if(mInstance != null) mInstance!!
                    else ChatRestApiServiceImpl(
                            appId = appId,
                            mRetrofit = retrofit
                    )
                            .also {
                                // Assign to static mInstance
                                mInstance = it
                            }
        }

        object ChatModeration {
            @JvmStatic
            var mInstance: ChatModerationService? = null

            @JvmStatic
            fun getInstance(
                    appId: String,
                    retrofit: retrofit2.Retrofit
            ): ChatModerationService =
                    if(mInstance != null) mInstance!!
                    else ChatModerationRestApiServiceImpl(
                            appId = appId,
                            mRetrofit = retrofit
                    )
                            .also {
                                // Assign to static mInstance
                                mInstance = it
                            }
        }

    }


}