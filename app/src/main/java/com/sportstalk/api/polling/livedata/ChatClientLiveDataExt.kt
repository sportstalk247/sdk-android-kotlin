package com.sportstalk.api.polling.livedata

import androidx.lifecycle.*
import com.sportstalk.api.ChatClient
import com.sportstalk.api.polling.*
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatEvent
import com.sportstalk.models.chat.EventType
import com.sportstalk.models.chat.GetUpdatesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext

/**
 * Returns an instance of reactive LiveData which emits Event Updates received at
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
): LiveData<List<ChatEvent>> = liveData<List<ChatEvent>> {
    val emitter = MutableLiveData<GetUpdatesResponse>()

    val scope = lifecycleOwner.lifecycle.coroutineScope
    // This code block gets executed at a fixed rate, used from within [GetUpdatesObserver],
    val getUpdateAction = Runnable {
        // Execute block from within coroutine scope
        scope.launchWhenStarted {
            // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
            if (roomSubscriptions.contains(chatRoomId)) {
                // Perform GET UPDATES operation
                val response = withContext(Dispatchers.IO) {
                    getUpdates(
                            chatRoomId = chatRoomId,
                            // Apply event cursor
                            cursor = chatRoomEventCursor[chatRoomId]?.takeIf { it.isNotEmpty() }
                    )
                }

                // Emit response value
                emitter.postValue(response)
            }
            // ELSE, Either event updates has NOT yet started or `stopEventUpdates()` has been explicitly invoked
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
    emitSource(
            emitter.switchMap { response ->
                liveData<List<ChatEvent>> {
                    // Update internally stored chatroom event cursor
                    response.cursor?.takeIf { it.isNotEmpty() }?.let { cursor ->
                        chatRoomEventCursor[chatRoomId] = cursor
                    }

                    // Emit transform into List<ChatEvent>
                    val events = response.events
                    emit(events)

                    // Trigger callbacks based on event type
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
    )
}