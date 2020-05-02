package com.sportstalk.impl

import com.sportstalk.api.ChatApiService
import com.sportstalk.impl.retrofit.services.ChatRetrofitService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatRoom
import com.sportstalk.models.chat.CreateRoomRequest
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.CompletableFuture

class ChatApiServiceImpl(
        private val appId: String,
        mRetrofit: Retrofit
): ChatApiService {

    private val service: ChatRetrofitService = mRetrofit.create()

    override fun createRoom(request: CreateRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.createRoom(
                    appId = appId,
                    request = request
            )
}