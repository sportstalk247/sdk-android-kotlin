package com.sportstalk.api.polling

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.sportstalk.api.ChatApiService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Lifecycle Observer Implementation that initiates ExecutorService to perform scheduled action
 * at a fixed rate when [Lifecycle.Event.ON_START] state already reached. Also, automatically shuts down
 * when reaching [Lifecycle.Event.ON_STOP] state.
 */
class GetUpdatesObserver(
        private val chatApiService: ChatApiService,
        private val getUpdateAction: (() -> Unit),
        /* Polling Frequency */
        private val frequency: Long = 500L
) : LifecycleObserver {

    private lateinit var scheduler: ScheduledExecutorService
    private lateinit var schedule: ScheduledFuture<*>

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onSubscribeStart() {
        scheduler = Executors.newScheduledThreadPool(1)
        schedule = scheduler.scheduleAtFixedRate(
                { getUpdateAction() },
                0L,
                frequency,
                TimeUnit.MILLISECONDS
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onUnsbuscribeStop() {
        schedule.cancel(true)
        scheduler.shutdownNow()
    }


}