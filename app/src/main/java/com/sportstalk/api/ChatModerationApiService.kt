package com.sportstalk.api

import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import java.util.concurrent.CompletableFuture

interface ChatModerationApiService {

    /**
     * [POST] /{{api_appid}}/chat/moderation/queues/events/{{eventId}}/applydecision
     * - https://apiref.sportstalk247.com/?version=latest#6f9bf714-5b3b-48c9-87d2-eb2e12d2bcbf
     * - APPROVES a message in the moderation queue.
     */
    fun approveMessage(eventId: String, approve: Boolean): CompletableFuture<ApiResponse<ChatEvent>>

}