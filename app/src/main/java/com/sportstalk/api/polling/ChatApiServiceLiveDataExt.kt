package com.sportstalk.api.polling

import androidx.lifecycle.*
import com.sportstalk.api.ChatApiService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.EventType
import com.sportstalk.models.chat.GetUpdatesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext

/**
    export enum EventType {
        speech = "speech",
        purge = "purge",
        reaction = "reaction",
        roomClosed = "roomclosed",
        roomOpen = "roomopen",
        action = "action",
        reply = "reply",
        goal = "goal", // custom type
        advertisement = "advertisement" // custom type
    }
 */

fun ChatApiService.allEventUpdatesLiveData(
        chatRoomId: String,
        eventTypeFilter: String? = null /** [EventType] */,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> = liveData {
    val emitter = MutableLiveData<ApiResponse<GetUpdatesResponse>>()

    val scope = lifecycleOwner.lifecycle.coroutineScope
    // This code block gets executed at a fixed rate, used from within [GetUpdatesObserver],
    val getUpdateAction = action@ {
        // Execute block from within coroutine scope
        scope.launchWhenStarted {
            try {
                // Perform GET UPDATES operation
                val response = withContext(Dispatchers.IO) {
                    getUpdates(chatRoomId = chatRoomId)
                            // Awaits for completion of the completion stage without blocking a thread
                            .await()
                }

                // Emit response value
                emitter.postValue(response)
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
                    chatApiService = this@allEventUpdatesLiveData,
                    getUpdateAction = getUpdateAction,
                    frequency = frequency
            )
    )

    // Emit Response
    emitSource(
            emitter.switchMap { response ->
                liveData<List<ChatEvent>> {
                    // Emit transform into List<ChatEvent>
                    emit(
                            response.data
                                    ?.events
                                    // Apply filter not-null provided
                                    ?.filter { _chatevent ->
                                        // Apply filter if
                                        return@filter if(eventTypeFilter != null && eventTypeFilter.isNotEmpty()) {
                                            _chatevent.eventtype == eventTypeFilter
                                        } else {
                                            true
                                        }
                                    }
                                    ?: listOf<ChatEvent>()
                    )
                }
            }
    )
}

//////////////////////////////
// Convenience Functions
//////////////////////////////

// "speech" Event Updates
fun ChatApiService.speechEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.SPEECH,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "purge" Event Updates
fun ChatApiService.purgeEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.PURGE,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "reaction" Event Updates
fun ChatApiService.reactionEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.REACTION,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "roomClosed" Event Updates
fun ChatApiService.roomClosedEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ROOM_CLOSED,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "roomopen" Event Updates
fun ChatApiService.roomOpenEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ROOM_OPEN,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "action" Event Updates
fun ChatApiService.actionEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ACTION,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "reply" Event Updates
fun ChatApiService.replyEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.REPLY,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "goal" Event Updates
fun ChatApiService.goalEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.GOAL,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )

// "advertisement" Event Updates
fun ChatApiService.advertisementEventsLiveData(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner,
        /* Polling Frequency */
        frequency: Long = 500L
): LiveData<List<ChatEvent>> =
        allEventUpdatesLiveData(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ADVERTISEMENT,
                lifecycleOwner = lifecycleOwner,
                frequency = frequency
        )
