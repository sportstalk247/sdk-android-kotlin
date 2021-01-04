package com.sportstalk.reactive.rx2.impl

import com.sportstalk.datamodels.*
import io.reactivex.Single
import kotlinx.serialization.json.Json
import retrofit2.Response

/**
 * This extension function wraps up SDK Response and will properly enclose
 * and throw [SportsTalkException] accordingly.
 */
fun <T> Single<Response<ApiResponse<T>>>.handleSdkResponse(
        json: Json
): Single<T> =
        map { response ->
            if (response.isSuccessful && response.body() != null && response.body()!!.data != null) {
                response.body()!!.data!!
            } else {
                throw response.errorBody()?.string()?.let { errBodyStr ->
                    json.decodeFromString(SportsTalkException.serializer(), errBodyStr)
                }
                        ?: SportsTalkException(
                                kind = Kind.API,
                                message = response.message(),
                                code = response.code()
                        )
            }
        }