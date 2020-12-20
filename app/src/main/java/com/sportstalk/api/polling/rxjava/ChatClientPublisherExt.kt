package com.sportstalk.api.polling.rxjava

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.sportstalk.api.ChatClient
import com.sportstalk.api.polling.*
import com.sportstalk.models.SportsTalkException
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.EventType
import com.sportstalk.models.chat.GetUpdatesResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers

/**
 * Returns an instance of reactive RxJava Publisher which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
 * OR if the underlying lifecycleOwner reaches STOP state.
 */
fun ChatClient.allEventUpdates(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
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
    /*LiveDataReactiveStreams.toPublisher(
                lifecycleOwner,
                allEventUpdatesLiveData(chatRoomId, eventTypeFilter, lifecycleOwner)
        )*/
    return Flowable.create<GetUpdatesResponse>({ emitter ->
        val scope = lifecycleOwner.lifecycle.coroutineScope
        // This code block gets executed at a fixed rate, used from within [GetUpdatesObserver],
        val getUpdateAction = Runnable {
            try {
                // Execute block from within coroutine scope
                scope.launchWhenCreated {
                    try {
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
                            }
                        }
                        // ELSE, Either event updates has NOT yet started or `stopEventUpdates()` has been explicitly invoked
                    } catch (err: SportsTalkException) {
                        emitter.onError(err)
                    }
                }
            } catch (err: CancellationException) {
                err.printStackTrace()
            }
        }

        /**
         * Add GetUpdates Lifecycle Observer Implementation to this lifecycle owner's set of observers
         */
        lifecycleOwner.lifecycle.addObserver(
                GetUpdatesObserver(
                        getUpdateAction = getUpdateAction,
                        frequency = frequency
                )
        )

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
