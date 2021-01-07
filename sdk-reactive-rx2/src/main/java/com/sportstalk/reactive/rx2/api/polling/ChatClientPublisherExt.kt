package com.sportstalk.reactive.rx2.api.polling

import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.polling.*
import com.sportstalk.reactive.rx2.service.ChatService
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
            .switchMap {
                // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
                return@switchMap if(roomSubscriptions().contains(chatRoomId)) {
                    getUpdates(
                            chatRoomId = chatRoomId,
                            // Apply event cursor
                            cursor = getChatRoomEventUpdateCursor(forRoomId = chatRoomId)?.takeIf { it.isNotEmpty() }
                    )
                            .doOnSuccess { response ->
                                // Update internally stored chatroom event cursor
                                response.cursor?.takeIf { it.isNotEmpty() }?.let { cursor ->
                                    setChatRoomEventUpdateCursor(
                                            forRoomId = chatRoomId,
                                            cursor = cursor
                                    )
                                } ?: run {
                                    clearChatRoomEventUpdateCursor(fromRoomId = chatRoomId)
                                }
                            }
                            .toFlowable()
                } else {
                    Flowable.empty()
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
