# User Client

## Create or Update User

Invoke this function if you want to create a user or update an existing user.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#8cc680a6-6ce8-4af7-ab1e-e793a7f0e7d2>

Below is a code sample on how to use this SDK feature:

```kotlin
val userClient = SportsTalk247.UserClient(
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
val userClient = SportsTalk247.UserClient(
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
val userClient = SportsTalk247.UserClient(
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
            cursor = null // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of user(s).
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
val userClient = SportsTalk247.UserClient(
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

## Global Purge User

This function will purge all chat content published by the specified user.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#c36d94e2-4fd9-4c9f-8009-f1d8ae9da6f5>

Below is a code sample on how to use this SDK feature:

```kotlin
val userClient = SportsTalk247.UserClient(
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
    val response = withContext(Dispatchers.IO) {
        userClient.globalPurge(
            userid = "023976080242ac120002",
            banned = true // If set to true, attempt to purge all the chat messages published by the specified user.
        )
    }

    // Resolve `response` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Shadow Ban User

This function toggles the specified user's `shadowbanned` flag.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#211a5696-59ce-4988-82c9-7c614cab3efb>

Below is a code sample on how to use this SDK feature:

```kotlin
val userClient = SportsTalk247.UserClient(
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
    val shadowBannedUser = withContext(Dispatchers.IO) {
        userClient.shadowBanUser(
            userId = "023976080242ac120002",
            shadowban = true, // If set to true, user can send messages into a chat room, however those messages are flagged as shadow banned.
            expireseconds = 3600 // [OPTIONAL]: Duration of shadowban value in seconds. If specified, the shadow ban will be lifted when this time is reached. If not specified, shadowban remains until explicitly lifted. Maximum seconds is a double byte value.

        )
    }

    // Resolve `shadowBannedUser` from HERE onwards(ex. update UI displaying the response data)...
}

```

## Search User(s)

This function searches the users in an app.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#dea07871-86bb-4c12-bef3-d7290d762a06>

Below is a code sample on how to use this SDK feature:

```kotlin
val userClient = SportsTalk247.UserClient(
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
    // Search by Handle
    val searchedUsersByHandle = withContext(Dispatchers.IO) {
        userClient.searchUsers(
            handle = "testhandle1",
            limit = 20, // Defaults to 200 on backend API server
            cursor = null   // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of user(s).
        )
    }

    // Search by Name
    val searchedUsersByName = withContext(Dispatchers.IO) {
        userClient.searchUsers(
            name = "Josie Rizal",
            limit = 20, // Defaults to 200 on backend API server
            cursor = null   // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of user(s).
        )
    }

    // Search by User ID
    val searchedUsersByUserId = withContext(Dispatchers.IO) {
        userClient.searchUsers(
            userid = "userid_georgew",
            limit = 20, // Defaults to 200 on backend API server
            cursor = null   // OPTIONAL: The cursor value from previous search attempt to indicate next paginated fetch. Null if fetching the first list of user(s).
        )
    }
}

```

## Delete User

This function will delete the specified user. All rooms with messages by that user will have the messages from this user purged in the rooms.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#ab387784-ad82-4025-bb3b-56659129279c>

Below is a code sample on how to use this SDK feature:

```kotlin
val userClient = SportsTalk247.UserClient(
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
    val deletedUser = withContext(Dispatchers.IO) {
        userClient.deleteUser(
            userid = "023976080242ac120002"
        )
    }

    // Resolve `deletedUser` from HERE onwards(ex. update UI displaying the response data)...
}

```
