package com.sportstalk.reactive.rx2.api

import io.reactivex.Flowable
import io.reactivex.internal.disposables.DisposableContainer

class JWTRefreshManager(
    private val refreshCallbackFlowable: Flowable<String>,
    private val disposeBag: DisposableContainer
) {

    var customJWT: String? = null
        private set

    init {
        val disposable = refreshCallbackFlowable.doOnNext { newJWT ->
                this@JWTRefreshManager.customJWT = newJWT
            }.subscribe()

        disposeBag.add(disposable)
    }

}