package com.sportstalk.datamodels.chat

import kotlinx.serialization.Serializable

@Serializable
data class ExecuteChatCommandRequest(
        val command: String,
        val userid: String,
        /**
         * (Optional) By default, the API will determine the type of event by processing your command.
         * However you can send custom commands.
         * - "custom"   This indicates you will be using a custom event type.
         * - "announcement"     This indicates the event is of type announcement.
         * - "ad"       Use this event type to push an advertisement. Use the CustomPayload property to specify parameters for your add.
         */
        val eventtype: String? = null,
        val customtype: String? = null,
        val customid: String? = null,
        val custompayload: String? = null,
        /** [ChatEvent.id] */
        val replyto: String? = null
)