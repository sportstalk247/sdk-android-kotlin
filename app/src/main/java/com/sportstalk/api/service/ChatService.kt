package com.sportstalk.api.service

import androidx.annotation.RestrictTo
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import java.util.concurrent.CompletableFuture

interface ChatService {

    /**
    * A set of ChatRoom IDs to keep track which rooms are subscribed to get event updates
    */
    val roomSubscriptions: MutableSet<String>

    /**
     * Chatroom ID paired with current event cursor
     */
    val chatRoomEventCursor: HashMap<String, String>

    /**
     * Signals the START of event updates being emitted
     */
    fun startListeningToChatUpdates(forRoomId: String)
    /**
     * Signals the END of event updates being emitted
     */
    fun stopListeningToChatUpdates(forRoomId: String)

    /**
     * [POST] /{{api_appid}}/chat/rooms
     * - https://apiref.sportstalk247.com/?version=latest#8b2eea78-82bc-4cae-9cfa-175a00a9e15b
     * - Creates a new chat room
     */
    suspend fun createRoom(request: CreateChatRoomRequest): ChatRoom

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#9bac9724-7505-4e3e-966f-08cfebbca88d
     * - Get the details for a room
     */
    suspend fun getRoomDetails(chatRoomId: String): ChatRoom

    /**
     * [GET] /{{api_appid}}/chat/roomsbycustomid/{{chat_create_room_customid}}
     * - https://apiref.sportstalk247.com/?version=latest#0fd07be5-f8d5-43d9-bf0f-8fb9829c172c
     * - Get the details for a room
     */
    suspend fun getRoomDetailsByCustomId(chatRoomCustomId: String): ChatRoom

    /**
     * [DEL] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#c5ae345d-004d-478a-b543-5abaf691000d
     * - Deletes the specified room and all events contained therein) by ID
     */
    suspend fun deleteRoom(chatRoomId: String): DeleteChatRoomResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#96ef3138-4820-459b-b400-e9f25d5ddb00
     * - Updates an existing room
     */
    suspend fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): ChatRoom

    /**
     * [GET] /{{api_appid}}/chat/rooms/
     * - https://apiref.sportstalk247.com/?version=latest#0580f06e-a58e-447a-aa1c-6071f3cfe1cf
     * - List all the available public chat rooms
     */
    suspend fun listRooms(
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): ListRoomsResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda
     * - Join A Room(Authenticated User)
     */
    suspend fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda
     * - Join A Room(Anonymous User)
     */
    suspend fun joinRoom(chatRoomIdOrLabel: String): JoinChatRoomResponse

    /**
     * [POST] /{{api_appid}}/chat/roomsbycustomid/{{chat_create_room_customid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#a64f2c32-6167-4639-9c32-413edded2c18
     * - This method is the same as Join Room, except you can use your customid
     */
    suspend fun joinRoomByCustomId(
            chatRoomCustomId: String,
            request: JoinChatRoomRequest
    ): JoinChatRoomResponse

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/participants?cursor&limit=200
     * - https://apiref.sportstalk247.com/?version=latest#1b1b82a9-2b2f-4785-993b-baed6e7eba7b
     * - List all the participants in the specified room
     */
    suspend fun listRoomParticipants(
            chatRoomId: String,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): ListChatRoomParticipantsResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/exit
     * - https://apiref.sportstalk247.com/?version=latest#408b43ca-fca9-4f2d-8883-f6f725d140f2
     * - Exit a Room
     */
    suspend fun exitRoom(chatRoomId: String, userId: String)

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/updates
     * - https://apiref.sportstalk247.com/?version=latest#be93067d-562e-41b2-97b2-b2bf177f1282
     * - Get the Recent Updates to a Room
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    suspend fun getUpdates(
            chatRoomId: String,
            cursor: String? = null /* eventId */
    ): GetUpdatesResponse

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/listpreviousevents
     * - https://apiref.sportstalk247.com/?version=latest#f750f610-5db8-46ca-b9f7-a800c2e9c94a
     * - LIST PREVIOUS EVENTS
     */
    suspend fun listPreviousEvents(
            chatRoomId: String,
            limit: Int? = null,
            cursor: String? = null
    ): ListEvents

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/listeventshistory
     * - https://apiref.sportstalk247.com/?version=latest#b8ca9766-ab07-4c8c-8e25-002a24a8feaa
     * - LIST EVENTS HISTORY
     */
    suspend fun listEventsHistory(
            chatRoomId: String,
            limit: Int? = null,
            cursor: String? = null
    ): ListEvents

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/command
     * - https://apiref.sportstalk247.com/?version=latest#c81e90fc-1a54-40bb-a75b-2fc935c12b59
     * - Executes a command in a chat room
     *
     * - https://apiref.sportstalk247.com/?version=latest#d291ac74-e3f2-48cc-a3f0-ae4470a950a4
     * - Execute Dance Action
     *
     * - https://apiref.sportstalk247.com/?version=latest#d54ce72a-1a8a-4230-b950-0d1b345c20c6
     * - Reply to a Message
     *
     */
    suspend fun executeChatCommand(
            chatRoomId: String,
            request: ExecuteChatCommandRequest
    ): ExecuteChatCommandResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/command
     * - https://apiref.sportstalk247.com/?version=latest#d54ce72a-1a8a-4230-b950-0d1b345c20c6
     * - Reply to a Message (Threaded)
     *
     */
    suspend fun sendThreadedReply(
            chatRoomId: String,
            replyTo: String,
            request: SendThreadedReplyRequest
    ): ExecuteChatCommandResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{chatEventId}}/quote
     * - https://apiref.sportstalk247.com/?version=latest#c463cddd-c247-4e7c-8280-2d4880813149
     * - Quotes an existing message and republishes it with a new message
     */
    suspend fun sendQuotedReply(
            chatRoomId: String,
            replyTo: String,
            request: SendQuotedReplyRequest
    ): ChatEvent

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/messagesbyuser/{{userid}}?cursor&limit=200
     * - https://apiref.sportstalk247.com/?version=latest#0ec044c6-a3c0-478f-985a-156f6f5b660a
     * - Gets a list of users messages
     */
    suspend fun listMessagesByUser(
            chatRoomId: String,
            userId: String,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): ListMessagesByUser

    // TODO:: `Removes a message` API is broken at the moment
    /**
     * Flag Message Event As Deleted
     * [PUT] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}/setdeleted?userid=&deleted=true&permanentifnoreplies
     * - https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de
     * - Removes a message from a room
     */
    suspend fun removeEvent(
            chatRoomId: String,
            eventId: String,
            userid: String,
            deleted: Boolean,
            permanentifnoreplies: Boolean? = null
    ): DeleteEventResponse
    /**
     * Convenience Function to Remove Message where `deleted` = true and `permanentifnoreplies` = true
     */
    suspend fun permanentlyDeleteEvent(
            chatRoomId: String,
            eventId: String,
            userid: String,
            permanentifnoreplies: Boolean? = null
    ): DeleteEventResponse
    /**
     * Convenience Function to Remove Message where `deleted` = false and `permanentifnoreplies` = false
     */
    suspend fun flagEventLogicallyDeleted(
            chatRoomId: String,
            eventId: String,
            userid: String,
            permanentifnoreplies: Boolean? = null
    ): DeleteEventResponse

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}/report
     * - https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de
     * - REPORTS a message to the moderation
     */
    suspend fun reportMessage(
            chatRoomId: String,
            eventId: String,
            request: ReportMessageRequest
    ): ChatEvent

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}/react
     * - https://apiref.sportstalk247.com/?version=latest#977044d8-9133-4185-ac1f-4d96a40aa60b
     * - Adds or removes a reaction to an existing event
     */
    suspend fun reactToEvent(
            chatRoomId: String,
            eventId: String,
            request: ReactToAMessageRequest
    ): ChatEvent

}