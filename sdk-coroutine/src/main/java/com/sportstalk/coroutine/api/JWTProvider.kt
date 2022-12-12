package com.sportstalk.coroutine.api

class JWTProvider {
    private var token: String? = null

    fun getToken(): String? = this.token

    fun setToken(value: String?) {
        this.token = value
    }
}