@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.sportstalk.api.polling.coroutines

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.polling.*
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.EventType
import com.sportstalk.models.chat.GetUpdatesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await

fun ChatService.allEventUpdates(
        chatRoomId: String,
        /* Polling Frequency */
        frequency: Long = 500L,
        lifecycleOwner: LifecycleOwner,
        /*
        * The following are placeholder functions should they opt to provide custom callbacks
        */
        onChatEvent: OnChatEvent? = null,
        onGoalEvent: OnGoalEvent? = null,
        onAdEvent: OnAdEvent? = null,
        onReply: OnReply? = null,
        onReaction: OnReaction? = null,
        onPurgeEvent: OnPurgeEvent? = null
): Flow<List<ChatEvent>> = flow<List<ChatEvent>> {
    val emitter = ConflatedBroadcastChannel<ApiResponse<GetUpdatesResponse>>()
    val lastEventTs = ConflatedBroadcastChannel<Long>(-1L)

    val scope = lifecycleOwner.lifecycle.coroutineScope
    // This code block gets executed at a fixed rate, used from within [GetUpdatesObserver],
    val getUpdateAction = Runnable {
        // Execute block from within coroutine scope
        scope.launchWhenStarted {
            try {
                // Attempt operation call ONLY IF `startEventUpdates(roomId)` is called.
                if (roomSubscriptions.contains(chatRoomId)) {
                    // Perform GET UPDATES operation
                    val response = kotlinx.coroutines.withContext(Dispatchers.IO) {
                        getUpdates(chatRoomId = chatRoomId)
                                // Awaits for completion of the completion stage without blocking a thread
                                .await()
                    }

                    // Emit response value
                    emitter.send(response)
                }
                // ELSE, Either event updates has NOT yet started or `stopEventUpdates()` has been explicitly invoked
            } catch (err: Throwable) {
                err.printStackTrace()
            }
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

    // Emit Response
    emitAll(
            emitter.asFlow()
                    .map { response ->
                        (response.data
                                ?.events
                                // Filter out redundant events that were already emitted prior
                                ?.filter { event ->
                                    (event.ts ?: 0L) > lastEventTs.value
                                }
                                ?: listOf()
                                )
                                .also { events ->
                                    // Update lastEventTs with the latest ts
                                    lastEventTs.sendBlocking(events.maxBy { it.ts ?: 0L }?.ts ?: 0L)
                                }
                    }
    )

}
        // Trigger callbacks based on event type
        .onEach { events ->
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
