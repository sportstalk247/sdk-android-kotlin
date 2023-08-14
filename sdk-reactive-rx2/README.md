[![Release](https://jitpack.io/v/com.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.github.sportstalk247/sdk-android-kotlin)

# sdk-reactive-rx2

```groovy
implementation 'com.github.sportstalk247:sdk-android-kotlin:sdk-reactive-rx2:X.Y.Z'
```

[![Release](https://jitpack.io/v/com.github.sportstalk247/sdk-android-kotlin.svg)](https://jitpack.io/#com.github.sportstalk247/sdk-android-kotlin)

# How to Use

## Instantiate SportsTalkManager Client

This Sportstalk SDK is meant to power custom chat applications. Sportstalk does not enforce any restricitons on your UI design, but instead empowers your developers to focus on the user experience without worrying about the underlying chat behavior.

This Android Sportstalk SDK artifact is a Reactive-driven API, powered by [RxJava]([https://github.com/ReactiveX/RxJava](https://github.com/ReactiveX/RxJava)) to gracefully handle reactive operations.

```kotlin
import com.sportstalk.reactive.SportsTalk247
// ...

class MyFragment: Fragment() {
    //...
    //...
   val appId = "c84cb9c852932a6b0411e75e"
   val apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA"
   val endpoint = "http://api.custom.endpoint/v1/" // please ensure out of the box the SDKs are configured for production URL

   // Instantiate UserClient
   val userClient = SportsTalk247.UserClient(
      config = ClientConfig(
         appId = appId,
         apiToken = apiToken,
         endpoint = endpoint
      )
   )

   // Instantiate ChatClient
   val chatClient = SportsTalk247.ChatClient(
      config = ClientConfig(
         appId = appId,
         apiToken = apiToken,
         endpoint = endpoint
      )
   )

}
```

## User Client Features
### Create or Update User
```kotlin
val rxDisposeBag = CompositeDisposable()

userClient.createOrUpdateUser(
    request = CreateUpdateUserRequest(
        userid = "023976080242ac120002",
        handle = "sample_handle_123",
        displayname = "Test Name 123", // OPTIONAL
        pictureurl = "<Image URL>", // OPTIONAL
        profileurl = "<Image URL>" // OPTIONAL
    )
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { createdUser ->
        // Resolve `createdUser` (ex. Display prompt OR Update UI)
    }
```

### Get User Details
```kotlin
val rxDisposeBag = CompositeDisposable()

userClient.getUserDetails(
    userid = "023976080242ac120002"
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { userDetails ->
        // Resolve `userDetails` (ex. Display prompt OR Update UI)
    }

```

### List Users
```kotlin
val rxDisposeBag = CompositeDisposable()

userClient.listUsers(
    limit = 10, /* Defaults to 200 on backend API server */
    cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of user(s).
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { listUsers ->
        // Resolve `listUsers` (ex. Display prompt OR Update UI)
    }
```

For more API documentation reference, you may browse our [read-the-docs documentation](https://sdk-android-kotlin.readthedocs.io/en/latest/usage/user_client.html).

## Chat Client Features

### Join Chat Room

```kotlin
import com.sportstalk.reactive.SportsTalk247
// ...

// Under Fragment class 

val rxDisposeBag = CompositeDisposable()

// Assuming that there is already an existing User instance
val testUser = User(...)
// Assuming that there is already an existing ChatRoom instance
val testChatRoom = ChatRoom(..., customid = "new-york-room-1", ...)
// Attempt Join Chat Room
chatApi.joinRoomByCustomId(
   chatRoomCustomId = testChatRoom.customid!!,
   request = JoinChatRoomRequest(
      userid = testUser.userid!!,
      handle = testUser.handle!!
   )
)
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .doOnSubscribe { rxDisposeBag.add(it) }
   .subscribe { joinRoomResponse ->
      // Once joined, the developer may immediately access the join response's `eventscursor.events` field, which contains an initial list of messages of the chat room
      val initialMessages: List<ChatEvent> = joinRoomResponse.eventscursor?.events ?: listOf()
      // ex. display in UI the initial chat messages/events
   }
```

### Listen to Event Updates

The Chat service provides a polling mechanism to dispatch chat event/message updates every N milliseconds(provided as frequency argument). Chat event/message updates are dispatched in a reactive pattern.

```kotlin
import com.sportstalk.reactive.api.polling.allEventUpdates

// Under Fragment class
class MyFragment: Fragment() {
   
   // ...

   override fun onViewCreated(view: View) {
      super.onViewCreated(view)

      // ...

      // Now that the test user has joined the room, setup reactive subscription to event updates
      // Below returns a Flow<List<ChatEvent>>
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
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .distinctUntilChanged()
      .doOnSubscribe { rxDisposeBag.add(it) }
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

      // Then, perform start listening to event updates
      chatClient.startListeningToChatUpdates(forRoomId = testChatRoom.id!!)

      // ...

      // At some point in time, the developer might want to explicitly stop listening to event updates
      chatClient.stopListeningToChatUpdates(forRoomId = testChatRoom.id!!)

      // ...
   }

}
```

### Send a Chat Message

```kotlin
   // To Send a Chat Message
   chatApi.executeChatCommand(
      chatRoomId = testChatRoom.id!!,
      request = ExecuteChatCommandRequest(
         command = "Yow Jessy, how are you doin'?",
         userid = testUser.userid!!
      )
   )
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .doOnSubscribe { rxDisposeBag.add(it) }
      .subscribe { executeChatResponse ->
         // Resolve `executeChatResponse` (ex. Display prompt OR Update UI)
      }
```

### Set Message/Event as Deleted

```kotlin
// This is with the assumption that
// 1. A test user has been created(or already exists)
// 2. A test chat room has already been created(or already exists)
// 3. Test user already joined the room
// 4. There exists a message/event on that room
chatClient.flagEventLogicallyDeleted(
            chatRoomId = testChatRoom.id!!,
            eventId = testEvent.id!!,
            userid = testUser.userid!!,
            permanentifnoreplies = false // OPTIONAL
)
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .doOnSubscribe { rxDisposeBag.add(it) }
   .subscribe { removeMessageResponse ->
      // Resolve `removeMessageResponse` (ex. Display prompt OR Update UI)
   }
```

### Bounce User (ban user from room)

```kotlin
// This is with the assumption that
// 1. A test user has been created(or already exists)
// 2. A test chat room has already been created(or already exists)
// 3. Test user already joined the room
val banUserInputRequest = BounceUserRequest(
   userid = testUserData.userid!!,
   bounce = true,
   announcement = "Test user has been banned."
)

chatClient.bounceUser(
   chatRoomId = testChatRoomData.id!!,
   request = banUserInputRequest
)
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .doOnSubscribe { rxDisposeBag.add(it) }
   .subscribe { banUserResponse ->
      // Resolve `banUserResponse` (ex. Display prompt OR Update UI)
      // Next call to getEventUpdates() now includes a ChatEvent with eventtype="bounce"
   }
```

For more API documentation reference, you may browse our [read-the-docs documentation](https://sdk-android-kotlin.readthedocs.io/en/latest/usage/chat_client.html).

## How to use Comment Client

```kotlin
// Under Fragment class
val commentClient = SportsTalk247.CommentClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e",
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA",
      endpoint = "http://api.custom.endpoint/v1/"
   )
)
```

### Create a Conversation

```kotlin
val rxDisposeBag = CompositeDisposable()

commentClient.createOrUpdateConversation(
    request = CreateOrUpdateConversationRequest(
        conversationid = "test-conversation-id123",
        property = "sportstalk247.com/apidemo",
        moderation = "post",
        enableprofanityfilter = false,
        title = "Sample Conversation",
        open = true,
        customid = "/articles/2020-03-01/article1/something-very-important-happened"
    )
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { createdConversation ->
        // Resolve `createdConversation` (ex. Display prompt OR Update UI)
    }
```

### Create/Publish a comment against a Conversation

```kotlin
val rxDisposeBag = CompositeDisposable()

commentClient.createComment(
    conversationid = "test-conversation-id123",
    request = CreateCommentRequest(
        userid = "test-user-id123", // ID of the User who is attempting to create the comment
        displayname = "HelloUser1", // [OPTIONAL] Override display name of the User who is attempting to create the comment
        body = "Hello, this is my comment!",
        customtype = null,    // [OPTIONAL]
        customfield1 = null,    // [OPTIONAL]
        customfield2 = null,    // [OPTIONAL]
        custompayload = "{ num: 0 }",    // [OPTIONAL]
    )
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { createdComment ->
        // Resolve `createdComment` (ex. Display prompt OR Update UI)
    }
```

### Reply to a comment

```kotlin
val rxDisposeBag = CompositeDisposable()

commentClient.replyToComment(
    conversationid = "test-conversation-id123",
    replyto = "test-comment-id123", // ID of the comment you are about to reply to
    request = CreateCommentRequest(
        userid = "test-user-id123", // ID of the User who is attempting to create the comment
        displayname = "HelloUser1", // [OPTIONAL] Override display name of the User who is attempting to create the comment
        body = "Hello, this is my comment!",
        customtype = null,    // [OPTIONAL]
        customfield1 = null,    // [OPTIONAL]
        customfield2 = null,    // [OPTIONAL]
        custompayload = "{ num: 0 }",    // [OPTIONAL]
    )
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { commentReply ->
        // Resolve `commentReply` (ex. Display prompt OR Update UI)
    }
```

### Permanently Delete Comment

```kotlin
val rxDisposeBag = CompositeDisposable()

commentClient.permanentlyDeleteComment(
    conversationid = "test-conversation-id123",
    commentid = "test-comment-id123",
)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .doOnSubscribe { rxDisposeBag.add(it) }
    .subscribe { deletedComment ->
        // Resolve `deletedComment` (ex. Display prompt OR Update UI)
    }
```

For more API documentation reference, you may browse our [read-the-docs documentation](https://sdk-android-kotlin.readthedocs.io/en/latest/usage/comment_client.html).

## Handling SDK Exception

If any client operations receive an error response, whether it be Network, Server, or Validation Error, these functions will throw an instance of `SportsTalkException`.

```kotlin
data class SportsTalkException(
   val kind: String? = null, // "api.result"
   val message: String? = null, // ex. "The specified comment was not found."
   val code: Int? = null // ex. 404,
   val data: Map<String, String?>? = null,
   val err: Throwable? = null
)

// These should throw Error 404 - "The specified conversation was not found and was not deleted.".
commentClient.permanentlyDeleteComment(
   conversationid = "Non-existent-Conversation-ID",
   commentid = testComment.id!!
)
   .subscribeOn(Schedulers.io())
   .observeOn(AndroidSchedulers.mainThread())
   .doOnError { err ->
      when(err) {
         is SportsTalkException -> {
            // You may access [SportsTalkException] fields for error prompt
         }
         else -> {
            // Catch all other error(s) encountered during the execution
         }
      }
    }
   .doOnSubscribe { rxDisposeBag.add(it) }
   .subscribe { setCommentDeletedResponse ->
      // Resolve `setCommentDeletedResponse` (ex. Display prompt OR Update UI)
   }
```
