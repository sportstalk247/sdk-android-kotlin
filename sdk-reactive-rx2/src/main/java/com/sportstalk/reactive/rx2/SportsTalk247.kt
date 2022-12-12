package com.sportstalk.reactive.rx2

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.reactive.rx2.api.ChatClient
import com.sportstalk.reactive.rx2.api.JWTRefreshManager
import com.sportstalk.reactive.rx2.api.UserClient
import com.sportstalk.reactive.rx2.impl.ChatClientImpl
import com.sportstalk.reactive.rx2.impl.UserClientImpl
import io.reactivex.Flowable
import io.reactivex.internal.disposables.DisposableContainer

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

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @JvmStatic
    var jwtRefreshManager: JWTRefreshManager? = null
    /**
     * Setter method to implement JWT Refresh callback.
     */
    @JvmStatic
    fun JWTRefreshCallback(
        callbackFlowable: Flowable<String>,
        disposeBag: DisposableContainer
    ) {
        jwtRefreshManager = JWTRefreshManager(
            refreshCallbackFlowable = callbackFlowable,
            disposeBag = disposeBag
        )
    }

}