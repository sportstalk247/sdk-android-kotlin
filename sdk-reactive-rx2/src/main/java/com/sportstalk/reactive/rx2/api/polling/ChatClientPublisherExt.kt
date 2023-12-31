package com.sportstalk.reactive.rx2.api.polling

import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.polling.*
import com.sportstalk.reactive.rx2.service.ChatService
import io.reactivex.Flowable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher
import java.util.concurrent.TimeUnit

/**
 * Returns an instance of reactive RxJava Publisher which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
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
): Flowable<List<ChatEvent>> {

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

    var getUpdatesInProgress = false

    return Flowable.merge(
            chatEventsEmitter,  // Execute Chat Command SPEECH event emitter
            Flowable.interval(0, frequency, TimeUnit.MILLISECONDS)
                    .switchMap {
                        // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
                        return@switchMap if(roomSubscriptions().contains(chatRoomId) && !getUpdatesInProgress) {
                            getUpdatesInProgress = true // Set fetch state in progress...
                            getUpdates(
                                    chatRoomId = chatRoomId,
                                    limit = limit,
                                    // Apply event cursor
                                    cursor = getChatRoomEventUpdateCursor(forRoomId = chatRoomId)?.takeIf { it.isNotEmpty() }
                            )
                                    .doOnSuccess { response ->
                                        getUpdatesInProgress = false     // Set fetch state DONE...

                                        // Update internally stored chatroom event cursor
                                        response.cursor?.takeIf { it.isNotEmpty() }?.let { cursor ->
                                            setChatRoomEventUpdateCursor(
                                                    forRoomId = chatRoomId,
                                                    cursor = cursor
                                            )
                                        } ?: run {
                                            clearChatRoomEventUpdateCursor(fromRoomId = chatRoomId)
                                        }
                                    }
                                    .toFlowable()
                        } else {
                            Flowable.empty()
                        }
                    }
                    .map {
                        it.events
                                .filterNot { ev ->
                                    // We already rendered this on send.
                                    val eventId = ev.id ?: ""
                                    val alreadyPreRendered = preRenderedMessages.contains(eventId)
                                    if(alreadyPreRendered) preRenderedMessages.remove(eventId)
                                    alreadyPreRendered
                                }
                    },
            /*
            * Upon start listen to event updates, dispatch call to Touch Session API every 60 seconds to keep user session alive.
            * Add a flow that does NOT EMIT anything, but will just continuously dispatch call to Touch Session API.
            */
            Flowable.interval(0, 60, TimeUnit.SECONDS)
                    .switchMap {
                        return@switchMap currentUser?.userid?.let { userid ->
                            touchSession(
                                    chatRoomId = chatRoomId,
                                    userId = userid
                            )
                                .map<List<ChatEvent>> { listOf() }
                                .toFlowable()
                        } ?: Flowable.empty()
                    }
    )
            .map { allEventUpdates ->
                allEventUpdates
                        // Filter out shadowban events for shadowbanned user
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
            }
            .flatMap { allEventUpdates ->
                // If smoothing is enabled, render events with some spacing.
                // However, if we have a massive batch, we want to catch up, so we do not put spacing and just jump ahead.
                if(smoothEventUpdates && allEventUpdates.isNotEmpty() && allEventUpdates.size < maxEventBufferSize) {
                    // Emit spaced event updates(i.e. emit per batch list of chat events)
                    Flowable.merge(
                            allEventUpdates
                                    .mapIndexed { index, ev ->
                                        val appliedDelay = index * delayEventSpacingMs
                                        Flowable.just(listOf(ev))
                                                // Apply Delay(eventSpacing) in between emits
                                                .delay(appliedDelay, TimeUnit.MILLISECONDS)
                                    }
                    )
                } else {
                    Flowable.just(allEventUpdates)
                }
            }
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
