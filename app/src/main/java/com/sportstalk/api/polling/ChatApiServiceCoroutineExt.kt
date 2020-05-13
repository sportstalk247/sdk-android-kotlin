@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.sportstalk.api.polling

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.sportstalk.api.ChatApiService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.EventType
import com.sportstalk.models.chat.GetUpdatesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.future.await

fun ChatApiService.allEventUpdatesFlow(
        chatRoomId: String,
        eventTypeFilter: String? = null /* [EventType] */,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> = flow {
    val emitter = ConflatedBroadcastChannel<ApiResponse<GetUpdatesResponse>>()

    val scope = lifecycleOwner.lifecycle.coroutineScope
    // This code block gets executed at a fixed rate, used from within [GetUpdatesObserver],
    val getUpdateAction = action@{
        // Execute block from within coroutine scope
        scope.launchWhenStarted {
            try {
                // Perform GET UPDATES operation
                val response = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    getUpdates(chatRoomId = chatRoomId)
                            // Awaits for completion of the completion stage without blocking a thread
                            .await()
                }

                // Emit response value
                emitter.send(response)
            } catch (err: Throwable) {
                err.printStackTrace()
            }
        }

        return@action
    }

    /**
     * Add GetUpdates Lifecycle Observer Implementation to this lifecycle owner's set of observers
     */
    lifecycleOwner.lifecycle.addObserver(
            GetUpdatesObserver(
                    chatApiService = this@allEventUpdatesFlow,
                    getUpdateAction = getUpdateAction,
                    frequency = frequency
            )
    )

    // Emit Response
    emitAll(
            emitter.asFlow()
                    .map { response ->
                        response.data
                                ?.events
                                // Apply filter not-null provided
                                ?.filter { _chatevent ->
                                    // Apply filter if
                                    return@filter if (eventTypeFilter != null && eventTypeFilter.isNotEmpty()) {
                                        _chatevent.eventtype == eventTypeFilter
                                    } else {
                                        true
                                    }
                                }
                                ?: listOf()
                    }
    )
}

//////////////////////////////
// Convenience Functions
//////////////////////////////

// "speech" Event Updates
fun ChatApiService.speechEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.SPEECH,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "purge" Event Updates
fun ChatApiService.purgeEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.PURGE,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "reaction" Event Updates
fun ChatApiService.reactionEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.REACTION,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "roomClosed" Event Updates
fun ChatApiService.roomClosedEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ROOM_CLOSED,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "roomopen" Event Updates
fun ChatApiService.roomOpenEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ROOM_OPEN,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "action" Event Updates
fun ChatApiService.actionEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ACTION,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "reply" Event Updates
fun ChatApiService.replyEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.REPLY,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "goal" Event Updates
fun ChatApiService.goalEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.GOAL,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "advertisement" Event Updates
fun ChatApiService.advertisementEventsFlow(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): Flow<List<ChatEvent>> =
        allEventUpdatesFlow(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ADVERTISEMENT,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

