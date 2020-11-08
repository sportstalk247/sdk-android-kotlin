# How to use SportsTalk SDK

## Instantiate SportsTalkManager Clients

This Sportstalk SDK is meant to power custom chat applications. Sportstalk does not enforce any restricitons on your UI design, but instead empowers your developers to focus on the user experience without worrying about the underlying chat behavior.

```kotlin
class MyFragment: Fragment() {

    // ...
    // ...

    // YOUR APP ID
    val appId = "c84cb9c852932a6b0411e75e" // This is just a sample app id
    // YOUR API TOKEN
    val apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA" // This is just a sample token
    val endpoint = "http://api.custom.endpoint/v1/" // please ensure out of the box the SDKs are configured for production URL
    
    // Instantiate User Client
    val userClient = SportsTalk247.UserClient(
        config = ClientConfig(
            appId = appId,
            apiToken = apiToken,
            endpoint = endpoint
        )
    )

    // Instantiate Chat Client
    val chatClient = SportsTalk247.ChatClient(
        config = ClientConfig(
            appId = appId,
            apiToken = apiToken,
            endpoint = endpoint
        )
    )

    // ...

}

```

## Use SDK with Coroutines

Android Sportstalk SDK is an Asynchronous-driven API, powered by Kotlin [Coroutines](https://developer.android.com/kotlin/coroutines)) to gracefully handle asynchronous operations.

Client SDK functions are declared with `suspend` keyword. This means that the function should be invoked from within coroutine scope. See the example below:

```kotlin
class MyFragment: Fragment() {

    // ...
    // ...
    // Instantiate User Client
    val userClient = SportsTalk247.UserClient(/*...*/)

    // Instantiate Chat Client
    val chatClient = SportsTalk247.ChatClient(/*...*/)

    override fun onViewCreated(view: View) {
        // ...
        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val createdUser = withContext(Dispatchers.IO) {
                userClient.createOrUpdateUser(
                    request = CreateUpdateUserRequest(
                                userid = "8cb689cc-21b7-11eb-adc1-0242ac120002", // sample user ID
                                handle = "sample_handle_123",
                                displayname = "Test Name 123", // OPTIONAL
                                pictureurl = "https://i.imgur.com/ohlx5wW.jpeg", // OPTIONAL
                                profileurl = "https://i.imgur.com/ohlx5wW.jpeg" // OPTIONAL
                            )
                )
            }

            // Resolve `createdUser` from HERE onwards(ex. update UI displaying the response data)...
        }

    }

}

```

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
  
// Under Fragment class
// Execute within coroutine scope
lifecycleScope.launch {
   val testComment = Comment(id = "0987654321",...)
   
   val setCommentDeletedResponse = try {  
      withContext(Dispatchers.IO) {  
         // These should throw Error 404 - "The specified conversation was not found and was not deleted.".  
         commentClient.permanentlyDeleteComment(  
            conversationid = "Non-existent-Conversation-ID",  
            commentid = testComment.id!!  
         )
      }
   } catch(err: SportsTalkException) {
      // Resolve ERROR from HERE.
      return
   }
}
```
