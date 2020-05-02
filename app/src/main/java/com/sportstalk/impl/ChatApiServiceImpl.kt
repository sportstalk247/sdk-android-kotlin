package com.sportstalk.impl

import com.sportstalk.api.ChatApiService
import com.sportstalk.impl.retrofit.services.ChatRetrofitService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.CompletableFuture

class ChatApiServiceImpl(
        private val appId: String,
        mRetrofit: Retrofit
): ChatApiService {

    private val service: ChatRetrofitService = mRetrofit.create()

    override fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.createRoom(
                    appId = appId,
                    request = request
            )

    override fun getRoomDetails(chatRoomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            service.getRoomDetails(
                    appId = appId,
                    chatRoomId = chatRoomId
            )

    override fun deleteRoom(chatRoomId: String): CompletableFuture<ApiResponse<DeleteChatRoomResponse>> =
            service.deleteRoom(
                    appId = appId,
                    chatRoomId = chatRoomId
            )

    override fun updateRoom(request: UpdateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.updateRoom(
                    appId = appId,
                    chatRoomId = request.roomid,
                    request = request
            )

    override fun joinRoom(request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = request.roomid,
                    request = request
            )

    override fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomIdOrLabel
            )
}