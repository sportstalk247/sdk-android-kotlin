package com.sportstalk.impl.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatRoom
import com.sportstalk.models.chat.CreateRoomRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.CompletableFuture

interface ChatRetrofitService {

    @POST("{appId}/chat/rooms")
    fun createRoom(
            @Path("appId") appId: String,
            @Body request: CreateRoomRequest
    ): CompletableFuture<ApiResponse<ChatRoom>>

}