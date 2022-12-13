package com.sportstalk.reactive.rx2.api

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

class JWTProvider(
    initialToken: String? = null,
    private val refreshCallback: ((String?/* Old Token */) -> Single<String?>)? = null
) {

    private var token: String? = null
    private val refreshSubj = PublishSubject.create<String?>()

    init {
        this.token = initialToken
    }

    fun getToken(): String? = this.token

    fun setToken(value: String?) {
        this.token = value
    }

    fun refreshToken() {
        refreshSubj.onNext(this.token ?: "")
    }

    fun observe(): Observable<String?> =
        refreshSubj
            .flatMap<String?> { oldToken ->
                return@flatMap refreshCallback?.invoke(oldToken)?.toObservable()
                    ?: Observable.never<String?>()
            }
            .doOnNext { newToken ->
                this@JWTProvider.token = newToken
            }
}