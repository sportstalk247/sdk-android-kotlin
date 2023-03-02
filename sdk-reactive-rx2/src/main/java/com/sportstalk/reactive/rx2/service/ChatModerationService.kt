package com.sportstalk.reactive.rx2.service

import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.moderation.ListMessagesNeedingModerationResponse
import io.reactivex.Single

interface ChatModerationService {

    /**
     * [POST] /{{api_appid}}/chat/moderation/queues/events/{{eventId}}/applydecision
     * - https://apiref.sportstalk247.com/?version=latest#6f9bf714-5b3b-48c9-87d2-eb2e12d2bcbf
     * - APPROVES a message in the moderation queue.
     */
    fun approveMessage(eventId: String, approve: Boolean): Single<ChatEvent>

    /**
     * [POST] /{{api_appid}}/chat/moderation/queues/events
     * - https://apiref.sportstalk247.com/?version=latest#bcdbda1b-e495-46c9-8fe9-c5dc6a4c1756
     * - List all the messages in the moderation queue
     */
    fun listMessagesNeedingModeration(
        roomId: String? = null /* Filter to which specific roomId to fetch event(s) from */,
        limit: Int? = null /* Defaults to 200 on backend API server */,
        cursor: String? = null
    ): Single<ListMessagesNeedingModerationResponse>

}