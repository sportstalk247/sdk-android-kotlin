package com.sportstalk.reactive.rx2

import androidx.annotation.RestrictTo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.ConfigUtils
import com.sportstalk.reactive.BuildConfig
import com.sportstalk.reactive.rx2.impl.restapi.ChatRestApiServiceImpl
import com.sportstalk.reactive.rx2.impl.restapi.CommentModerationRestApiServiceImpl
import com.sportstalk.reactive.rx2.impl.restapi.CommentRestApiServiceImpl
import com.sportstalk.reactive.rx2.impl.restapi.UserRestApiServiceImpl
import com.sportstalk.reactive.rx2.service.ChatService
import com.sportstalk.reactive.rx2.service.CommentModerationService
import com.sportstalk.reactive.rx2.service.CommentService
import com.sportstalk.reactive.rx2.service.UserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ServiceFactory {

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    object RestApi {
        @JvmStatic
        internal val json: Json by lazy {
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
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
                        .apply {
                            if(BuildConfig.DEBUG) {
                                addNetworkInterceptor(
                                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                                )
                            }
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
                            .baseUrl(
                                config.endpoint.let(ConfigUtils::validateEndpoint)
                            )
                            .addConverterFactory(
                                json.asConverterFactory("application/json".toMediaType())
                            )
                            .addCallAdapterFactory(
                                    RxJava2CallAdapterFactory.create()
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

    object Comment {
        @JvmStatic
        private val instances: HashMap<ClientConfig, CommentService> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun get(config: ClientConfig): CommentService =
            if(instances.containsKey(config)) {
                instances[config]!!
            } else {
                val okHttpClient = RestApi.getOkHttpInstance(config)
                val retrofit = RestApi.getRetrofitInstance(config, okHttpClient, RestApi.json)
                // REST API Implementation
                CommentRestApiServiceImpl(
                    appId = config.appId,
                    json = RestApi.json,
                    mRetrofit = retrofit
                ).also {
                    instances[config] = it
                }
            }

    }

    object CommentModeration {
        @JvmStatic
        private val instances: HashMap<ClientConfig, CommentModerationService> = hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        @JvmStatic
        fun get(config: ClientConfig): CommentModerationService =
            if(instances.containsKey(config)) {
                instances[config]!!
            } else {
                val okHttpClient = RestApi.getOkHttpInstance(config)
                val retrofit = RestApi.getRetrofitInstance(config, okHttpClient, RestApi.json)
                // REST API Implementation
                CommentModerationRestApiServiceImpl(
                    appId = config.appId,
                    json = RestApi.json,
                    mRetrofit = retrofit
                ).also {
                    instances[config] = it
                }
            }
    }

}
