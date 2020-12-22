package com.sportstalk.impl

import com.sportstalk.datamodels.*
import kotlinx.serialization.json.Json
import retrofit2.Response

/**
 * This extension function wraps up SDK Response and will properly enclose
 * and throw [SportsTalkException] accordingly.
 */
fun <T> Response<ApiResponse<T>>.handleSdkResponse(
        json: Json
): T =
        if (this.isSuccessful && this.body() != null && this.body()!!.data != null) {
            this.body()!!.data!!
        } else {
            throw this.errorBody()?.string()?.let { errBodyStr ->
                json.decodeFromString(SportsTalkException.serializer(), errBodyStr)
            }
                    ?: SportsTalkException(
                            kind = Kind.API,
                            message = message(),
                            code = code()
                    )
        }