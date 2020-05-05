package com.sportstalk.impl.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import com.sportstalk.models.chat.moderation.ApproveMessageRequest
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

interface ChatModerationRetrofitService {

    @POST("{appId}/chat/moderation/queues/events/{eventid}/applydecision")
    fun approveMessage(
            @Path("appId") appId: String,
            @Path("eventid") eventId: String,
            @Body request: ApproveMessageRequest
    ): CompletableFuture<ApiResponse<ChatEvent>>

}