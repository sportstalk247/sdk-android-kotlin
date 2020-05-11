package com.sportstalk.api.polling

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope
import com.sportstalk.api.ChatApiService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.EventType
import com.sportstalk.models.chat.GetUpdatesResponse
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import org.reactivestreams.Publisher

fun ChatApiService.allEventUpdatesPublisher(
        chatRoomId: String,
        eventTypeFilter: String? = null /* [EventType] */,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        /*LiveDataReactiveStreams.toPublisher(
                lifecycleOwner,
                allEventUpdatesLiveData(chatRoomId, eventTypeFilter, lifecycleOwner)
        )*/
        Flowable.create<ApiResponse<GetUpdatesResponse>>({ emitter ->

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
                        emitter.onNext(response)
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
                            chatApiService = this@allEventUpdatesPublisher,
                            getUpdateAction = getUpdateAction
                    )
            )

        }, BackpressureStrategy.LATEST)
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

//////////////////////////////
// Convenience Functions
//////////////////////////////

// "speech" Event Updates
fun ChatApiService.speechEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.SPEECH,
                lifecycleOwner = lifecycleOwner
        )

// "purge" Event Updates
fun ChatApiService.purgeEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.PURGE,
                lifecycleOwner = lifecycleOwner
        )

// "reaction" Event Updates
fun ChatApiService.reactionEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.REACTION,
                lifecycleOwner = lifecycleOwner
        )

// "roomClosed" Event Updates
fun ChatApiService.roomClosedEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ROOM_CLOSED,
                lifecycleOwner = lifecycleOwner
        )

// "roomopen" Event Updates
fun ChatApiService.roomOpenEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ROOM_OPEN,
                lifecycleOwner = lifecycleOwner
        )

// "action" Event Updates
fun ChatApiService.actionEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ACTION,
                lifecycleOwner = lifecycleOwner
        )

// "reply" Event Updates
fun ChatApiService.replyEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.REPLY,
                lifecycleOwner = lifecycleOwner
        )

// "goal" Event Updates
fun ChatApiService.goalEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.GOAL,
                lifecycleOwner = lifecycleOwner
        )

// "advertisement" Event Updates
fun ChatApiService.advertisementEventsPublisher(
        chatRoomId: String,
        lifecycleOwner: LifecycleOwner
): Publisher<List<ChatEvent>> =
        allEventUpdatesPublisher(
                chatRoomId = chatRoomId,
                eventTypeFilter = EventType.ADVERTISEMENT,
                lifecycleOwner = lifecycleOwner
        )

