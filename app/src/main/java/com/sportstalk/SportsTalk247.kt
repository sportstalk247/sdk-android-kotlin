package com.sportstalk

import com.sportstalk.api.ChatClient
import com.sportstalk.api.CommentClient
import com.sportstalk.api.UserClient
import com.sportstalk.impl.ChatClientImpl
import com.sportstalk.impl.CommentClientImpl
import com.sportstalk.impl.UserClientImpl
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
}