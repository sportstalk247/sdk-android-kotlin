package com.sportstalk.reactive.rx2

import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.reactive.rx2.api.ChatClient
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
}