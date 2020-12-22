package com.sportstalk.datamodels.chat.polling

import com.sportstalk.datamodels.chat.ChatEvent

typealias OnChatEvent = ((chatEvent: ChatEvent) -> Unit)
typealias OnGoalEvent = ((chatEvent: ChatEvent /* eventtype = EventType.GOAL */ ) -> Unit)
typealias OnAdEvent = ((chatEvent: ChatEvent /* eventtype = EventType.ADVERTISEMENT */ ) -> Unit)
typealias OnReply = ((chatEvent: ChatEvent /* eventtype = EventType.REPLY */ ) -> Unit)
typealias OnReaction = ((chatEvent: ChatEvent /* eventtype = EventType.REACTION */ ) -> Unit)
typealias OnPurgeEvent = ((chatEvent: ChatEvent /* eventtype = EventType.PURGE */ ) -> Unit)
