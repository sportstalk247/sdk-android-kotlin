package com.sportstalk.coroutine.impl.restapi.retrofit.services

import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.ApiResponse
import com.sportstalk.datamodels.users.SetShadowBanStatusRequest
import com.sportstalk.datamodels.users.User
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

    @GET("{appId}/chat/rooms/batch/details")
    suspend fun getRoomDetailsExtendedBatch(
            @Path("appId") appId: String,
            @Query("entity") entityTypes: List<String>,
            @Query("roomid", encoded = true) roomIds: List<String>,
            @Query("customid", encoded = true) customIds: List<String>
    ): Response<ApiResponse<GetRoomDetailsExtendedBatchResponse>>

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

    @POST("{appId}/chat/rooms/{chatroomid}/sessions/{userid}/touch")
    suspend fun touchSession(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("userid") userId: String
    ): Response<ApiResponse<String>>

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

    @GET("{appId}/chat/user/{userid}/subscriptions")
    suspend fun listUserSubscribedRooms(
            @Path("appId") appId: String,
            @Path("userid") userid: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null
    ): Response<ApiResponse<ListUserSubscribedRoomsResponse>>

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
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<GetUpdatesResponse>>

    @GET("{appId}/chat/rooms/{chatroomid}/listpreviousevents")
    suspend fun listPreviousEvents(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<ListEvents>>

    @GET("{appId}/chat/rooms/{chatroomid}/events/{eventId}")
    suspend fun getEventById(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventId") eventId: String
    ): Response<ApiResponse<ChatEvent>>

    @POST("{appId}/chat/rooms/{chatroomid}/users/{userId}/report")
    suspend fun reportUserInRoom(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("userId") userId: String,
            @Body request: ReportUserInRoomRequest
    ): Response<ApiResponse<ChatRoom>>

    @GET("{appId}/chat/rooms/{chatroomid}/listeventshistory")
    suspend fun listEventsHistory(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<ListEvents>>

    @GET("{appId}/chat/rooms/{chatroomid}/listeventsbytype")
    suspend fun listEventsByType(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Query("eventtype") eventtype: String,
            @Query("customtype") customtype: String? = null,
            @Query("limit") limit: Int? = null,
            @Query("cursor") cursor: String? = null /* eventId */
    ): Response<ApiResponse<ListEvents>>

    @GET("{appId}/chat/rooms/{chatroomid}/eventsbytimestamp/list/{timestamp}")
    suspend fun listEventsByTimestamp(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("timestamp") timestamp: Long,
            @Query("limitolder") limitolder: Int? = null,
            @Query("limitnewer") limitnewer: Int? = null
    ): Response<ApiResponse<ListEventsByTimestamp>>

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
    ): Response<ApiResponse<ChatEvent>>

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

    @POST("{appId}/chat/searchevents")
    suspend fun searchEventHistory(
            @Path("appId") appId: String,
            @Body request: SearchEventHistoryRequest
    ): Response<ApiResponse<SearchEventHistoryResponse>>

    @PUT("{appId}/chat/rooms/{chatroomid}/events/{eventid}")
    suspend fun updateChatMessage(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Body request: UpdateChatMessageRequest
    ): Response<ApiResponse<ChatEvent>>

    @DELETE("{appId}/chat/rooms/{chatroomid}/events/{eventid}")
    suspend fun permanentlyDeleteEvent(
            @Path("appId") appId: String,
            @Path("chatroomid") chatRoomId: String,
            @Path("eventid") eventId: String,
            @Query("userid") userid: String
    ): Response<ApiResponse<DeleteEventResponse>>

    @PUT("{appId}/chat/rooms/{chatroomid}/events/{eventid}/setdeleted")
    suspend fun flagEventLogicallyDeleted(
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

    @POST("{appId}/chat/rooms/{chatroomId}/shadowban")
    suspend fun shadowBanUser(
            @Path("appId") appId: String,
            @Path(value = "chatroomId", encoded = true) chatroomId: String,
            @Body request: ShadowBanUserInRoomRequest
    ): Response<ApiResponse<ChatRoom>>

    @POST("{appId}/chat/rooms/{chatroomId}/mute")
    suspend fun muteUser(
            @Path("appId") appId: String,
            @Path(value = "chatroomId", encoded = true) chatroomId: String,
            @Body request: MuteUserInRoomRequest
    ): Response<ApiResponse<ChatRoom>>

}