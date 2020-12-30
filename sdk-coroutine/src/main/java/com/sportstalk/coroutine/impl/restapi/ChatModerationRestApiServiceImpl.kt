package com.sportstalk.coroutine.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.service.ChatModerationService
import com.sportstalk.coroutine.impl.handleSdkResponse
import com.sportstalk.coroutine.impl.restapi.retrofit.services.ChatModerationRetrofitService
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.moderation.ApproveMessageRequest
import com.sportstalk.datamodels.chat.moderation.ListMessagesNeedingModerationResponse
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create

class ChatModerationRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        private val json: Json,
        mRetrofit: Retrofit
) : ChatModerationService {

    private val service: ChatModerationRetrofitService = mRetrofit.create()

    override suspend fun approveMessage(
            eventId: String,
            approve: Boolean
    ): ChatEvent =
            try {
                service.approveMessage(
                        appId = appId,
                        eventId = eventId,
                        request = ApproveMessageRequest(approve = approve)
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listMessagesNeedingModeration(roomId: String?, limit: Int?, cursor: String?): ListMessagesNeedingModerationResponse =
            try {
                service.listMessagesNeedingModeration(
                        appId = appId,
                        roomId = roomId,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }
}