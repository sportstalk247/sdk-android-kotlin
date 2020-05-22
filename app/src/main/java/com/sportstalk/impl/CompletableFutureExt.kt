package com.sportstalk.impl

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.Kind
import com.sportstalk.models.SportsTalkException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.util.concurrent.CompletableFuture

/**
 * This extension function wraps up SDK Response and will properly enclose
 * and throw [SportsTalkException] accordingly.
 */
fun <T> CompletableFuture<Response<ApiResponse<T>>>.handleSdkResponse(
        json: Json
): CompletableFuture<T> =
        handle { resp, err ->
            if (err != null) {
                throw SportsTalkException(message = err.message, err = err)
            } else {
                if (resp.isSuccessful) {
                    resp.body()!!.data!!
                } else {
                    throw resp.errorBody()?.string()?.let { errBodyStr ->
                        json.parse(SportsTalkException.serializer(), errBodyStr)
                    }
                            ?: SportsTalkException(
                                    kind = Kind.API,
                                    message = resp.message(),
                                    code = resp.code()
                            )
                }
            }
        }