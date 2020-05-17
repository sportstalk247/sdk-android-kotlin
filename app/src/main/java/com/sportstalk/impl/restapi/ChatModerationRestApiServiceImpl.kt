package com.sportstalk.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.api.ChatModerationService
import com.sportstalk.impl.restapi.retrofit.services.ChatModerationRetrofitService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.moderation.ApproveMessageRequest
import com.sportstalk.models.chat.moderation.ListMessagesNeedingModerationResponse
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.CompletableFuture

class ChatModerationRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        mRetrofit: Retrofit
) : ChatModerationService {

    private val service: ChatModerationRetrofitService = mRetrofit.create()

    override fun approveMessage(
            eventId: String,
            approve: Boolean
    ): CompletableFuture<ApiResponse<ChatEvent>> =
            service.approveMessage(
                    appId = appId,
                    eventId = eventId,
                    request = ApproveMessageRequest(approve = approve)
            )

    override fun listMessagesNeedingModeration(): CompletableFuture<ApiResponse<ListMessagesNeedingModerationResponse>> =
            service.listMessagesNeedingModeration(
                    appId = appId
            )
}