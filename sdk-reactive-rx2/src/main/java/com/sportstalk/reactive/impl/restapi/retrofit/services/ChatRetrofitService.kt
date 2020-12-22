package com.sportstalk.reactive.impl.restapi.retrofit.services

import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.ApiResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface ChatRetrofitService {

    @POST("{appId}/chat/rooms")
    fun createRoom(
            @Path("appId") appId: String,
            @Body request: CreateChatRoomRequest
    ): Single<Response<ApiResponse<ChatRoom>>>

    @GET("{appId}/chat/rooms/{chatroomid}")
    fun getRoomDetails(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): Single<Response<ApiResponse<ChatRoom>>>

    @GET("{appId}/chat/roomsbycustomid/{chatroom_customid}")
    fun getRoomDetailsByCustomId(
            @Path("appId") appId: String,
            @Path(value = "chatroom_customid", encoded = true) chatRoomCustomId: String
    ): Single<Response<ApiResponse<ChatRoom>>>

    @DELETE("{appId}/chat/rooms/{chatroomid}")
    fun deleteRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String
    ): Single<Response<ApiResponse<DeleteChatRoomResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}")
    fun updateRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: UpdateChatRoomRequest
    ): Single<Response<ApiResponse<ChatRoom>>>

    @GET("{appId}/chat/rooms/")
    fun listRooms(
            @Path("appId") appId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Single<Response<ApiResponse<ListRoomsResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/join")
    fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: JoinChatRoomRequest
    ): Single<Response<ApiResponse<JoinChatRoomResponse>>>

    @POST("{appId}/chat/rooms/{chatRoomIdOrLabel}/join")
    fun joinRoom(
            @Path("appId") appId: String,
            @Path("chatRoomIdOrLabel") chatRoomId: String
    ): Single<Response<ApiResponse<JoinChatRoomResponse>>>

    @POST("{appId}/chat/roomsbycustomid/{chatroom_customid}/join")
    fun joinRoomByCustomId(
            @Path("appId") appId: String,
            @Path(value = "chatroom_customid", encoded = true) chatRoomCustomId: String,
            @Body request: JoinChatRoomRequest
    ): Single<Response<ApiResponse<JoinChatRoomResponse>>>

    @GET("{appId}/chat/rooms/{chatroomid}/participants")
    fun listRoomParticipants(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Single<Response<ApiResponse<ListChatRoomParticipantsResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/exit")
    fun exitRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExitChatRoomRequest
    ): Single<Response<ApiResponse<String>>>

    @GET("{appId}/chat/rooms/{chatroomid}/updates")
    fun getUpdates(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Single<Response<ApiResponse<GetUpdatesResponse>>>

    @GET("{appId}/chat/rooms/{chatroomid}/listpreviousevents")
    fun listPreviousEvents(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Single<Response<ApiResponse<ListEvents>>>

    @GET("{appId}/chat/rooms/{chatroomid}/events/{eventId}")
    fun getEventById(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventId") eventId: String
    ): Single<Response<ApiResponse<ChatEvent>>>

    @GET("{appId}/chat/rooms/{chatroomid}/listeventshistory")
    fun listEventsHistory(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Single<Response<ApiResponse<ListEvents>>>

    @POST("{appId}/chat/rooms/{chatroomid}/command")
    fun executeChatCommand(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: ExecuteChatCommandRequest
    ): Single<Response<ApiResponse<ExecuteChatCommandResponse?>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{replyto}/reply")
    fun sendThreadedReply(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("replyto") replyto: String,
            @Body request: SendThreadedReplyRequest
    ): Single<Response<ApiResponse<ExecuteChatCommandResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{replyto}/quote")
    fun sendQuotedReply(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("replyto") replyto: String,
            @Body request: SendQuotedReplyRequest
    ): Single<Response<ApiResponse<ChatEvent>>>

    @GET("{appId}/chat/rooms/{chatroomid}/messagesbyuser/{userid}")
    fun listMessagesByUser(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("userid") userId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Single<Response<ApiResponse<ListMessagesByUser>>>

    @POST("{appId}/chat/rooms/{chatroomid}/bounce")
    fun bounceUser(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Body request: BounceUserRequest
    ): Single<Response<ApiResponse<BounceUserResponse>>>

    @DELETE("{appId}/chat/rooms/{chatroomid}/events/{eventid}")
    fun deleteEvent(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Query("userid") userid: String
    ): Single<Response<ApiResponse<DeleteEventResponse>>>

    @PUT("{appId}/chat/rooms/{chatroomid}/events/{eventid}/setdeleted")
    fun setMessageAsDeleted(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Query("userid") userid: String,
            @Query("deleted") deleted: Boolean,
            @Query("permanentifnoreplies") permanentifnoreplies: Boolean? = null
    ): Single<Response<ApiResponse<DeleteEventResponse>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/report")
    fun reportMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReportMessageRequest
    ): Single<Response<ApiResponse<ChatEvent>>>

    @POST("{appId}/chat/rooms/{chatroomid}/events/{eventid}/react")
    fun reactMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: ReactToAMessageRequest
    ): Single<Response<ApiResponse<ChatEvent>>>

}