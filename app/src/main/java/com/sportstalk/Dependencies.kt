package com.sportstalk

import android.content.Context
import android.content.pm.PackageManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

object Dependencies {

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
                    .also { mInstance = it }
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
                    .also { mInstance = it }
        }
    }

    object OkHttp {
        @JvmStatic
        private var mInstance: OkHttpClient? = null

        @JvmStatic
        fun getInstance(context: Context): OkHttpClient {
            if(mInstance != null) {
                return mInstance!!
            }

            val authToken = AuthToken.getInstance(context) ?: ""

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
                    .also { _instance ->
                        mInstance = _instance
                    }
        }
    }

    @UnstableDefault
    object Retrofit {
        @JvmStatic
        private var mInstance: retrofit2.Retrofit? = null

        @JvmStatic
        fun getInstance(
                okHttpClient: OkHttpClient,
                urlEndpoint: String
        ): retrofit2.Retrofit {
            if(mInstance != null) {
                return mInstance!!
            }

            return retrofit2.Retrofit.Builder()
                    .baseUrl(urlEndpoint)
                    .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
        }
    }

}