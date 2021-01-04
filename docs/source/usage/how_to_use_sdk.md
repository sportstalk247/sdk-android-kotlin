# How to use SportsTalk SDK

## Instantiate SportsTalkManager Clients

This Sportstalk SDK is meant to power custom chat applications. Sportstalk does not enforce any restricitons on your UI design, but instead empowers your developers to focus on the user experience without worrying about the underlying chat behavior.

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        import com.sportstalk.coroutine.SportsTalk247
        // ...

    .. code-tab:: kotlin sdk-reactive-rx2

        import com.sportstalk.reactive.rx2.SportsTalk247
        // ...

```

```kotlin
// ...

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

## Using the SDK

``` tabs::
    
    .. tab:: sdk-coroutine

        This Android Sportstalk SDK artifact is an Asynchronous-driven API, powered by `Kotlin Coroutines <https://developer.android.com/kotlin/coroutines>`_ to gracefully handle asynchronous operations.
        
        Client SDK functions are declared with `suspend` keyword. This means that the function should be invoked from within coroutine scope. See the example below:

        .. code-block:: kotlin

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

    .. tab:: sdk-reactive-rx2

        This Android Sportstalk SDK artifact is a Reactive-driven API, powered by `RxJava <https://github.com/ReactiveX/RxJava>`_ to gracefully handle reactive operations.

        Client SDK functions returns RxJava types. See the example below:

        .. code-block:: kotlin
            
            class MyFragment: Fragment() {

                // ...
                // ...
                val rxDisposeBag = CompositeDisposable()

                // Instantiate User Client
                val userClient = SportsTalk247.UserClient(/*...*/)

                // Instantiate Chat Client
                val chatClient = SportsTalk247.ChatClient(/*...*/)

                override fun onViewCreated(view: View) {
                    // ...
                    
                    userClient.createOrUpdateUser(
                                request = CreateUpdateUserRequest(
                                            userid = "8cb689cc-21b7-11eb-adc1-0242ac120002", // sample user ID
                                            handle = "sample_handle_123",
                                            displayname = "Test Name 123", // OPTIONAL
                                            pictureurl = "https://i.imgur.com/ohlx5wW.jpeg", // OPTIONAL
                                            profileurl = "https://i.imgur.com/ohlx5wW.jpeg" // OPTIONAL
                                        )
                            )
                            .doOnSubscribe { rxDisposeBag.add(it) }
                            .subscribe { createdUser ->
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

```

``` tabs::

    .. code-tab:: kotlin sdk-coroutine
        
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

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()
        // ...
        // ...

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
