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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.withContext
import kotlin.collections.filterNot

/**
 * Returns an instance of reactive coroutine Flow which emits Event Updates received at
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
): Flow<List<ChatEvent>> =
        merge(
            chatEventsEmitter,  // Execute Chat Command SPEECH event emitter
            flow<List<ChatEvent>> {
                do {
                    // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
                    if (roomSubscriptions().contains(chatRoomId)) {
                        try {
                            // Perform GET UPDATES operation
                            val response = withContext(Dispatchers.IO) {
                                getUpdates(
                                        chatRoomId = chatRoomId,
                                        limit = limit,
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

                            val allEventUpdates = response.events
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
                                var startIndex = 0
                                // Each batch will contain atmost N items
                                val stepSize = 1
                                do {
                                    val batchList = allEventUpdates.subList(
                                            startIndex,
                                            if((startIndex + stepSize) <= allEventUpdates.size) {
                                                stepSize
                                            } else allEventUpdates.size
                                    )
                                    // Emit Batch of Chat Event Items
                                    emit(batchList)

                                    // Apply spaced delay for each batch list being emitted
                                    delay(eventSpacingMs)

                                    startIndex += stepSize
                                } while(startIndex < allEventUpdates.size)
                            } else {
                                // Just emit all events as-is
                                emit(allEventUpdates)
                            }
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
                    // Skip pre-rendered messages
                    .map { events ->
                        events
                                .filterNot { ev ->
                                    val isPreRendered = preRenderedMessages.contains(ev.id!!)
                                    if(isPreRendered) preRenderedMessages.remove(ev.id!!)
                                    isPreRendered
                                }
                    }
        )
            // Filter out shadowban events for shadowbanned user
            .map { events ->
                events
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
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

fun <T> merge(vararg flows: Flow<T>): Flow<T> =
        flowOf(*flows).flattenMerge(concurrency = flows.size)