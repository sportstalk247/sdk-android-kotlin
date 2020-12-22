package com.sportstalk.reactive.api.polling

import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.polling.*
import com.sportstalk.reactive.service.ChatService
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

/**
 * Returns an instance of reactive RxJava Publisher which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
 */
fun ChatService.allEventUpdates(
        chatRoomId: String,
        /* Polling Frequency */
        frequency: Long = 500L,
        /*
        * The following are placeholder/convenience functions should they opt to provide custom callbacks
        */
        onChatEvent: OnChatEvent? = null,
        onGoalEvent: OnGoalEvent? = null,
        onAdEvent: OnAdEvent? = null,
        onReply: OnReply? = null,
        onReaction: OnReaction? = null,
        onPurgeEvent: OnPurgeEvent? = null
): Flowable<List<ChatEvent>> {
    return Flowable.interval(0, frequency, TimeUnit.MILLISECONDS)
            // Proceed EMIT ONLY If still currently subscribed with the `chatRoomId`
            .filter { roomSubscriptions.contains(chatRoomId) }
            .switchMap {
                getUpdates(
                        chatRoomId = chatRoomId,
                        // Apply event cursor
                        cursor = chatRoomEventCursor[chatRoomId]?.takeIf { it.isNotEmpty() }
                )
                        .toFlowable()
            }
            .doOnNext { response ->
                // Update internally stored chatroom event cursor
                response.cursor?.takeIf { it.isNotEmpty() }?.let { cursor ->
                    chatRoomEventCursor[chatRoomId] = cursor
                }
            }
            .map { it.events }
            .doOnNext { events ->
                events.forEach { chatEvent ->
                    when (chatEvent.eventtype) {
                        EventType.GOAL -> onGoalEvent?.invoke(chatEvent)
                        EventType.ADVERTISEMENT -> onAdEvent?.invoke(chatEvent)
                        EventType.REPLY -> onReply?.invoke(chatEvent)
                        EventType.REACTION -> onReaction?.invoke(chatEvent)
                        EventType.PURGE -> onPurgeEvent?.invoke(chatEvent)
                        else -> onChatEvent?.invoke(chatEvent)
                    }
                }
            }
}
