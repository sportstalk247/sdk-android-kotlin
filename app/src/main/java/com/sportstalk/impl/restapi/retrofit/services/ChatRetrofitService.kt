package com.sportstalk.impl.restapi.retrofit.services

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

    @GET("{appId}/chat/roomsbycustomid/{chatroom_customid}")
    fun getRoomDetailsByCustomId(
            @Path("appId") appId: String,
            @Path("chatroom_customid") chatRoomCustomId: String
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

    @GET("{appId}/chat/rooms/")
    fun listRooms(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<ApiResponse<ListRoomsResponse>>

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

    @POST("{appId}/chat/roomsbycustomid/{chatroom_customid}/join")
    fun joinRoomByCustomId(
            @Path("appId") appId: String,
            @Path("chatroom_customid") chatRoomCustomId: String,
            @Body request: JoinChatRoomRequest
    ): CompletableFuture<ApiResponse<JoinChatRoomResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/participants")
    fun listRoomParticipants(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<ApiResponse<ListChatRoomParticipantsResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}/exit")
    fun exitRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExitChatRoomRequest
    ): CompletableFuture<ApiResponse<ExitChatRoomResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/updates")
    fun getUpdates(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("cursor") cursor: String? = null /* eventId */
    ): CompletableFuture<ApiResponse<GetUpdatesResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}/command")
    fun executeChatCommand(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExecuteChatCommandRequest
    ): CompletableFuture<ApiResponse<ExecuteChatCommandResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/messagesbyuser/{userid}")
    fun listMessagesByUser(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("userid") userId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<ApiResponse<ListMessagesByUser>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/report")
    fun reportMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReportMessageRequest
    ): CompletableFuture<ApiResponse<ChatEvent>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/react")
    fun reactMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReactToAMessageRequest
    ): CompletableFuture<ApiResponse<ChatEvent>>

}