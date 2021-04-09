package com.sportstalk.coroutine.api.polling.livedata

import androidx.lifecycle.*
import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.GetUpdatesResponse
import com.sportstalk.datamodels.chat.polling.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map

/**
 * Returns an instance of reactive LiveData which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
 * OR if the underlying lifecycleOwner reaches STOP state.
 */
fun ChatService.allEventUpdates(
        chatRoomId: String,
        /* Polling Frequency */
        frequency: Long = 500L,
        limit: Int? = null, // (optional) Number of events to return for each poll. Default is 100, maximum is 500.
        /**
         * If [true], render events with some spacing.
         * - However, if we have a massive batch, we want to catch up, so we do not put spacing and just jump ahead.
         */
        smoothEventUpdates: Boolean = true,
        /**
         * (optional, 100ms by default) This only applies if `smoothEventUpdates` = true.
         * This defines how long to pause before emitting the next event in a batch.
         */
        eventSpacingMs: Long = 200L,
        /**
         * (optional, 30 by default) This only applies if `smoothEventUpdates` = true.
         * Holds the size of the event buffer we will accept before displaying everything in order to catch up.
         */
        maxEventBufferSize: Int = 30,
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

    val delayEventSpacingMs = when {
        eventSpacingMs >= 0 -> eventSpacingMs
        else -> 100L
    }

    // Emit Response
    emitSource(
            mergeLiveData(
                chatEventsEmitter.asLiveData(),  // Execute Chat Command SPEECH event emitter
                emitter.switchMap { response ->
                    liveData<List<ChatEvent>> {
                        // Update internally stored chatroom event cursor
                        response.cursor?.takeIf { it.isNotEmpty() }?.let { cursor ->
                            setChatRoomEventUpdateCursor(chatRoomId, cursor)
                        }

                        emit(response.events)
                    }

                }
            )
                    .switchMap { events ->
                        liveData<List<ChatEvent>> {
                            // Emit transform into List<ChatEvent>
                            val allEventUpdates = events
                                    // Filter out shadowban events for shadowbanned user
                                    .filterNot { ev ->
                                        ev.shadowban == true && ev.userid != currentUser?.userid
                                    }
                                    .filterNot { ev ->
                                        // We already rendered this on send.
                                        val eventId = ev.id ?: ""
                                        val alreadyPreRendered = preRenderedMessages.contains(eventId)
                                        if(alreadyPreRendered) preRenderedMessages.remove(eventId)
                                        alreadyPreRendered
                                    }

                            // If smoothing is enabled, render events with some spacing.
                            // However, if we have a massive batch, we want to catch up, so we do not put spacing and just jump ahead.
                            if(smoothEventUpdates && allEventUpdates.size < maxEventBufferSize) {
                                // Emit spaced event updates(i.e. emit per batch list of chat events)
                                for(chatEvent in allEventUpdates) {
                                    // Emit each Chat Event Items
                                    emit(listOf(chatEvent))
                                    // Apply spaced delay for each chat event item being emitted
                                    delay(delayEventSpacingMs)
                                }
                            } else {
                                // Just emit all events as-is
                                emit(allEventUpdates)
                            }
                        }
                    }
                    .map { events ->
                        events.apply {
                            // Trigger callbacks based on event type
                            forEach { chatEvent ->
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

    do {
        // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
        if (roomSubscriptions().contains(chatRoomId)) {
            try {
                // Perform GET UPDATES operation
                val response = kotlinx.coroutines.withContext(Dispatchers.IO) {
                    getUpdates(
                            chatRoomId = chatRoomId,
                            limit = limit,
                            // Apply event cursor
                            cursor = getChatRoomEventUpdateCursor(chatRoomId)?.takeIf { it.isNotEmpty() }
                    )
                }

                // Emit response value
                emitter.value = response
            } catch (err: SportsTalkException) {
                err.printStackTrace()
            }
        } else {
            // ELSE, Either event updates has NOT yet started or `stopEventUpdates()` has been explicitly invoked
            break
        }

        delay(frequency)
    } while (true)
}

internal class MergeLiveData<T> internal constructor(
        source1: LiveData<T>,
        source2: LiveData<T>
) : MediatorLiveData<T>() {

    private var data1: T? = null
    private var data2: T? = null

    init {
        super.addSource(source1) {
            data1 = it
            value = data1!!
        }
        super.addSource(source2) {
            data2 = it
            value = data2!!
        }
    }
}

internal fun <T> mergeLiveData(source1: LiveData<T>, source2: LiveData<T>): LiveData<T> =
        MergeLiveData(source1, source2)
