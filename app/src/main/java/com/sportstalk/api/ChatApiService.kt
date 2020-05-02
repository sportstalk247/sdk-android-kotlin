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

}