package com.sportstalk.coroutine.api

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class JWTProvider(
    initialToken: String? = null,
    private val refreshCallback: (suspend (String? /* Old Token */) -> String? /* New Token */)? = null
) {
    private var token: String? = null
    private val mutex = Mutex()

    private val refreshChannel = Channel<String?>(capacity = Channel.RENDEZVOUS)

    init {
        this.token = initialToken
    }

    fun getToken(): String? = this.token

    fun setToken(value: String?) {
        this.token = value
    }

    fun refreshToken() {
        refreshChannel.sendBlocking(this.token)
    }

    /**
     * This allows the refresh callback to execute from within the Coroutine Flow.
     * @return  A cold observable that performs refresh token operation, then internally updates and stores
     * the new token.
     */
    @UseExperimental(FlowPreview::class)
    fun observe(): Flow<String?> =
        refreshChannel
            .consumeAsFlow()
            .map { oldToken ->
                refreshCallback?.invoke(oldToken)
            }
            .onEach { newToken ->
                mutex.withLock {
                    this@JWTProvider.token = newToken
                }
            }

}