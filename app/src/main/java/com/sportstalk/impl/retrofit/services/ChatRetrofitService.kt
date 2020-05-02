package com.sportstalk.impl.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatRoom
import com.sportstalk.models.chat.CreateRoomRequest
import com.sportstalk.models.chat.DeleteChatRoomResponse
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

interface ChatRetrofitService {

    @POST("{appId}/chat/rooms")
    fun createRoom(
            @Path("appId") appId: String,
            @Body request: CreateRoomRequest
    ): CompletableFuture<ApiResponse<ChatRoom>>

    @GET("{appId}/chat/rooms/{chatroomid}")
    fun getRoomDetails(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): CompletableFuture<ApiResponse<ChatRoom>>

    @DELETE("{appId}/chat/rooms/{chatroomid}")
    fun deleteRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): CompletableFuture<ApiResponse<DeleteChatRoomResponse>>

}