package com.sportstalk.reactive.rx2.service

import androidx.annotation.RestrictTo
import io.reactivex.Completable
import io.reactivex.Single
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.users.ListUserNotificationsResponse
import com.sportstalk.datamodels.users.User
import com.sportstalk.datamodels.users.UserNotification
import io.reactivex.Flowable

interface ChatService {

    /** Current user who joined the [ChatRoom]. Sets to null upon exit [ChatRoom] */
    var currentUser: User?

    /**
     * Only used if event smoothing is enabled.
     * Keeps a list of messages we already rendered so we can ignore them in getUpdates
     */
    var preRenderedMessages: MutableSet<String>

    /**
     * A coroutine Flow to which Execute Chat Command Response SPEECH is immediately emitted
     * to achieve smooth event update.
     */
    var chatEventsEmitter: Flowable<List<ChatEvent>>

    /**
     * A set of ChatRoom IDs to keep track which rooms are subscribed to get event updates
     */
    fun roomSubscriptions(): Set<String>

    /**
     * Get current event update cursor for the specified room ID
     */
    fun getChatRoomEventUpdateCursor(forRoomId: String): String?

    /**
     * Override current event update cursor
     */
    fun setChatRoomEventUpdateCursor(forRoomId: String, cursor: String)

    /**
     * Clear event update cursor
     */
    fun clearChatRoomEventUpdateCursor(fromRoomId: String)

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
    fun createRoom(request: CreateChatRoomRequest): Single<ChatRoom>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#9bac9724-7505-4e3e-966f-08cfebbca88d
     * - Get the details for a room
     */
    fun getRoomDetails(chatRoomId: String): Single<ChatRoom>

    /**
     * [GET] /{{api_appid}}/chat/roomsbycustomid/{{chat_create_room_customid}}
     * - https://apiref.sportstalk247.com/?version=latest#0fd07be5-f8d5-43d9-bf0f-8fb9829c172c
     * - Get the details for a room
     */
    fun getRoomDetailsByCustomId(chatRoomCustomId: String): Single<ChatRoom>

    /**
     * [DEL] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#c5ae345d-004d-478a-b543-5abaf691000d
     * - Deletes the specified room and all events contained therein) by ID
     */
    fun deleteRoom(chatRoomId: String): Single<DeleteChatRoomResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#96ef3138-4820-459b-b400-e9f25d5ddb00
     * - Updates an existing room
     */
    fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): Single<ChatRoom>

    /**
     * [GET] /{{api_appid}}/chat/rooms/
     * - https://apiref.sportstalk247.com/?version=latest#0580f06e-a58e-447a-aa1c-6071f3cfe1cf
     * - List all the available public chat rooms
     */
    fun listRooms(
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): Single<ListRoomsResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda
     * - Join A Room(Authenticated User)
     */
    fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): Single<JoinChatRoomResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda
     * - Join A Room(Anonymous User)
     */
    fun joinRoom(chatRoomIdOrLabel: String): Single<JoinChatRoomResponse>

    /**
     * [POST] /{{api_appid}}/chat/roomsbycustomid/{{chat_create_room_customid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#a64f2c32-6167-4639-9c32-413edded2c18
     * - This method is the same as Join Room, except you can use your customid
     */
    fun joinRoomByCustomId(
            chatRoomCustomId: String,
            request: JoinChatRoomRequest
    ): Single<JoinChatRoomResponse>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/participants?cursor&limit=200
     * - https://apiref.sportstalk247.com/?version=latest#1b1b82a9-2b2f-4785-993b-baed6e7eba7b
     * - List all the participants in the specified room
     */
    fun listRoomParticipants(
            chatRoomId: String,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): Single<ListChatRoomParticipantsResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/exit
     * - https://apiref.sportstalk247.com/?version=latest#408b43ca-fca9-4f2d-8883-f6f725d140f2
     * - Exit a Room
     */
    fun exitRoom(chatRoomId: String, userId: String): Completable

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/updates
     * - https://apiref.sportstalk247.com/?version=latest#be93067d-562e-41b2-97b2-b2bf177f1282
     * - Get the Recent Updates to a Room
     */
    fun getUpdates(
            chatRoomId: String,
            limit: Int? = null, // (optional) Number of events to return. Default is 100, maximum is 500.
            cursor: String? = null /* eventId */
    ): Single<GetUpdatesResponse>

    /**
     * Checks if the user has already reported a message.
     * @param which     The [ChatEvent] to check.
     * @param userid    The userid of the [User] to check if he/she has already reported the [ChatEvent]
     * @return          Returns true if specified [User] has reported the [ChatEvent]. Otherwise, returns false.
     */
    fun messageIsReported(
            which: ChatEvent,
            userid: String
    ): Boolean

    /**
     * Checks if the user has already reported a message.
     * @param which     The [ChatEvent] to check.
     * @param userid    The userid of the [User] to check if he/she has reacted to the [ChatEvent]
     * @param reaction  The type of reaction([EventReaction]]) to check.
     * @return          Returns true if specified [User] has reacted with a certain reaction([EventReaction]) to the [ChatEvent]. Otherwise, returns false.
     */
    fun messageIsReactedTo(
            which: ChatEvent,
            userid: String,
            reaction: String
    ): Boolean

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/listpreviousevents
     * - https://apiref.sportstalk247.com/?version=latest#f750f610-5db8-46ca-b9f7-a800c2e9c94a
     * - LIST PREVIOUS EVENTS
     */
    fun listPreviousEvents(
            chatRoomId: String,
            limit: Int? = null,
            cursor: String? = null
    ): Single<ListEvents>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{chatroomid}/events/{eventId}
     * - https://apiref.sportstalk247.com/?version=latest#04f8f563-eacf-4a64-9f00-b3d6c050a2fa
     * - Get Chat Event by ID
     */
    fun getEventById(
            chatRoomId: String,
            eventId: String
    ): Single<ChatEvent>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{chatroomid}/users/{userId}/report
     * - https://apiref.sportstalk247.com/?version=latest#94fdf593-06b6-41a2-80f6-79b8eb989b8b
     * - REPORTS a USER in the room
     */
    fun reportUserInRoom(
            chatRoomId: String,
            /** the person being reported */
            userid: String,
            /** the userid of the person doing the report */
            reporterid: String,
            /* [ReportType] */
            reporttype: String
    ): Single<ChatRoom>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/listeventshistory
     * - https://apiref.sportstalk247.com/?version=latest#b8ca9766-ab07-4c8c-8e25-002a24a8feaa
     * - LIST EVENTS HISTORY
     */
    fun listEventsHistory(
            chatRoomId: String,
            limit: Int? = null,
            cursor: String? = null
    ): Single<ListEvents>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/listeventsbytype?eventtype=&limit=&cursor=
     * - https://apiref.sportstalk247.com/?version=latest#68a36454-bf36-41e0-b8ef-6bcb2a13dd36
     * - List Events By Type
     */
    fun listEventsByType(
            chatRoomId: String,
            /** [EventType] */
            eventtype: String,
            customtype: String? = null,
            limit: Int? = null,
            cursor: String? = null
    ): Single<ListEvents>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/eventsbytimestamp/list/{timestamp}?limitolder=&limitnewer=&
     * - https://apiref.sportstalk247.com/?version=latest#fe87c58e-2fd3-4e59-80fa-07ffaed94ee0
     * - List Events By Timestamp
     */
    fun listEventsByTimestamp(
            chatRoomId: String,
            timestamp: Long,
            limitolder: Int? = null,
            limitnewer: Int? = null
    ): Single<ListEventsByTimestamp>

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
    fun executeChatCommand(
            chatRoomId: String,
            request: ExecuteChatCommandRequest
    ): Single<ExecuteChatCommandResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/command
     * - https://apiref.sportstalk247.com/?version=latest#d54ce72a-1a8a-4230-b950-0d1b345c20c6
     * - Reply to a Message (Threaded)
     *
     */
    fun sendThreadedReply(
            chatRoomId: String,
            replyTo: String,
            request: SendThreadedReplyRequest
    ): Single<ChatEvent>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{chatEventId}}/quote
     * - https://apiref.sportstalk247.com/?version=latest#c463cddd-c247-4e7c-8280-2d4880813149
     * - Quotes an existing message and republishes it with a new message
     */
    fun sendQuotedReply(
            chatRoomId: String,
            replyTo: String,
            request: SendQuotedReplyRequest
    ): Single<ChatEvent>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/messagesbyuser/{{userid}}?cursor&limit=200
     * - https://apiref.sportstalk247.com/?version=latest#0ec044c6-a3c0-478f-985a-156f6f5b660a
     * - Gets a list of users messages
     */
    fun listMessagesByUser(
            chatRoomId: String,
            userId: String,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): Single<ListMessagesByUser>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/bounce
     * - https://apiref.sportstalk247.com/?version=latest#7116d7ca-a1b8-44c1-8894-bea85225e4c7
     * - Bounce User (ban user from room)
     */
    fun bounceUser(
            chatRoomId: String,
            request: BounceUserRequest
    ): Single<BounceUserResponse>

    /**
     * [POST] /{{api_appid}}/chat/searchevents
     * - https://apiref.sportstalk247.com/?version=latest#a6b5380c-4e6c-4ded-b0b1-55225bcdea67
     * - UPDATES the contents of an existing chat event
     */
    fun searchEventHistory(
            request: SearchEventHistoryRequest
    ): Single<SearchEventHistoryResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/bounce
     * - https://apiref.sportstalk247.com/?version=latest#207a7dfa-5233-4acb-b855-031928941b25
     * - UPDATES the contents of an existing chat event
     */
    fun updateChatMessage(
            chatRoomId: String,
            eventId: String,
            request: UpdateChatMessageRequest
    ): Single<ChatEvent>

    /**
     * Delete Event
     * [DEL] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}?userid={{userid}}
     * - https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de
     * - Deletes an event from the room
     */
    fun permanentlyDeleteEvent(
            chatRoomId: String,
            eventId: String,
            userid: String
    ): Single<DeleteEventResponse>

    /**
     * Flag Message Event As Deleted
     * [PUT] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}/setdeleted?userid=&deleted=true&permanentifnoreplies
     * - https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de
     * - Removes a message from a room
     */
    fun flagEventLogicallyDeleted(
            chatRoomId: String,
            eventId: String,
            userid: String,
            deleted: Boolean,
            permanentifnoreplies: Boolean? = null
    ): Single<DeleteEventResponse>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}/report
     * - https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de
     * - REPORTS a message to the moderation
     */
    fun reportMessage(
            chatRoomId: String,
            eventId: String,
            request: ReportMessageRequest
    ): Single<ChatEvent>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}/react
     * - https://apiref.sportstalk247.com/?version=latest#977044d8-9133-4185-ac1f-4d96a40aa60b
     * - Adds or removes a reaction to an existing event
     */
    fun reactToEvent(
            chatRoomId: String,
            eventId: String,
            request: ReactToAMessageRequest
    ): Single<ChatEvent>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/shadowban
     * - https://apiref.sportstalk247.com/?version=latest#c4a83dfa-9e83-4eb8-b371-e105463f3a52
     * - Will toggle the user's shadow banned flag
     */
    fun shadowBanUser(
            chatRoomId: String,
            userid: String,
            applyeffect: Boolean,
            expireseconds: Long? = null
    ): Single<ChatRoom>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/mute
     * - https://apiref.sportstalk247.com/?version=latest#67d66190-eb25-4f19-9d65-c127ed368233
     * - Will toggle the user's mute effect
     */
    fun muteUser(
            chatRoomId: String,
            userid: String,
            applyeffect: Boolean,
            expireseconds: Long? = null
    ): Single<ChatRoom>

}