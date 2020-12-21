# Chat Client

## Create Room

Invoke this function to create a new chat room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#8b2eea78-82bc-4cae-9cfa-175a00a9e15b>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val createdRoom = withContext(Dispatchers.IO) {
        chatClient.createRoom(
            request = CreateChatRoomRequest(
                name = "Test Chat Room 1",
                customid = "test-room-1",
                description = "This is a test chat room 1.",
                moderation = "post",
                enableactions = true,
                enableenterandexit = true,
                enableprofanityfilter = false,
                delaymessageseconds = 0L,
                roomisopen = true,
                maxreports = 0
            )
        )
    }

    // Resolve `createdRoom` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Get Room Details

Invoke this function to get the details for a room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#9bac9724-7505-4e3e-966f-08cfebbca88d>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val chatRoom = withContext(Dispatchers.IO) {
        chatClient.getRoomDetails(
            chatRoomId = "080001297623242ac002"
        )
    }

    // Resolve `chatRoom` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Get Room Details By CustomId

Invoke this function to get the details for a room, using custom ID.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#0fd07be5-f8d5-43d9-bf0f-8fb9829c172c>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val chatRoom = withContext(Dispatchers.IO) {
        chatClient.getRoomDetailsByCustomId(
            chatRoomCustomId = "custom-id-0239760802"
        )
    }

    // Resolve `chatRoom` from HERE onwards(ex. update UI displaying the response data)...
}

```

## List Rooms

Invoke this function to list all the available public chat rooms.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#0580f06e-a58e-447a-aa1c-6071f3cfe1cf>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val listRooms = withContext(Dispatchers.IO) {
        chatClient.listRooms(
            limit = 20, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of chat room(s).
        )
    }

    // Resolve `chatRoom` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Join Room (Authenticated User)

Invoke this function to join a room.

You want your chat experience to open fast. The steps to opening a chat experience are:

1. Create Room
2. Create User
3. Join Room (user gets permission to access events data from the room)
4. Get Recent Events to display in your app

If you have already created the room (step 1) then you can perform steps 2 - 4 using join room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#eb3f78c3-a8bb-4390-ab25-77ce7072ddda>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val joinRoomResponse = withContext(Dispatchers.IO) {
        chatClient.joinRoom(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = JoinChatRoomRequest(
                userid = "023976080242ac120002" // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `joinRoomResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Join Room By CustomID

Invoke this function to join a room by Custom ID. This method is the same as Join Room, except you can use your customid.

The benefit of this method is you don't need to query to get the roomid using customid, and then make another call to join the room. This eliminates a request and enables you to bring your chat experience to your user faster.

You want your chat experience to open fast. The steps to opening a chat experience are:

1. Create Room
2. Create User
3. Join Room (user gets permission to access events data from the room)
4. Get Recent Events to display in your app

If you have already created the room (step 1) then you can perform steps 2 - 4 using join room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#a64f2c32-6167-4639-9c32-413edded2c18>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val joinRoomResponse = withContext(Dispatchers.IO) {
        chatClient.joinRoomByCustomId(
            chatRoomCustomId = "custom-room-id-12976",    // Custom ID of an existing chat room
            request = JoinChatRoomRequest(
                userid = "023976080242ac120002" // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `joinRoomResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Join Room (Anonymous User)

Invoke this function to join a room as an anonymous user.

A user can be added to a room in a logged in state or in an anonymous state. Typically the anonymous state is used so that people can see what is happening in the room and be enticed to register with you in order to participate in the conversation, as they must be logged in to say something or react to anything happening in the room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#c83c1afc-300b-4a18-b7e2-e3a1797dbca3>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val joinRoomResponse = withContext(Dispatchers.IO) {
        chatClient.joinRoom(
            chatRoomIdOrLabel = "080001297623242ac002"    // ID of an existing chat room
        )
    }

    // Resolve `joinRoomResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## List Room Participants

Invoke this function to list all the participants in the specified room.

Use this method to cursor through the people who have subscribe to the room.

To cursor through the results if there are many participants, invoke this function many times. Each result will return a cursor value and you can pass that value to the next invokation to get the next page of results. The result set will also include a next field with the full URL to get the next page, so you can just keep reading that and requesting that URL until you reach the end. When you reach the end, no more results will be returned or the result set will be less than maxresults and the next field will be empty.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#1b1b82a9-2b2f-4785-993b-baed6e7eba7b>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val listRoomParticipants = withContext(Dispatchers.IO) {
        chatClient.listRoomParticipants(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            limit = 20, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of chatroom participant(s).
        )
    }

    // Resolve `listRoomParticipants` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Update Room

Invoke this function to update an existing room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#96ef3138-4820-459b-b400-e9f25d5ddb00>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val updatedRoom = withContext(Dispatchers.IO) {
        chatClient.updateRoom(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = UpdateChatRoomRequest(
                name = "${testData.name!!}-updated",
                customid = "${testData.customid}-updated(${System.currentTimeMillis()})",
                description = "${testData.description}-updated",
                enableactions = !testData.enableactions!!,
                enableenterandexit = !testData.enableenterandexit!!,
                maxreports = 30L
            )
        )
    }

    // Resolve `updatedRoom` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Execute Chat Command (say 'Hello, World!')

Invoke this function to execute a command in a chat room.

**Precondition**: The user must JOIN the room first with a call to Join Room. Otherwise you'll receive HTTP Status Code PreconditionFailed (412)

### SENDING A MESSAGE

* Send any text that doesn't start with a reserved symbol to perform a SAY command.
* Use this function to REPLY to existing messages
* Use this function to perform ACTION commands
* Use this function to perform ADMIN commands

*example:*

```kotlin
ExecuteChatCommandRequest(
    command = "These commands both do the same thing, which is send the message 'Hello World' to the room. SAY Hello, World Hello, World",
    // ....
)
```

### ACTION COMMANDS

* Action commands start with the `/` character

*example:*

```kotlin
// Assuming current user's handle is "@MikeHandle05"
ExecuteChatCommandRequest(
    command = "/dance nicole",
    // ....
)

// User sees: "You dance with Nicole"
// Nicole sees: "@MikeHandle05 dances with you"
// Everyone else sees: "@MikeHandle05 dances with Nicole"
```

This requires that the action command dance is on the approved list of commands and Nicole is the handle of a participant in the room, and that actions are allowed in the room.

### ADMIN COMMANDS

* These commands start with the `*` character

*example:*

```kotlin
// This bans the user from the entire chat experience (all rooms).
ExecuteChatCommandRequest(
    command = "*ban",
    // ....
)
```

```kotlin
// This restores the user to the chat experience (all rooms).
ExecuteChatCommandRequest(
    command = "*restore",
    // ....
)
```

```kotlin
// This deletes all messages from the specified user.
ExecuteChatCommandRequest(
    command = "*purge",
    // ....
)
```

```kotlin
// This deletes all messages in this room.
// Assuming ADMIN password "testpassword123"
ExecuteChatCommandRequest(
    command = "*deleteallevents testpassword123",
    // ....
)
```

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#c81e90fc-1a54-40bb-a75b-2fc935c12b59>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val executeChatCmdResponse = withContext(Dispatchers.IO) {
        chatClient.executeChatCommand(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = ExecuteChatCommandRequest(
                command = "Hello World!",
                userid = "023976080242ac120002" // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `executeChatCmdResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Execute Chat Command (Announcement by Admin)

Invoke this function to execute a command in a chat room.

**Precondition**: The user must JOIN the room first with a call to Join Room. Otherwise you'll receive HTTP Status Code PreconditionFailed (412)

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#45c88ff5-4006-491a-b4d3-5f2ad542fa09>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val executeChatCmdResponse = withContext(Dispatchers.IO) {
        chatClient.executeChatCommand(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = ExecuteChatCommandRequest(
                command = "This is a test annoncement!",
                userid = "023976080242ac120002", // ID of an existing user from this chatroom
                eventtype = "announcement"
            )
        )
    }

    // Resolve `executeChatCmdResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Execute Dance Action

Invoke this function to execute a command `High five or Dance Action` in a chat room.

**Precondition**: The user must JOIN the room first with a call to Join Room. Otherwise you'll receive HTTP Status Code PreconditionFailed (412)

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#45c88ff5-4006-491a-b4d3-5f2ad542fa09>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val executeChatCmdResponse = withContext(Dispatchers.IO) {
        chatClient.executeChatCommand(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = ExecuteChatCommandRequest(
                command = "/high5 georgew",
                userid = "023976080242ac120002", // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `executeChatCmdResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Reply to a Message (Threaded)

Invoke this function to create a threaded reply to another message event.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#d54ce72a-1a8a-4230-b950-0d1b345c20c6>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val sendThreadedReplyResponse = withContext(Dispatchers.IO) {
        chatClient.sendThreadedReply(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            replyTo = "0976280012ac00023242",   // ID of an existing event from this chatroom, which you intend to reply to
            request = SendThreadedReplyRequest(
                body = "This is Jessy, replying to your greetings yow!!!",
                userid = "023976080242ac120002", // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `sendThreadedReplyResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Quote a Message

Invoke this function to quote an existing message and republishes it with a new message.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#c463cddd-c247-4e7c-8280-2d4880813149>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val sendQuotedReplyResponse = withContext(Dispatchers.IO) {
        chatClient.sendQuotedReply(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            replyTo = "0976280012ac00023242",   // ID of an existing event from this chatroom, which you intend to reply to
            request = SendQuotedReplyRequest(
                body = "This is Jessy, quoting your greetings yow!!!",
                userid = "023976080242ac120002", // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `sendQuotedReplyResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## React To A Message ("Like")

Invoke this function to add or remove a reaction to an existing event.

After this completes, a new event appears in the stream representing the reaction. The new event will have an updated version of the event in the replyto field, which you can use to update your UI.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#977044d8-9133-4185-ac1f-4d96a40aa60b>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val reactToAMsgResponse = withContext(Dispatchers.IO) {
        chatClient.reactToEvent(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            eventId = "0976280012ac00023242",   // ID of an existing event from this chatroom, which you intend to reply to
            request = ReactToAMessageRequest(
                userid = "023976080242ac120002", // ID of an existing user from this chatroom
                reaction = "like",
                reacted = true
            )
        )
    }

    // Resolve `reactToAMsgResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Report Message

Invoke this function to REPORT a message to the moderation team.

After this completes, a new event appears in the stream representing the reaction. The new event will have an updated version of the event in the replyto field, which you can use to update your UI.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#2b231a1e-a12b-4a2e-b7f3-7104bec91a0a>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val reportMsgResponse = withContext(Dispatchers.IO) {
        chatClient.reportMessage(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            eventId = "0976280012ac00023242",   // ID of an existing event from this chatroom, which you intend to reply to
            request = ReportMessageRequest(
                reporttype = "abuse",
                userid = "023976080242ac120002" // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `reportMsgResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Execute Admin Command (*help)

Invoke this function to execute `help` command in a chat room.

**Precondition**: The user must JOIN the room first with a call to Join Room. Otherwise you'll receive HTTP Status Code PreconditionFailed (412)

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#08b0ab21-0e9f-40a3-bdfe-f228196fea03>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val executeChatCmdResponse = withContext(Dispatchers.IO) {
        chatClient.executeChatCommand(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = ExecuteChatCommandRequest(
                command = "*help*",
                userid = "023976080242ac120002", // ID of an existing user from this chatroom
            )
        )
    }

    // Resolve `executeChatCmdResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Get Updates

Invoke this function to get the recent updates to a room.

* You can use this function to poll the room to get the recent events in the room. The recommended poll interval is 500ms. Each event has an ID and a timestamp. To detect new messages using polling, call this function and then process items with a newer timestamp than the most recent one you have already processed.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#be93067d-562e-41b2-97b2-b2bf177f1282>

Below is a code sample on how to use this SDK feature:

### Using Coroutines Flow extension

```kotlin
import com.sportstalk.api.polling.coroutines.allEventUpdates
// ...

// Under Fragment class
// ...
// User must first Join Chat Room
// Now that the test user has joined the room, setup reactive subscription to event updates
// Below returns a Flow<List<ChatEvent>>
lifecycleScope.launch {
    chatClient.allEventUpdates(
        chatRoomId = testChatRoom.id!!,
        frequency = 1000L /* Polling Frequency. Defaults to 500 milliseconds if not explicitly provided */,
        /*
        * The following are placeholder/convenience functions should the developers want to implement it
        * in a callback-oriented way. (Invoked as subscription's side-effect. In RxJava, these are invoked via .doOnNext { ... }. In coroutine flow, these are invoked via .onEach { ... })
        */
        onChatEvent = { event: ChatEvent -> /* Handle all other eventtype */ }, // OPTIONAL
        onGoalEvent = { goalEvent: ChatEvent -> /* Handle eventtype == "goal" */ }, // OPTIONAL
        onAdEvent = { adEvent: ChatEvent -> /* Handle eventtype == "advertisement" */ }, // OPTIONAL
        onReply = { replyEvent: ChatEvent -> /* Handle eventtype == "reply" */ }, // OPTIONAL
        onReaction = { reactionEvent: ChatEvent -> /* Handle eventtype == "reaction" */ }, // OPTIONAL
        onPurgeEvent = { purgeEvent: ChatEvent -> /* Handle eventtype == "purge" */ } // OPTIONAL
   )
   .distinctUntilChanged()
   .onEach { events ->
      // Alternatively, the developer can opt to consume the events in here...
      // NOTE:: ONLY choose 1 approach to avoid handling it twice.
      // Iterate each event item(s)
      events.forEach { chatEvent ->
         when(chatEvent.eventtype) {
            EventType.GOAL -> { /* Handle goal event types */ }
            EventType.ADVERTISEMENT -> { /* Handle advertisements event types */ }
            // ...
            // ...
         }
      }
   }
   .launchIn(lifecycleScope /* Already provided by androidx.Fragment */)

    // Then, perform start listening to event updates
    chatClient.startListeningToChatUpdates(
        forRoomId = testChatRoom.id!!
    )

    // At some point in time, the developer might want to explicitly stop listening to event updates
    chatClient.stopListeningToChatUpdates(
        forRoomId = testChatRoom.id!!
    )

}

```

### Using Rx2Java extension

```kotlin
import com.sportstalk.api.polling.rxjava.allEventUpdates
// ...

// Under Fragment class
// ...
// User must first Join Chat Room
// Now that the test user has joined the room, setup reactive subscription to event updates
// Below returns a Flowable<List<ChatEvent>>
lifecycleScope.launch {
    chatClient.allEventUpdates(
        chatRoomId = testChatRoom.id!!,
        lifecycleOwner = viewLifecycleOwner /* Already provided by androidx.Fragment */,
        frequency = 1000L /* Polling Frequency. Defaults to 500 milliseconds if not explicitly provided */,
        /*
        * The following are placeholder/convenience functions should the developers want to implement it
        * in a callback-oriented way. (Invoked as subscription's side-effect. In RxJava, these are invoked via .doOnNext { ... }. In coroutine flow, these are invoked via .onEach { ... })
        */
        onChatEvent = { event: ChatEvent -> /* Handle all other eventtype */ }, // OPTIONAL
        onGoalEvent = { goalEvent: ChatEvent -> /* Handle eventtype == "goal" */ }, // OPTIONAL
        onAdEvent = { adEvent: ChatEvent -> /* Handle eventtype == "advertisement" */ }, // OPTIONAL
        onReply = { replyEvent: ChatEvent -> /* Handle eventtype == "reply" */ }, // OPTIONAL
        onReaction = { reactionEvent: ChatEvent -> /* Handle eventtype == "reaction" */ }, // OPTIONAL
        onPurgeEvent = { purgeEvent: ChatEvent -> /* Handle eventtype == "purge" */ } // OPTIONAL
   )
   .distinctUntilChanged()
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .subscribe { events ->
      // Alternatively, the developer can opt to consume the events in here...
      // NOTE:: ONLY choose 1 approach to avoid handling it twice.
      // Iterate each event item(s)
      events.forEach { chatEvent ->
         when(chatEvent.eventtype) {
            EventType.GOAL -> { /* Handle goal event types */ }
            EventType.ADVERTISEMENT -> { /* Handle advertisements event types */ }
            // ...
            // ...
         }
      }
   }
   .addTo(rxDisposeBag) // RxKotlin `Observable<*>.addTo()`/`Flowable<*>.addTo()`

    // Then, perform start listening to event updates
    chatClient.startListeningToChatUpdates(
        forRoomId = testChatRoom.id!!
    )

    // At some point in time, the developer might want to explicitly stop listening to event updates
    chatClient.stopListeningToChatUpdates(
        forRoomId = testChatRoom.id!!
    )

}

```

### Using LiveData extension

```kotlin
import com.sportstalk.api.polling.livedata.allEventUpdates
// ...

// Under Fragment class
// ...
// User must first Join Chat Room
// Now that the test user has joined the room, setup reactive subscription to event updates
// Below returns a LiveData<List<ChatEvent>>
lifecycleScope.launch {
    chatClient.allEventUpdates(
        chatRoomId = testChatRoom.id!!,
        lifecycleOwner = viewLifecycleOwner /* Already provided by androidx.Fragment */,
        frequency = 1000L /* Polling Frequency. Defaults to 500 milliseconds if not explicitly provided */,
        /*
        * The following are placeholder/convenience functions should the developers want to implement it
        * in a callback-oriented way. (Invoked as subscription's side-effect. In RxJava, these are invoked via .doOnNext { ... }. In coroutine flow, these are invoked via .onEach { ... })
        */
        onChatEvent = { event: ChatEvent -> /* Handle all other eventtype */ }, // OPTIONAL
        onGoalEvent = { goalEvent: ChatEvent -> /* Handle eventtype == "goal" */ }, // OPTIONAL
        onAdEvent = { adEvent: ChatEvent -> /* Handle eventtype == "advertisement" */ }, // OPTIONAL
        onReply = { replyEvent: ChatEvent -> /* Handle eventtype == "reply" */ }, // OPTIONAL
        onReaction = { reactionEvent: ChatEvent -> /* Handle eventtype == "reaction" */ }, // OPTIONAL
        onPurgeEvent = { purgeEvent: ChatEvent -> /* Handle eventtype == "purge" */ } // OPTIONAL
   )
   .distinctUntilChanged()  // livedata-ktx
   .observe(viewLifeCycleOwner, Observer { events ->
      // Alternatively, the developer can opt to consume the events in here...
      // NOTE:: ONLY choose 1 approach to avoid handling it twice.
      // Iterate each event item(s)
      events.forEach { chatEvent ->
         when(chatEvent.eventtype) {
            EventType.GOAL -> { /* Handle goal event types */ }
            EventType.ADVERTISEMENT -> { /* Handle advertisements event types */ }
            // ...
            // ...
         }
      }
   })

    // Then, perform start listening to event updates
    chatClient.startListeningToChatUpdates(
        forRoomId = testChatRoom.id!!
    )

    // At some point in time, the developer might want to explicitly stop listening to event updates
    chatClient.stopListeningToChatUpdates(
        forRoomId = testChatRoom.id!!
    )

}

```

## List Messages By User

Invoke this function to get a list of users messages.

*This method requires authentication.*

The purpose of this method is to get a list of messages or comments by a user, with count of replies and reaction data. This way, you can easily make a screen in your application that shows the user a list of their comment contributions and how people reacted to it.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#0ec044c6-a3c0-478f-985a-156f6f5b660a>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val listUserMessages = withContext(Dispatchers.IO) {
        chatClient.listMessagesByUser(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            userid = "023976080242ac120002", // ID of an existing user from this chatroom
            limit = 20, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of user message(s).
        )
    }

    // Resolve `listUserMessages` from HERE onwards(ex. update UI displaying the response data)...
}

```

## List Event History

Invoke this function to list events history.

* This method enables you to download all of the events from a room in large batches. It should only be used if doing a data export.
* This method returns a list of events sorted from oldest to newest.
* This method returns all events, even those in the inactive state.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#b8ca9766-ab07-4c8c-8e25-002a24a8feaa>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val listEventsHistory = withContext(Dispatchers.IO) {
        chatClient.listEventsHistory(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            limit = 20, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of events.
        )
    }

    // Resolve `listEventsHistory` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Message is Reported

Invoke this function to check if the current user has already reported a message.

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val messageIsReported = withContext(Dispatchers.IO) {
        chatClient.messageIsReported(
            eventId = "7620812242ac09300002"    // ID of an existing event from the chat room
            userid = "023976080242ac120002", // ID of an existing user from the chat room
        )
    }

    // Resolve `messageIsReported` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Message is Reacted To

Invoke this function to check if a message was reacted to by the current user.

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val messageIsReactedTo = withContext(Dispatchers.IO) {
        chatClient.messageIsReactedTo(
            eventId = "7620812242ac09300002"    // ID of an existing event from the chat room
            userid = "023976080242ac120002", // ID of an existing user from the chat room
            reaction = "like"   // One of the EventReaction string constants
        )
    }

    // Resolve `messageIsReactedTo` from HERE onwards(ex. update UI displaying the response data)...
}

```

## List Previous Events

Invoke this function to list previous events.

* This method allows you to go back in time to "scroll" in reverse through past messages. The typical use case for this method is to power the scroll-back feature of a chat window allowing the user to look at recent messages that have scrolled out of view. It's intended use is to retrieve small batches of historical events as the user is scrolling up.
* This method returns a list of events sorted from newest to oldest.
* This method excludes events that are not in the active state (for example if they are removed by a moderator)
* This method excludes non-displayable events (reaction, replace, remove, purge)
* This method will not return events that were emitted and then deleted before this method was called

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#f750f610-5db8-46ca-b9f7-a800c2e9c94a>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val listPreviousEvents = withContext(Dispatchers.IO) {
        chatClient.listPreviousEvents(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            limit = 20, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of events.
        )
    }

    // Resolve `listPreviousEvents` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Get Event by ID

Invoke this function to get a chat event by ID.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#04f8f563-eacf-4a64-9f00-b3d6c050a2fa>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val chatEventResponse = withContext(Dispatchers.IO) {
        chatClient.getEventById(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            eventId = "7620812242ac09300002"    // ID of an existing event from the chat room
        )
    }

    // Resolve `chatEventResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Purge User Messages

Invoke this function to execute a command in a chat room to purge all messages for a user.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#04ffee45-a3e6-49b8-8968-46b219020b66>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val purgeCmdResponse = withContext(Dispatchers.IO) {
        chatClient.executeChatCommand(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            // Assuming ADMIN password "testpassword123"
            // Assuming user "@nicoleWd" exists
            request = ExecuteChatCommandRequest(
                command = "*purge testpassword123 nicoleWd",
                userid = "023976080242ac120002" // ID of an existing user "@nicoleWd" from this chatroom
            )
        )
    }

    // Resolve `purgeCmdResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Bounce User

Invoke this function to remove the user from the room and prevent the user from reentering.

Optionally display a message to people in the room indicating this person was bounced.

When you bounce a user from the room, the user is removed from the room and blocked from reentering. Past events generated by that user are not modified (past messages from the user are not removed).

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#7116d7ca-a1b8-44c1-8894-bea85225e4c7>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val bounceUserResponse = withContext(Dispatchers.IO) {
        chatClient.bounceUser(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            // Assuming user "@nicoleWd" exists
            request = BounceUserRequest(
                userid = "023976080242ac120002", // ID of an existing user "@nicoleWd" from this chatroom
                bounce = true,
                announcement = "@nicoleWd has been banned."
            )
        )
    }

    // Resolve `bounceUserResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Flag Message Event As Deleted

Invoke this function to set a ChatEvent as logically deleted.

Everything in a chat room is an event. Each event has a type. Events of type "speech, reply, quote" are considered "messages".

Use logical delete if you want to flag something as deleted without actually deleting the message so you still have the data. When you use this method:

* The message is not actually deleted. The comment is flagged as deleted, and can no longer be read, but replies are not deleted.
* If flag "permanentifnoreplies" is true, then it will be a permanent delete instead of logical delete for this comment if it has no children.
* If you use "permanentifnoreplies" = true, and this comment has a parent that has been logically deleted, and this is the only child, then the parent will also be permanently deleted (and so on up the hierarchy of events).

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#92632caf-9bd0-449d-91df-90fef54f6634>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val deleteEventResponse = withContext(Dispatchers.IO) {
        chatClient.setMessageAsDeleted(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            eventId = "7620812242ac09300002",    // ID of an existing event from the chat room
            userid = "023976080242ac120002", // ID of an existing user "@nicoleWd" from this chatroom
            // Assuming user "@nicoleWd" exists
            deleted = false,
            permanentifnoreplies = true
        )
    }

    // Resolve `deleteEventResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Delete Event

Invoke this function to deletes an event from the room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#f2894c8f-acc9-4b14-a8e9-216b28c319de>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val deleteEventResponse = withContext(Dispatchers.IO) {
        chatClient.deleteEvent(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            eventId = "7620812242ac09300002",    // ID of an existing event from the chat room
            userid = "023976080242ac120002", // ID of an existing user "@nicoleWd" from this chatroom
        )
    }

    // Resolve `deleteEventResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Delete All Events in Room

Invoke this function to execute a command in a chat room to delete all messages in the chatroom.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#e4d62330-469e-4e37-a42e-049b10259152>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val deleteAllEventsCmdResponse = withContext(Dispatchers.IO) {
        chatClient.executeChatCommand(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            // Assuming ADMIN password "testpassword123"
            request = ExecuteChatCommandRequest(
                command = "*deleteallevents testpassword123",
                userid = "023976080242ac120002" // ID of an existing user "@nicoleWd" from this chatroom
            )
        )
    }

    // Resolve `deleteAllEventsCmdResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Update Room (Close a room)

Invoke this function to update an existing room.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#e4d62330-469e-4e37-a42e-049b10259152>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val updatedEventResponse = withContext(Dispatchers.IO) {
        chatClient.updateRoom(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            request = UpdateChatRoomRequest(
                    name = "Test Chat Room 1 - UPDATED",
                    customid = "test-room-1-updated",
                    description = "[UPDATED] This is a test chat room 1.",
                    moderation = "post",
                    enableactions = false,
                    enableenterandexit = false,
                    enableprofanityfilter = true,
                    delaymessageseconds = 10L,
                    roomisopen = false,
                    maxreports = 30
            )
        )
    }

    // Resolve `updatedEventResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Exit a Room

Invoke this function to exit from a chatroom where the user has currently joined.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#408b43ca-fca9-4f2d-8883-f6f725d140f2>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val exitRoomResponse = withContext(Dispatchers.IO) {
        chatClient.exitRoom(
            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
            userid = "023976080242ac120002" // ID of an existing user from this chatroom
        )
    }

    // Resolve `exitRoomResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Delete Room

Invoke this function to delete the specified room and all events contained therein) by ID

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#c5ae345d-004d-478a-b543-5abaf691000d>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val deleteRoomResponse = withContext(Dispatchers.IO) {
        chatClient.deleteRoom(
            chatRoomId = "080001297623242ac002"    // ID of an existing chat room
        )
    }

    // Resolve `deleteRoomResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```

## List Messages Needing Moderation

Invoke this function to list all the messages in the moderation queue.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#bcdbda1b-e495-46c9-8fe9-c5dc6a4c1756>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val listMessagesInModeration = withContext(Dispatchers.IO) {
        chatClient.listMessagesNeedingModeration(
            roomId = "080001297623242ac002",    // ID of an existing chat room
            limit = 20, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of messages from this chatroom.
        )
    }

    // Resolve `listMessagesInModeration` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Approve Message

Invoke this function to approve a message in the moderation queue.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#6f9bf714-5b3b-48c9-87d2-eb2e12d2bcbf>

Below is a code sample on how to use this SDK feature:

```kotlin
val chatClient = SportsTalk247.ChatClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v1/" // This is just a sample API endpoint
   )
)

// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
    val approveResponse = withContext(Dispatchers.IO) {
        chatClient.approveMessage(
            eventId = "0976280012ac00023242",   // ID of an existing event from this chatroom, which you intend to reply to
            approve = true
        )
    }

    // Resolve `approveResponse` from HERE onwards(ex. update UI displaying the response data)...
}

```
