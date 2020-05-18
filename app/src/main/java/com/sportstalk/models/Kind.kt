package com.sportstalk.models

object Kind {
    const val CHAT = "chat.event"
    const val ROOM = "chat.room"
    const val USER = "app.user"
    const val API = "api.result"
    const val WEBHOOK = "chat.webhook"
    const val WEBHOOK_LOGS = "list.webhook.logentries"
    const val WEBHOOK_COMMENT_PAYLOAD = "webhook.payload.comment"
    const val CHAT_COMMAND = "chat.executecommand"
    const val CONVERSATION = "comment.conversation"
    const val DELETED_CONVERSATION = "delete.conversation"
    const val COMMENT = "comment.comment"
    const val DELETED_COMMENT ="delete.comment"
    const val DELETED_ROOM = "deleted.room"
    const val DELETED_USER = "deleted.appuser"
    const val CONVERSATION_LIST = "list.commentconversations"
    const val CHAT_LIST = "list.chatevents"
    const val EVENT_LIST = "list.events"
    const val ROOM_LIST = "list.chatrooms"
    const val USER_LIST = "list.users"
}