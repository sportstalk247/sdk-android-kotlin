@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.sportstalk.coroutine.api.polling

import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.polling.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * Returns an instance of reactive coroutine Flow which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
 * OR if the underlying lifecycleOwner reaches STOP state.
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
): Flow<List<ChatEvent>> = flow<List<ChatEvent>> {
    do {
        // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
        if (roomSubscriptions().contains(chatRoomId)) {
            try {
                // Perform GET UPDATES operation
                val response = withContext(Dispatchers.IO) {
                    getUpdates(
                            chatRoomId = chatRoomId,
                            // Apply event cursor
                            cursor = getChatRoomEventUpdateCursor(chatRoomId)?.takeIf { it.isNotEmpty() }
                    )
                }

                // Emit response value
                response.cursor?.takeIf { it.isNotEmpty() }?.let { cursor ->
                    setChatRoomEventUpdateCursor(chatRoomId, cursor)
                } ?: run {
                    clearChatRoomEventUpdateCursor(chatRoomId)
                }
                emit(response.events)
            } catch (err: SportsTalkException) {
                err.printStackTrace()
            } catch (err: CancellationException) {
                err.printStackTrace()
            }
        } else {
            // ELSE, Either event updates has NOT yet started or `stopEventUpdates()` has been explicitly invoked
            break
        }

        delay(frequency)
    } while (true)
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
