package com.sportstalk.reactive.rx2

import androidx.annotation.RestrictTo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.reactive.rx2.impl.restapi.ChatRestApiServiceImpl
import com.sportstalk.reactive.rx2.impl.restapi.UserRestApiServiceImpl
import com.sportstalk.reactive.rx2.service.ChatService
import com.sportstalk.reactive.rx2.service.UserService
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
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
                encodeDefaults = false
                prettyPrint = true
                strictMode = false
//                isLenient = true
//                ignoreUnknownKeys = true
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

}
