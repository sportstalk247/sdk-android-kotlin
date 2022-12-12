package com.sportstalk.coroutine

import androidx.annotation.RestrictTo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sportstalk.coroutine.service.ChatModerationService
import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.coroutine.service.UserService
import com.sportstalk.coroutine.impl.restapi.ChatModerationRestApiServiceImpl
import com.sportstalk.coroutine.impl.restapi.ChatRestApiServiceImpl
import com.sportstalk.coroutine.impl.restapi.UserRestApiServiceImpl
import com.sportstalk.datamodels.ClientConfig
import kotlinx.serialization.json.Json
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
            Json {
                encodeDefaults = false
                prettyPrint = true
                strictMode = false
            }
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
                                    .apply {
                                        val jwtProvider = SportsTalk247.getJWTProvider(config)
                                        val customJWT = jwtProvider?.getToken()
                                        customJWT?.trim()?.takeIf { it.isNotEmpty() }?.let { jwt ->
                                            addHeader("Authorization", "Bearer $jwt")
                                        }
                                    }
                                    .build()
                            )
                        }
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
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

    }

    object User {
        @JvmStatic
        private val instances: HashMap<ClientConfig, UserService> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun get(config: ClientConfig): UserService =
                if (instances.containsKey(config)) {
                    instances[config]!!
                } else {
                    val okHttpClient = RestApi.getOkHttpInstance(config)
                    val retrofit = RestApi.getRetrofitInstance(config, okHttpClient, RestApi.json)
                    // REST API Implementation
                    UserRestApiServiceImpl(
                            appId = config.appId,
                            json = RestApi.json,
                            mRetrofit = retrofit
                    ).also {
                        instances[config] = it
                    }
                }
    }

    object Chat {
        @JvmStatic
        private val instances: HashMap<ClientConfig, ChatService> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun get(config: ClientConfig): ChatService =
                if (instances.containsKey(config)) {
                    instances[config]!!
                } else {
                    val okHttpClient = RestApi.getOkHttpInstance(config)
                    val retrofit = RestApi.getRetrofitInstance(config, okHttpClient, RestApi.json)
                    // REST API Implementation
                    ChatRestApiServiceImpl(
                            appId = config.appId,
                            json = RestApi.json,
                            mRetrofit = retrofit
                    ).also {
                        instances[config] = it
                    }
                }
    }

    object ChatModeration {
        @JvmStatic
        private val instances: HashMap<ClientConfig, ChatModerationService> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun get(config: ClientConfig): ChatModerationService =
                if (instances.containsKey(config)) {
                    instances[config]!!
                } else {
                    val okHttpClient = RestApi.getOkHttpInstance(config)
                    val retrofit = RestApi.getRetrofitInstance(config, okHttpClient, RestApi.json)
                    // REST API Implementation
                    ChatModerationRestApiServiceImpl(
                            appId = config.appId,
                            json = RestApi.json,
                            mRetrofit = retrofit
                    ).also {
                        instances[config] = it
                    }
                }
    }

}
