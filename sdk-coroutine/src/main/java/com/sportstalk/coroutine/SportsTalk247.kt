package com.sportstalk.coroutine

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.api.*
import com.sportstalk.coroutine.impl.ChatClientImpl
import com.sportstalk.coroutine.impl.CommentClientImpl
import com.sportstalk.coroutine.impl.UserClientImpl
import com.sportstalk.datamodels.ClientConfig

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

    /**
     * Factory method to create `CommentClient` instance.
     */
    @JvmStatic
    fun CommentClient(config: ClientConfig): CommentClient =
            CommentClientImpl(config)

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