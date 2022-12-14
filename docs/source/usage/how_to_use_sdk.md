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

## Implement Custom JWT

``` tabs::
    
    .. tab:: sdk-coroutine
    
        You can instantiate a JWTProvider instance and provide a token refresh action function that returns a new token. Then you just have to launch the coroutine flow by calling `JWTProvider.observe()` method.

        .. code-block:: kotlin

            // ...
            // ...
        
            // YOUR APP ID
            val appId = "c84cb9c852932a6b0411e75e" // This is just a sample app id
            // YOUR API TOKEN
            val apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA" // This is just a sample token
            val endpoint = "http://api.custom.endpoint/v1/" // please ensure out of the box the SDKs are configured for production URL
            
            val config = ClientConfig(
                appId = appId,
                apiToken = apiToken,
                endpoint = endpoint
            )
            
            // Prepare JWTProvider
            val myJwtProvider = JWTProvider(
                token = "...",  // Developer may immediately provide a token on init
                tokenRefreshAction = /* This is a suspend function */ { 
                    val newToken = doPerformFetchNewToken() // Developer may perform a long-running operation to generate a new JWT
                    return@JWTProvider newToken
                }
            )
            
            // Set custom JWTProvider
            SportsTalk247.setJWTProvider(
                config = config,
                provider = myJwtProvider
            )
            
            //
            // In order to make refresh callback work, developer must bind through a coroutine scope by calling `observe()` function.
            // 
            val coroutineScope = viewLifecycleOwner.lifecycleScope  // If called from within a Fragment
            // val coroutineScope = this.lifecycleScope  // If called from within a Fragment
            // val coroutineScope = CoroutineScope(context = EmptyCoroutineContext) // Developer may also provide a custom coroutine scope of choice
            myJwtProvider.observe()
                .launchIn(coroutineScope) 
            
            // ...
            // Instantiate Chat Client
            val chatClient = SportsTalk247.ChatClient(
                config = ClientConfig(
                    appId = appId,
                    apiToken = apiToken,
                    endpoint = endpoint
                )
            )
            
            // Launch thru coroutine block
            // https://developer.android.com/topic/libraries/architecture/coroutines
            lifecycleScope.launch {
                try {
                    // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
                    val joinRoomResponse = withContext(Dispatchers.IO) {
                        chatClient.joinRoom(
                            chatRoomId = "080001297623242ac002",    // ID of an existing chat room
                            request = JoinChatRoomRequest(
                                userid = "023976080242ac120002" // ID of an existing user from this chatroom
                            )
                        )
                    }                    
                } catch(err: SportsTalkException) {
                    err.printStackTrace()
                    
                    //
                    // Handle Unauthorized Error
                    //  - Attempt request refresh token
                    // 
                    if(err.code == 401) {
                        jwtProvider.refreshToken()
                        // Then, prompt UI layer to perform the operation again after a short while(this is to ensure that the token gets refreshed first before retry attempt)
                    }
                }
    
            }

    .. tab:: sdk-reactive-rx2
        
        You can instantiate a JWTProvider instance and provide a token refresh action observable that returns a new token. Then you just have to launch the coroutine flow by calling `JWTProvider.observe()` method.
        
        .. code-block:: kotlin
            
            // ...
            // ...
            val rxDisposeBag = CompositeDisposable()
            
            // Prepare JWTProvider
            val myJwtProvider = JWTProvider(
                token = "...", // Developer may immediately provide a token on init
                tokenRefreshObservable = {
                    return@JWTProvider Single.create<String?> { e ->
                        val newToken = doPerformFetchNewToken() // Developer may perform a long-running operation to generate a new JWT                                 
                        e.onSuccess(newToken) 
                    } 
                }
            )
            
            // Set custom JWTProvider
            SportsTalk247.setJWTProvider(
                config = config,
                provider = myJwtProvider
            )
            
            //
            // In order to make refresh callback work, developer must be subscribe by calling `observe()` function.
            // 
            jwtProvider
                .observe()
                .doOnSubscribe {
                    rxDisposeBag.add(it)
                }
                .subscribe()

            // Instantiate User Client
            val userClient = SportsTalk247.UserClient(/*...*/)

            // Instantiate Chat Client
            val chatClient = SportsTalk247.ChatClient(/*...*/)

            override fun onViewCreated(view: View) {
            //
            // ...
            //
            chatClient.joinRoom(
                chatRoomId = "080001297623242ac002",    // ID of an existing chat room
                request = JoinChatRoomRequest(
                    userid = "023976080242ac120002" // ID of an existing user from this chatroom
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnError { 
                    val err = it as? SportsTalkException ?: return@doOnError
                    err.printStackTrace()
                    //
                    // Handle Unauthorized Error
                    //  - Attempt request refresh token
                    // 
                    if(err.code == 401) {
                        jwtProvider.refreshToken()
                        // Then, prompt UI layer to perform the operation again after a short while(this is to ensure that the token gets refreshed first before retry attempt)
                    }
                }
                .subscribe { joinRoomResponse ->
                    // Resolve `joinRoomResponse` (ex. Display prompt OR Update UI)
                }
    
```

You can also directly specify the JWT value by calling `JWTProvider.setToken(newToken)`.
There is also a function provided to explicitly refresh token by calling `JWTProvider.refreshToken()`, which will trigger the provided token refresh action above to fetch a new token and will automatically add that on the SDK.

Once the User Token has been added to the SDK, the SDK will automatically append it to all requests.

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
