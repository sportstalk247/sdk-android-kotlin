package com.sportstalk.api

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatRoom
import com.sportstalk.models.chat.CreateRoomRequest
import com.sportstalk.models.chat.DeleteChatRoomResponse
import java.util.concurrent.CompletableFuture

interface ChatApiService {

    /**
     * [POST] /{{api_appid}}/chat/rooms
     * - https://apiref.sportstalk247.com/?version=latest#8b2eea78-82bc-4cae-9cfa-175a00a9e15b
     * - Creates a new chat room
     */
    fun createRoom(request: CreateRoomRequest): CompletableFuture<ApiResponse<ChatRoom>>

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



}