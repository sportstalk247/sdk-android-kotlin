package com.sportstalk

import com.sportstalk.api.ChatClient
import com.sportstalk.api.UserClient
import com.sportstalk.models.ClientConfig

object SportsTalk247 {
    /**
     * Factory method to create `IUserClient` instance.
     */
    @JvmStatic
    fun UserClient(config: ClientConfig): UserClient =
            UserClient(config)

    /**
     * Factory method to create `IChatClient` instance.
     */
    @JvmStatic
    fun ChatClient(config: ClientConfig): ChatClient =
            ChatClient(config)
}