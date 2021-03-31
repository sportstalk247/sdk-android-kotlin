package com.sportstalk.reactive.rx2.api.polling

import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.EventType
import com.sportstalk.datamodels.chat.polling.*
import com.sportstalk.reactive.rx2.service.ChatService
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

/**
 * Returns an instance of reactive RxJava Publisher which emits Event Updates received at
 * a certain frequency. This will stop emitting when `chatClient.stopEventUpdates()` has been invoked
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
        eventSpacingMs: Long = 100L,
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

    val delayEventSpacingMs = when {
        eventSpacingMs >= 0 -> eventSpacingMs
        else -> 100L
    }

    return Flowable.merge(
            chatEventsEmitter,  // Execute Chat Command SPEECH event emitter
            Flowable.interval(0, frequency, TimeUnit.MILLISECONDS)
                    .switchMap {
                        // Attempt operation call ONLY IF `startListeningToChatUpdates(roomId)` is called.
                        return@switchMap if(roomSubscriptions().contains(chatRoomId)) {
                            getUpdates(
                                    chatRoomId = chatRoomId,
                                    limit = limit,
                                    // Apply event cursor
                                    cursor = getChatRoomEventUpdateCursor(forRoomId = chatRoomId)?.takeIf { it.isNotEmpty() }
                            )
                                    .doOnSuccess { response ->
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
                    val batchListFlowable = allEventUpdates
                            .asSequence()
                            .mapIndexed { index, chatEvent ->
                        Flowable.just(
                                // Emit each Chat Event Items
                                listOf(chatEvent)
                        )
                    }
                            .asIterable()

                    Flowable.merge(batchListFlowable)
                            // Apply Delay(eventSpacing) in between emits
                            .delay(delayEventSpacingMs, TimeUnit.MILLISECONDS)
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
