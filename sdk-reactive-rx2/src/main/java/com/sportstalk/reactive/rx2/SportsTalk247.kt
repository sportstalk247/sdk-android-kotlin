package com.sportstalk.reactive.rx2

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.reactive.rx2.api.ChatClient
import com.sportstalk.reactive.rx2.api.JWTProvider
import com.sportstalk.reactive.rx2.api.UserClient
import com.sportstalk.reactive.rx2.impl.ChatClientImpl
import com.sportstalk.reactive.rx2.impl.UserClientImpl

object SportsTalk247 {
    /**
     * Factory method to create `UserClient` instance.
     */
    @JvmStatic
    fun UserClient(config: ClientConfig): UserClient =
            UserClientImpl(config)

    /**
     * Factory method to create `ChatClient` instance.
     */
    @JvmStatic
    fun ChatClient(config: ClientConfig): ChatClient =
            ChatClientImpl(config)

    @JvmStatic
    private var jwtProviders: MutableMap<ClientConfig, JWTProvider?> = mutableMapOf()
    /**
     * Method to set a JWT Provider instance for a specific config.
     */
    fun setJWTProvider(
        config: ClientConfig,
        provider: JWTProvider
    ) {
        jwtProviders[config] = provider
    }

    /**
     * Method to get the JWT Provider instance for the provided config.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @JvmStatic
    fun getJWTProvider(config: ClientConfig): JWTProvider? = jwtProviders[config]

}