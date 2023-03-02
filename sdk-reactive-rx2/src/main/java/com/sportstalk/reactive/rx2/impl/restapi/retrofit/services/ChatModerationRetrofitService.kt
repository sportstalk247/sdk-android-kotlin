package com.sportstalk.reactive.rx2.impl.restapi.retrofit.services

import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.moderation.ApproveMessageRequest
import com.sportstalk.datamodels.chat.moderation.ListMessagesNeedingModerationResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface ChatModerationRetrofitService {

    @POST("{appId}/chat/moderation/queues/events/{eventid}/applydecision")
    fun approveMessage(
        @Path("appId") appId: String,
        @Path("eventid") eventId: String,
        @Body request: ApproveMessageRequest
    ): Single<Response<ApiResponse<ChatEvent>>>

    @GET("{appId}/chat/moderation/queues/events")
    fun listMessagesNeedingModeration(
        @Path("appId") appId: String,
        @Query("roomId") roomId: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("cursor") cursor: String? = null
    ): Single<Response<ApiResponse<ListMessagesNeedingModerationResponse>>>

}