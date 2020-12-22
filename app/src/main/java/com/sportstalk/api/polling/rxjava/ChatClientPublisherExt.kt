package com.sportstalk.api.polling.rxjava

import com.sportstalk.api.ChatClient
import com.sportstalk.api.polling.*
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.GetUpdatesResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.coroutines.*

/**
 * Returns an instance of reactive RxJava Publisher which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
 * OR if the underlying lifecycleOwner reaches STOP state.
 */
fun ChatClient.allEventUpdates(
        chatRoomId: String,
        coroutineScope: CoroutineScope,
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
    return Flowable.create<GetUpdatesResponse>({ emitter ->
        coroutineScope.launch {
            do {
                // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
                if (roomSubscriptions.contains(chatRoomId)) {
                    try {
                        // Perform GET UPDATES operation
                        val response = kotlinx.coroutines.withContext(Dispatchers.IO) {
                            getUpdates(
                                    chatRoomId = chatRoomId,
                                    // Apply event cursor
                                    cursor = chatRoomEventCursor[chatRoomId]?.takeIf { it.isNotEmpty() }
                            )
                        }

                        // Emit response value
                        emitter.onNext(response)
                    } catch (err: SportsTalkException) {
                        err.printStackTrace()
                        emitter.onError(err)
                    }
                } else {
                    // ELSE, Either event updates has NOT yet started or `stopEventUpdates()` has been explicitly invoked
                    break
                }

                delay(frequency)
            } while (true)
        }

    }, BackpressureStrategy.LATEST)
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
