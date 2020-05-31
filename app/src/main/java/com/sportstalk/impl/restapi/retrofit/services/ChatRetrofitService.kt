package com.sportstalk.impl.restapi.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import retrofit2.Response
import retrofit2.http.*
import java.util.concurrent.CompletableFuture

interface ChatRetrofitService {

    @POST("{appId}/chat/rooms")
    fun createRoom(
            @Path("appId") appId: String,
            @Body request: CreateChatRoomRequest
    ): CompletableFuture<Response<ApiResponse<ChatRoom>>>

    @GET("{appId}/chat/rooms/{chatroomid}")
    fun getRoomDetails(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): CompletableFuture<Response<ApiResponse<ChatRoom>>>

    @GET("{appId}/chat/roomsbycustomid/{chatroom_customid}")
    fun getRoomDetailsByCustomId(
            @Path("appId") appId: String,
            @Path(value = "chatroom_customid", encoded = true) chatRoomCustomId: String
    ): CompletableFuture<Response<ApiResponse<ChatRoom>>>

    @DELETE("{appId}/chat/rooms/{chatroomid}")
    fun deleteRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): CompletableFuture<Response<ApiResponse<DeleteChatRoomResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}")
    fun updateRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: UpdateChatRoomRequest
    ): CompletableFuture<Response<ApiResponse<ChatRoom>>>

    @GET("{appId}/chat/rooms/")
    fun listRooms(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<Response<ApiResponse<ListRoomsResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/join")
    fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: JoinChatRoomRequest
    ): CompletableFuture<Response<ApiResponse<JoinChatRoomResponse>>>

    @POST("{appId}/chat/rooms/{chatRoomIdOrLabel}/join")
    fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatRoomIdOrLabel") chatRoomId: String
    ): CompletableFuture<Response<ApiResponse<JoinChatRoomResponse>>>

    @POST("{appId}/chat/roomsbycustomid/{chatroom_customid}/join")
    fun joinRoomByCustomId(
            @Path("appId") appId: String,
            @Path(value = "chatroom_customid", encoded = true) chatRoomCustomId: String,
            @Body request: JoinChatRoomRequest
    ): CompletableFuture<Response<ApiResponse<JoinChatRoomResponse>>>

    @GET("{appId}/chat/rooms/{chatroomid}/participants")
    fun listRoomParticipants(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<Response<ApiResponse<ListChatRoomParticipantsResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/exit")
    fun exitRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExitChatRoomRequest
    ): CompletableFuture<Response<ApiResponse<String>>>

    @GET("{appId}/chat/rooms/{chatroomid}/updates")
    fun getUpdates(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("cursor") cursor: String? = null /* eventId */
    ): CompletableFuture<Response<ApiResponse<GetUpdatesResponse>>>

    @GET("{appId}/chat/rooms/{chatroomid}/listpreviousevents")
    fun listPreviousEvents(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): CompletableFuture<Response<ApiResponse<ListEvents>>>

    @GET("{appId}/chat/rooms/{chatroomid}/listeventshistory")
    fun listEventsHistory(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): CompletableFuture<Response<ApiResponse<ListEvents>>>

    @POST("{appId}/chat/rooms/{chatroomid}/command")
    fun executeChatCommand(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExecuteChatCommandRequest
    ): CompletableFuture<Response<ApiResponse<ExecuteChatCommandResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{replyto}/reply")
    fun sendThreadedReply(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("replyto") replyto: String,
            @Body request: SendThreadedReplyRequest
    ): CompletableFuture<Response<ApiResponse<ExecuteChatCommandResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{replyto}/quote")
    fun sendQuotedReply(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("replyto") replyto: String,
            @Body request: SendQuotedReplyRequest
    ): CompletableFuture<Response<ApiResponse<ChatEvent>>>

    @GET("{appId}/chat/rooms/{chatroomid}/messagesbyuser/{userid}")
    fun listMessagesByUser(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("userid") userId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): CompletableFuture<Response<ApiResponse<ListMessagesByUser>>>

    @PUT("{appId}/chat/rooms/{chatroomid}/events/{eventid}/setdeleted")
    fun setMessageAsDeleted(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Query("userid") userid: String,
            @Query("deleted") deleted: Boolean,
            @Query("permanentifnoreplies") permanentifnoreplies: Boolean? = null
    ): CompletableFuture<Response<ApiResponse<DeleteEventResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/report")
    fun reportMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReportMessageRequest
    ): CompletableFuture<Response<ApiResponse<ChatEvent>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/react")
    fun reactMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReactToAMessageRequest
    ): CompletableFuture<Response<ApiResponse<ChatEvent>>>

}