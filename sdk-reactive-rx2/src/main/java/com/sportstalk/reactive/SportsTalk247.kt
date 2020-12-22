package com.sportstalk.reactive

import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.reactive.api.ChatClient
import com.sportstalk.reactive.api.UserClient
import com.sportstalk.reactive.impl.ChatClientImpl
import com.sportstalk.reactive.impl.UserClientImpl

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
}