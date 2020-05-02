package com.sportstalk.api

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatRoom
import com.sportstalk.models.chat.CreateRoomRequest
import java.util.concurrent.CompletableFuture

interface ChatApiService {

    /**
     * [POST] /{{api_appid}}/user/users/{{userId}}
     * - https://apiref.sportstalk247.com/?version=latest#8b2eea78-82bc-4cae-9cfa-175a00a9e15b
     * - Creates a new chat room
     */
    fun createRoom(request: CreateRoomRequest): CompletableFuture<ApiResponse<ChatRoom>>

}