
# sdk-android-kotlin

# Implementing the SDK
 You can download the latest SportsTalk Android SDK from the following location:
 https://gitlab.com/sportstalk247/sdk-android-kotlin
 You need to register SportsTalk API with 'Appkey' and 'Token'.
 How to get API Key and Token
 You need to visit the dashboard with the following URL:
 https://dashboard.sportstalk247.com
 Then click on ''Application Management'' link to generate the above
 # How to download the SDK from public repository
 The SportsTalk SDK has been published into **jitpack.io**.
 In order to use it in your application, just do the following:
 1. Add the following in root  **build.gradle** file
 ```groovy
 allprojects {
	 repositories {
	 // ...
		 maven {
			 url "https://jitpack.io"
		 }
	 }
 }
 ```
 2. Add the following lines in your module **build.gradle** file, under dependencies section
 ```groovy
 implementation 'com.gitlab.sportstalk247:sdk-android-kotlin:master-SNAPSHOT'
 ```
Then sync again. The gradle build should now be successful.

# How to Use
## Instantiate SportsTalkManager Client
This Sportstalk SDK is meant to power custom chat applications. Sportstalk does not enforce any restricitons on your UI design, but instead empowers your developers to focus on the user experience without worrying about the underlying chat behavior.

Android Sportstalk SDK is a Reactive and Asynchronous-driven API, powered by Java 8's CompletableFuture to handle asynchronous operation with direct compatibility to Kotlin [Coroutines]([https://developer.android.com/kotlin/coroutines](https://developer.android.com/kotlin/coroutines)) and [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html). Additionally, provides bridge support to wrap this API with [Rx2Java](https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/kotlinx-coroutines-rx2/README.md) or [LiveData](https://developer.android.com/kotlin/ktx\#livedata) extensions. This gives enough flexibility to any developers whichever framework they are familiar with.

```  kotlin
val appId = "c84cb9c852932a6b0411e75e"
val apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA" 
val endpoint = "http://api.custom.endpoint/v1/" // Use this to set a proxy on the web, or if you have an on-prem install of sportstalk at a custom location.

val userClient = SportsTalk247.UserClient(
	config = ClientConfig(
		appId = appId, 
		apiToken = apiToken,
		endpoint = endpoint
	)
)
// Launch thru coroutine block
// https://developer.android.com/topic/libraries/architecture/coroutines
lifecycleScope.launch {
	// Switch to IO Coroutine Context(Operation will be executed on IO Thread)
	val response = withContext(Dispatchers.IO) {
		userClient.createOrUpdateUser(
			request = CreateUpdateUserRequest(
				userid = "<USERID>",
				handle = "sample_handle_123",
				displayname = "Test Name 123", // OPTIONAL
				pictureurl = "<Image URL>", // OPTIONAL
				profileurl = "<Image URL>" // OPTIONAL
			)
			// CompletableFuture -> Await Deferred
			.await()
		)
	}
	
	// Resolve response from HERE onwards(update UI displaying the response data)...
}

```


## How to use Reactive Frameworks
### Using Coroutine Flow
Below are the following dependencies in order to make the SDK compatible to Coroutines:
```gradle
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.1"  
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.3.1"  
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.1"  
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.3.1"
```
...
...
### Using Rx2Java
Below are the following dependencies in order to make the SDK compatible to Rx2Java:
```gradle
implementation "io.reactivex.rxjava2:rxandroid:2.1.1"
implementation "io.reactivex.rxjava2:rxkotlin:2.4.0"
```
...
...
### Using LiveData
Below are the following dependencies in order to make the SDK compatible to LiveData:
```gradle
implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"  
kapt "androidx.lifecycle:lifecycle-common-java8:2.2.0"  
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"  
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.2.0"
```
...
...
## Chat

```kotlin
import com.sportstalk.api.polling.coroutines.allEventUpdates

// Under Fragment
val chatClient = SportsTalk247.ChatClient(
	config = ClientConfig(
		appId = "c84cb9c852932a6b0411e75e",
		apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA",
		endpoint = "http://api.custom.endpoint/v1/"
	)
)

lifecycleScope.launch {
	// Assuming that there is already an existing User instance
	val testUser = User(...)
	// Assuming that there is already an existing ChatRoom instance
	val testChatRoom = ChatRoom(..., customid = "new-york-room-1", ...)
	// Attempt Join Chat Room
	val joinResponse = withContext(Dispatchers.IO) {
		chatApi.joinRoomByCustomId(
			chatRoomCustomId = testChatRoom.customid!!,
			request = JoinChatRoomRequest(
				userid = testUser.userid!!,
				handle = testUser.handle!!
			)
		).await()
	}
	
	// Now that the test user has joined the room, setup reactive subscription to event updates
	// Below returns a Flow<List<ChatEvent>>
	chatClient.allEventUpdates(  
	  chatRoomId = testChatRoom.id!!,  
	  lifecycleOwner = viewLifecycleOwner /* Already provided by androidx.Fragment */,
	  frequency = 1000L /* Polling Frequency. Defaults to 500 milliseconds if not explicitly provided */,
	  /*  
	  * The following are placeholder/convenience functions should the developers want to implement it 
	  * in a callback-oriented way. (Invoked as subscription's side-effect. In RxJava, it is done via .doOnNext { ... })
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
				...
				...
			}
		}

	}
	.launchIn(lifecycleScope /* Already provided by androidx.Fragment */)
	
	// To Send a Chat Message
	val executeChatResponse = withContext(Dispatchers.IO) {
		chatApi.executeChatCommand(
			chatRoomId = testChatRoom.id!!,
			request = ExecuteChatCommandRequest(  
			  command = "Yow Jessy, how are you doin'?",  
			  userid = testUser.userid!! 
			)
			.await()
		)
	}
	// Resolve `response` (ex. Update UI)
}


```

## Work-in-progress...