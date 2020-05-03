package com.sportstalk.api

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import java.util.concurrent.CompletableFuture

interface ChatApiService {

    /**
     * [POST] /{{api_appid}}/chat/rooms
     * - https://apiref.sportstalk247.com/?version=latest#8b2eea78-82bc-4cae-9cfa-175a00a9e15b
     * - Creates a new chat room
     */
    fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#9bac9724-7505-4e3e-966f-08cfebbca88d
     * - Get the details for a room
     */
    fun getRoomDetails(chatRoomId: String): CompletableFuture<ApiResponse<ChatRoom>>

    /**
     * [DEL] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#c5ae345d-004d-478a-b543-5abaf691000d
     * - Deletes the specified room and all events contained therein) by ID
     */
    fun deleteRoom(chatRoomId: String): CompletableFuture<ApiResponse<DeleteChatRoomResponse>>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}
     * - https://apiref.sportstalk247.com/?version=latest#96ef3138-4820-459b-b400-e9f25d5ddb00
     * - Updates an existing room
     */
    fun updateRoom(request: UpdateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda
     * - Join A Room(Authenticated User)
     */
    fun joinRoom(request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/join
     * - https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda
     * - Join A Room(Anonymous User)
     */
    fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<ApiResponse<JoinChatRoomResponse>>

    /**
     * [GET] /{{api_appid}}/chat/rooms/{{chatroomid}}/participants?cursor&limit=200
     * - https://apiref.sportstalk247.com/?version=latest#1b1b82a9-2b2f-4785-993b-baed6e7eba7b
     * - List all the participants in the specified room
     */
    fun listRoomParticipants(
            chatRoomId: String,
            limit: Int? = null /* Defaults to 200 on backend API server */,
            cursor: String? = null
    ): CompletableFuture<ApiResponse<ListChatRoomParticipantsResponse>>

    /**
     * [POST] /{{api_appid}}/chat/rooms/{{chatroomid}}/exit
     * - https://apiref.sportstalk247.com/?version=latest#408b43ca-fca9-4f2d-8883-f6f725d140f2
     * - Exit a Room
     */
    fun exitRoom(chatRoomId: String, userId: String): CompletableFuture<ApiResponse<ExitChatRoomResponse>>

    // fun getUpdates(chatRoomId: String): CompletableFuture<ApiResponse<???>>

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
    ): CompletableFuture<ApiResponse<ExecuteChatCommandResponse>>

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
    ): CompletableFuture<ApiResponse<ListMessagesByUser>>

//    TODO:: `Removes a message` API is broken at the moment
//    /**
//     * [DEL] /{{api_appid}}/chat/rooms/{{chatroomid}}/events/{{eventid}}
//     * - https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de
//     * - Removes a message from a room
//     */
//    fun removeMessage(
//            chatRoomId: String,
//            eventId: String
//    ): CompletableFuture<ApiResponse<ExecuteChatCommandResponse>>



}