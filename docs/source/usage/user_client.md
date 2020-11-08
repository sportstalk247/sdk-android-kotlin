# User Client

## Create or Update User

Invoke this function if you want to create a user or update an existing user.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2>

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
    val createdUser = withContext(Dispatchers.IO) {
        userClient.createOrUpdateUser(
        request = CreateUpdateUserRequest(
                    userid = "023976080242ac120002",
                    handle = "sample_handle_123",
                    displayname = "Test Name 123", // OPTIONAL
                    pictureurl = "<Image URL>", // OPTIONAL
                    profileurl = "<Image URL>" // OPTIONAL
                )
        )
    }

    // Resolve `createdUser` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Get User Details

This will return all the information about the user.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#3323caa9-cc3d-4569-826c-69070ca51758>

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
    val userDetails = withContext(Dispatchers.IO) {
        userClient.getUserDetails(
            userid = "023976080242ac120002"
        )
    }

    // Resolve `userDetails` from HERE onwards(ex. update UI displaying the response data)...
}

```

## List Users

Use this function to cursor through a list of users. This function will return users in the order in which they were created, so it is safe to add new users while cursoring through the list.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#51718594-63ac-4c28-b249-8f47c3cb02b1>

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
    val listUsers = withContext(Dispatchers.IO) {
        userClient.listUsers(
            limit = 10, /* Defaults to 200 on backend API server */
            cursor = null // OPTIONAL: user ID which will act as beginning cursor for this paginated fetch. Null if fetching the first list of user(s).
        )
    }

    // Resolve `listUsers` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Ban User

This function toggles the specified user's `banned` flag.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#211d5614-b251-4815-bf76-d8f6f66f97ab>

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
    val bannedUser = withContext(Dispatchers.IO) {
        userClient.setBanStatus(
            userid = "023976080242ac120002",
            banned = true // If set to true, attempt to ban the user. If set to false, attempt to remove the ban from user
        )
    }

    // Resolve `bannedUser` from HERE onwards(ex. update UI displaying the response data)...
}

```
