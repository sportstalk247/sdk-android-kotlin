@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.sportstalk.coroutine.api.polling

import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.polling.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.collections.filterNot

/**
 * Returns an instance of reactive coroutine Flow which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
 * OR if the underlying lifecycleOwner reaches STOP state.
 */
fun ChatService.allEventUpdates(
        chatRoomId: String,
        /*
         * Polling Frequency
         * - If provided value is below 1000ms, throw a SportstalkException to indicate that frequency must be equal to or greater than 1000ms.
         */
        frequency: Long = 1000L,
        limit: Int? = null, // (optional) Number of events to return for each poll. Default is 100, maximum is 500.
        /**
         * If [true], render events with some spacing.
         * - However, if we have a massive batch, we want to catch up, so we do not put spacing and just jump ahead.
         */
        smoothEventUpdates: Boolean = true,
        /**
         * (optional, 200ms by default) This only applies if `smoothEventUpdates` = true.
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
): Flow<List<ChatEvent>> {

    // Frequency check
    if(frequency < 1000L) {
        throw SportsTalkException(
                code = 500,
                err = kotlin.IllegalArgumentException("Frequency must be equal to or greater than 1000ms.")
        )
    }

    // Insanity check, event spacing delay must have a valid value.
    val delayEventSpacingMs = when {
        eventSpacingMs >= 0 -> eventSpacingMs
        else -> 100L
    }

    return merge(
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

                            emit(allEventUpdates)
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
            },
            /*
            * Upon start listen to event updates, dispatch call to Touch Session API every 60 seconds to keep user session alive.
            * Add a flow that does NOT EMIT anything, but will just continuously dispatch call to Touch Session API.
            */
            callbackFlow<List<ChatEvent>> {
                do {
                    try {
                        this.ensureActive()
                        currentUser?.userid?.let { userid ->
                            if(this.isActive) {
                                withContext(Dispatchers.IO) {
                                    this.coroutineContext.ensureActive()
                                    if(this.coroutineContext.isActive) {
                                        touchSession(
                                                chatRoomId = chatRoomId,
                                                userId = userid
                                        )
                                    }
                                }
                            }
                        }
                    }
                    catch (err: CancellationException) { err.printStackTrace() }
                    catch (err: Throwable) { err.printStackTrace() }
                    finally {
                        delay(60_000L)
                    }
                } while(true)
            }
    )
            // Filter out shadowban events for shadowbanned user
            .map { events ->
                events
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
            }
            .flatMapMerge { allEventUpdates ->
                // If smoothing is enabled, render events with some spacing.
                // However, if we have a massive batch, we want to catch up, so we do not put spacing and just jump ahead.
                if(smoothEventUpdates && allEventUpdates.isNotEmpty() && allEventUpdates.size < maxEventBufferSize) {

                    // Emit spaced event updates(i.e. emit per batch list of chat events)
                    flow<List<ChatEvent>> {
                        for(chatEvent in allEventUpdates) {
                            // Emit each Chat Event Items
                            emit(listOf(chatEvent))
                            // Apply spaced delay for each chat event item being emitted
                            delay(delayEventSpacingMs)
                        }
                    }
                } else {
                    // Just emit all events as-is
                    flowOf(allEventUpdates)
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
}

fun <T> merge(vararg flows: Flow<T>): Flow<T> =
        flowOf(*flows).flattenMerge(concurrency = flows.size)