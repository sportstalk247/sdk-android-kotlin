package com.sportstalk.impl.restapi.retrofit.services

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import retrofit2.Response
import retrofit2.http.*

interface ChatRetrofitService {

    @POST("{appId}/chat/rooms")
    suspend fun createRoom(
            @Path("appId") appId: String,
            @Body request: CreateChatRoomRequest
    ): Response<ApiResponse<ChatRoom>>

    @GET("{appId}/chat/rooms/{chatroomid}")
    suspend fun getRoomDetails(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): Response<ApiResponse<ChatRoom>>

    @GET("{appId}/chat/roomsbycustomid/{chatroom_customid}")
    suspend fun getRoomDetailsByCustomId(
            @Path("appId") appId: String,
            @Path(value = "chatroom_customid", encoded = true) chatRoomCustomId: String
    ): Response<ApiResponse<ChatRoom>>

    @DELETE("{appId}/chat/rooms/{chatroomid}")
    suspend fun deleteRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): Response<ApiResponse<DeleteChatRoomResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}")
    suspend fun updateRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: UpdateChatRoomRequest
    ): Response<ApiResponse<ChatRoom>>

    @GET("{appId}/chat/rooms/")
    suspend fun listRooms(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Response<ApiResponse<ListRoomsResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}/join")
    suspend fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: JoinChatRoomRequest
    ): Response<ApiResponse<JoinChatRoomResponse>>

    @POST("{appId}/chat/rooms/{chatRoomIdOrLabel}/join")
    suspend fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatRoomIdOrLabel") chatRoomId: String
    ): Response<ApiResponse<JoinChatRoomResponse>>

    @POST("{appId}/chat/roomsbycustomid/{chatroom_customid}/join")
    suspend fun joinRoomByCustomId(
            @Path("appId") appId: String,
            @Path(value = "chatroom_customid", encoded = true) chatRoomCustomId: String,
            @Body request: JoinChatRoomRequest
    ): Response<ApiResponse<JoinChatRoomResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/participants")
    suspend fun listRoomParticipants(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Response<ApiResponse<ListChatRoomParticipantsResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}/exit")
    suspend fun exitRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExitChatRoomRequest
    ): Response<ApiResponse<String>>

    @GET("{appId}/chat/rooms/{chatroomid}/updates")
    suspend fun getUpdates(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<GetUpdatesResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/listpreviousevents")
    suspend fun listPreviousEvents(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<ListEvents>>

    @GET("{appId}/chat/rooms/{chatroomid}/listeventshistory")
    suspend fun listEventsHistory(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<ListEvents>>

    @POST("{appId}/chat/rooms/{chatroomid}/command")
    suspend fun executeChatCommand(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExecuteChatCommandRequest
    ): Response<ApiResponse<ExecuteChatCommandResponse?>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{replyto}/reply")
    suspend fun sendThreadedReply(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("replyto") replyto: String,
            @Body request: SendThreadedReplyRequest
    ): Response<ApiResponse<ExecuteChatCommandResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{replyto}/quote")
    suspend fun sendQuotedReply(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("replyto") replyto: String,
            @Body request: SendQuotedReplyRequest
    ): Response<ApiResponse<ChatEvent>>

    @GET("{appId}/chat/rooms/{chatroomid}/messagesbyuser/{userid}")
    suspend fun listMessagesByUser(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("userid") userId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Response<ApiResponse<ListMessagesByUser>>

    @POST("{appId}/chat/rooms/{chatroomid}/bounce")
    suspend fun bounceUser(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: BounceUserRequest
    ): Response<ApiResponse<BounceUserResponse>>

    @PUT("{appId}/chat/rooms/{chatroomid}/events/{eventid}/setdeleted")
    suspend fun setMessageAsDeleted(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Query("userid") userid: String,
            @Query("deleted") deleted: Boolean,
            @Query("permanentifnoreplies") permanentifnoreplies: Boolean? = null
    ): Response<ApiResponse<DeleteEventResponse>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/report")
    suspend fun reportMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReportMessageRequest
    ): Response<ApiResponse<ChatEvent>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/react")
    suspend fun reactMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReactToAMessageRequest
    ): Response<ApiResponse<ChatEvent>>

}