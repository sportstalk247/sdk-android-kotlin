package com.sportstalk.coroutine.impl.restapi.retrofit.services

import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.moderation.ApproveMessageRequest
import com.sportstalk.datamodels.chat.moderation.ListMessagesNeedingModerationResponse
import com.sportstalk.datamodels.chat.moderation.PurgeUserMessagesRequest
import retrofit2.Response
import retrofit2.http.*

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

    @POST("{appId}/chat/rooms/{chatroomid}/commands/purge")
    suspend fun purgeUserMessages(
            @Path("appId") appId: String,
            @Path("chatroomid") roomId: String,
            @Body request: PurgeUserMessagesRequest? = null,
    ): Response<ApiResponse<String>>

}