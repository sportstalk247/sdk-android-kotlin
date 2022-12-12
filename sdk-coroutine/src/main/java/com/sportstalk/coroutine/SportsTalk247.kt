package com.sportstalk.coroutine

import com.sportstalk.coroutine.api.ChatClient
import com.sportstalk.coroutine.api.CommentClient
import com.sportstalk.coroutine.api.JWTRefreshManager
import com.sportstalk.coroutine.api.UserClient
import com.sportstalk.coroutine.impl.ChatClientImpl
import com.sportstalk.coroutine.impl.CommentClientImpl
import com.sportstalk.coroutine.impl.UserClientImpl
import com.sportstalk.datamodels.ClientConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow

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

    /**
     * Setter method to implement JWT Refresh callback.
     */
    @JvmStatic
    var jwtRefreshManager: JWTRefreshManager? = null
    @JvmStatic
    fun JWTRefreshCallback(
        callbackFlow: Flow<String>,
        coroutineScope: CoroutineScope = GlobalScope
    ) {
        jwtRefreshManager = JWTRefreshManager(
            refreshCallbackFlow = callbackFlow,
            coroutineScope = coroutineScope
        )
    }

}