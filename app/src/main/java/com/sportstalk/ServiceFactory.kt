package com.sportstalk

import androidx.annotation.RestrictTo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sportstalk.api.UserService
import com.sportstalk.impl.restapi.UserRestApiServiceImpl
import com.sportstalk.models.ClientConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ServiceFactory {

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    object RestApi {
        @JvmStatic
        internal val json: Json by lazy {
            Json(
                    JsonBuilder()
                            .apply {
                                prettyPrint = true
                                isLenient = true
                                ignoreUnknownKeys = true
                            }
                            .buildConfiguration()
            )
        }

        @JvmStatic
        private val okHttpClientInstances: HashMap<String /* config.apiToken */, OkHttpClient> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun getOkHttpInstance(
                config: ClientConfig
        ): OkHttpClient {
            return if (okHttpClientInstances.containsKey(config.apiToken)) okHttpClientInstances[config.apiToken]!! else
                OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            chain.proceed(
                                    chain.request().newBuilder()
                                            .addHeader("x-api-token", config.apiToken)
                                            .build()
                            )
                        }
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build()
        }

        @JvmStatic
        private val okRetrofitInstances: HashMap<String /* config.endpoint */, Retrofit> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun getRetrofitInstance(
                config: ClientConfig,
                okHttpClient: OkHttpClient,
                json: Json
        ): Retrofit =
                if (okRetrofitInstances.containsKey(config.endpoint)) okRetrofitInstances[config.endpoint]!! else
                    Retrofit.Builder()
                            .baseUrl(config.endpoint)
                            .addConverterFactory(
                                    json.asConverterFactory(MediaType.get("application/json"))
                            )
                            .client(okHttpClient)
                            .build()


        object User {
            @JvmStatic
            private val instances: HashMap<ClientConfig, UserService> = hashMapOf()

            @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
            @JvmStatic
            fun get(config: ClientConfig): UserService =
                    if (instances.containsKey(config)) {
                        instances[config]!!
                    } else {
                        val okHttpClient = getOkHttpInstance(config)
                        val retrofit = getRetrofitInstance(config, okHttpClient, json)
                        // REST API Implementation
                        UserRestApiServiceImpl(
                                appId = config.appId,
                                mRetrofit = retrofit
                        ).also {
                            instances[config] = it
                        }
                    }
        }

    }

}