package com.sportstalk.coroutine.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@UseExperimental(ExperimentalCoroutinesApi::class)
class JWTRefreshManager(
    private val refreshCallbackFlow: Flow<String>,
    private val coroutineScope: CoroutineScope = GlobalScope
) {

    var customJWT: String? = null
        private set

    private val mutex = Mutex()

    init {
        refreshCallbackFlow.onEach { newJWT ->
            mutex.withLock {
                this@JWTRefreshManager.customJWT = newJWT
            }

        }.launchIn(coroutineScope)
    }

}