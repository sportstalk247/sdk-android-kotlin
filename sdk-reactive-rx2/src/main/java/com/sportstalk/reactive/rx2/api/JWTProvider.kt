package com.sportstalk.reactive.rx2.api

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class JWTProvider(
    private var token: String? = null,
    private val tokenRefreshObservable: (() -> Single<String?>)? = null
) {

    private val refreshSubj = PublishSubject.create<String?>()

    fun getToken(): String? = this.token

    fun setToken(value: String?) {
        this.token = value
    }

    fun refreshToken() {
        refreshSubj.onNext(this.token ?: "")
    }

    fun observe(): Observable<String?> =
        refreshSubj
            .flatMap<String?> {
                return@flatMap tokenRefreshObservable?.invoke()?.toObservable()
                    ?: Observable.never<String?>()
            }
            .doOnNext { newToken ->
                this@JWTProvider.token = newToken
            }
}