package com.sportstalk.impl.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

interface ChatRetrofitService {

    @POST("{appId}/chat/rooms")
    fun createRoom(
            @Path("appId") appId: String,
            @Body request: CreateChatRoomRequest
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

    @POST("{appId}/chat/rooms/{chatroomid}")
    fun updateRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: UpdateChatRoomRequest
    ): CompletableFuture<ApiResponse<ChatRoom>>

    @POST("{appId}/chat/rooms/{chatroomid}/join")
    fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: JoinChatRoomRequest
    ): CompletableFuture<ApiResponse<JoinChatRoomResponse>>

    @POST("{appId}/chat/rooms/{chatRoomIdOrLabel}/join")
    fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatRoomIdOrLabel") chatRoomId: String
    ): CompletableFuture<ApiResponse<JoinChatRoomResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/participants")
    fun listRoomParticipants(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<ApiResponse<ListChatRoomParticipantsResponse>>

}