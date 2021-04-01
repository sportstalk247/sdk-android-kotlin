package com.sportstalk.datamodels.chat

enum class RoomDetailEntityType(val keyword: String) {
    ROOM("room"),
    NUM_PARTICIPANTS("numparticipants"),
    LAST_MESSAGE_TIME("lastmessagetime")
}