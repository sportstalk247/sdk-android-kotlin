package com.sportstalk

import android.content.Context
import android.content.pm.PackageManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sportstalk.api.UsersApiService
import com.sportstalk.impl.UsersApiServiceImpl
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
        private var mInstance: String? = null

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
        private var mInstance: String? = null

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
        private var mInstance: String? = null

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
        private var mInstance: OkHttpClient? = null

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
        private var mInstance: Json? = null

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
        private var mInstance: Retrofit? = null

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
            private var mInstance: UsersApiService? = null

            @JvmStatic
            fun getInstance(
                    appId: String,
                    retrofit: retrofit2.Retrofit
            ): UsersApiService {
                return UsersApiServiceImpl(
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


}