package com.sportstalk.impl.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.moderation.ApproveMessageRequest
import com.sportstalk.models.chat.moderation.ListMessagesNeedingModerationResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.CompletableFuture

interface ChatModerationRetrofitService {

    @POST("{appId}/chat/moderation/queues/events/{eventid}/applydecision")
    fun approveMessage(
            @Path("appId") appId: String,
            @Path("eventid") eventId: String,
            @Body request: ApproveMessageRequest
    ): CompletableFuture<ApiResponse<ChatEvent>>

    @GET("{appId}/chat/moderation/queues/events")
    fun listMessagesNeedingModeration(
            @Path("appId") appId: String
    ): CompletableFuture<ApiResponse<ListMessagesNeedingModerationResponse>>

}