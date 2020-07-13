package com.sportstalk.impl.restapi.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.moderation.ApproveMessageRequest
import com.sportstalk.models.chat.moderation.ListMessagesNeedingModerationResponse
import retrofit2.Response
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

interface ChatModerationRetrofitService {

    @POST("{appId}/chat/moderation/queues/events/{eventid}/applydecision")
    suspend fun approveMessage(
            @Path("appId") appId: String,
            @Path("eventid") eventId: String,
            @Body request: ApproveMessageRequest
    ): Response<ApiResponse<ChatEvent>>

    @GET("{appId}/chat/moderation/queues/events")
    suspend fun listMessagesNeedingModeration(
            @Path("appId") appId: String,
            @Query("roomId") roomId: String? = null,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Response<ApiResponse<ListMessagesNeedingModerationResponse>>

}