# Comment Client

```kotlin
val commentClient = SportsTalk247.CommentClient(
   config = ClientConfig(
      appId = "c84cb9c852932a6b0411e75e", // This is just a sample app id
      apiToken = "5MGq3XbsspBEQf3kj154_OSQV-jygEKwHJyuHjuAeWHA", // This is just a sample token
      endpoint = "http://api.custom.endpoint/v3/" // This is just a sample API endpoint
   )
)
```

## Create / Update Conversation

Invoke this function if you want to create or update a conversation.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#e0a0a63c-6e68-49d4-ab5c-b6c19a173f06>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val createdConversation = withContext(Dispatchers.IO) {
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
            }

            // Resolve `createdConversation` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

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

## Get Conversation by ID

Invoke this function if you want to retrieve metadata about a conversation.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#b23cafdf-35ce-4edc-b073-1215595a9de0>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val conversation = withContext(Dispatchers.IO) {
                commentClient.getConversation(
                    conversationid = "test-conversation-id123"
                )
            }

            // Resolve `conversation` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.getConversation(
            conversationid = "test-conversation-id123"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { conversation ->
                // Resolve `conversation` (ex. Display prompt OR Update UI)
            }
```

## Find Conversation by CustomID

Invoke this function if you want to retrieve the conversation object by using the CustomID for the conversation supplied by the app.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#5c85f5cb-8bd0-4a9d-b78f-165bfc31a724>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val conversation = withContext(Dispatchers.IO) {
                commentClient.getConversationByCustomId(
                    customid = "/articles/2020-03-01/article1/something-very-important-happened"
                )
            }

            // Resolve `conversation` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.getConversationByCustomId(
            customid = "/articles/2020-03-01/article1/something-very-important-happened"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { conversation ->
                // Resolve `conversation` (ex. Display prompt OR Update UI)
            }
```

## List Conversations

Invoke this function if you want to Get a list of all conversations with optional filters.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#dd62cc9e-c3be-4826-831d-40783531adb4>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val conversations = withContext(Dispatchers.IO) {
                commentClient.listConversations()
            }

            // Resolve `conversations` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.listConversations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { conversations ->
                // Resolve `conversations` (ex. Display prompt OR Update UI)
            }
```

## Batch Get Conversation Details

Invoke this function if you want to retrieve a list of conversations and you want metadata about only those conversations so you can display things like like count or comment count making minimal requests.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#7d249f48-de31-4647-9102-527c64fe37bb>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val batchOfConversations = withContext(Dispatchers.IO) {
                commentClient.batchGetConversationDetails(
                    ids = listOf("test-conversation-id123", "test-conversation-id456", "test-conversation-id789"), // [OPTIONAL] List of conversation id(s)
                    cid = listOf(
                        "/articles/2020-03-01/article1/something-very-important-happened",
                        "/articles/2020-03-01/article1/something-not-so-important-happened",
                        "/articles/2020-03-01/article1/something-okayish-happened",
                    ), // [OPTIONAL] List of conversation customid(s)
                    entities = listOf("reactions", "likecount"),    // [OPTIONAL] One of the string constants("reactions", "likecount", or "commentcount")
                )
            }

            // Resolve `batchOfConversations` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.batchGetConversationDetails(
            ids = listOf("test-conversation-id123", "test-conversation-id456", "test-conversation-id789"), // [OPTIONAL] List of conversation id(s)
            cid = listOf(
                "/articles/2020-03-01/article1/something-very-important-happened",
                "/articles/2020-03-01/article1/something-not-so-important-happened",
                "/articles/2020-03-01/article1/something-okayish-happened",
            ), // [OPTIONAL] List of conversation customid(s)
            entities = listOf("reactions", "likecount"),    // [OPTIONAL] One of the string constants("reactions", "likecount", or "commentcount")
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { batchOfConversations ->
                // Resolve `batchOfConversations` (ex. Display prompt OR Update UI)
            }
```

## React To Conversation Topic ("Like")

Invoke this function if you want to either react to the content itself (for example to LIKE an article/video/poll) or you can use the comment react api to react to an individual comment

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#e25d90d0-9a4b-4a45-839f-0242f960c6b3>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val reactComment = withContext(Dispatchers.IO) {
                commentClient.reactToConversationTopic(
                    conversationid = "test-conversation-id123",
                    request = ReactToConversationTopicRequest(
                        userid = "test-user-id123", // ID of the User who is attempting to react on the comment
                        reaction = ReactionType.LIKE,   // Any arbitratry reaction string constant(i.e. "like", etc.)
                        reacted = true,
                    )
                )
            }

            // Resolve `reactComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.reactToConversationTopic(
            conversationid = "test-conversation-id123",
            request = ReactToConversationTopicRequest(
                userid = "test-user-id123", // ID of the User who is attempting to react on the comment
                reaction = ReactionType.LIKE,   // Any arbitratry reaction string constant(i.e. "like", etc.)
                reacted = true,
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { reactComment ->
                // Resolve `reactComment` (ex. Display prompt OR Update UI)
            }
```

## Create and Publish Comment

Invoke this function if you want to create a comment and publishes it. You can optionally make this comment into a reply by passing in the optional replyto field. Custom fields can be set, and can be overwritten.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#1a6e6c69-c904-458e-ac87-c215091db098>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val createdComment = withContext(Dispatchers.IO) {
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
            }

            // Resolve `createdComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

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

## Reply to Comment

Invoke this function if you want to reply to a specific comment. The reply to comment method is the same as the create comment method, except you pass in the ID of the parent comment using the replyto field.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#c0ccaa52-ac7c-4424-8f27-58cc58812001>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val commentReply = withContext(Dispatchers.IO) {
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
            }

            // Resolve `commentReply` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

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

## List Replies

Invoke this function if you want to retrieve a list of replies from a specific comment. This method works the same way as the List Comments method, so view the documentation on that method. 
The difference is that this method will filter to only include comments that have a parent.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#71e7a205-471a-4554-9897-da45a8b671ee>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val commentReplies = withContext(Dispatchers.IO) {
                commentClient.listReplies(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123", // ID of the parent comment
                    cursor = null, // OPTIONAL
                    limit = null, // OPTIONAL
                    direction = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
                    sort = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies"
                    includechildren = true, // (optional, default is false) If false, this returns all reply nodes that are immediate children of the provided parent id. If true, it includes all replies under the parent id and all the children of those replies and so on.
                    includeinactive = true, // (optional, default is false) If true, return comments that are inactive (for example, disabled by moderation)
                )
            }

            // Resolve `commentReplies` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.listReplies(
            conversationid = "test-conversation-id123",
            commentid = "test-comment-id123", // ID of the parent comment
            cursor = null, // OPTIONAL
            limit = null, // OPTIONAL
            direction = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
            sort = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies"
            includechildren = true, // (optional, default is false) If false, this returns all reply nodes that are immediate children of the provided parent id. If true, it includes all replies under the parent id and all the children of those replies and so on.
            includeinactive = true, // (optional, default is false) If true, return comments that are inactive (for example, disabled by moderation)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { commentReplies ->
                // Resolve `commentReplies` (ex. Display prompt OR Update UI)
            }
```

## Get Comment by ID

Invoke this function if you want to retrieve a specific comment.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#b7c10a98-f5cd-4ed5-8fcc-aa3440cd4233>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val comment = withContext(Dispatchers.IO) {
                commentClient.getComment(
                    commentid = "test-comment-id123",
                )
            }

            // Resolve `comment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.getComment(
            commentid = "test-comment-id123",
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { comment ->
                // Resolve `comment` (ex. Display prompt OR Update UI)
            }
```

## List Comments

Invoke this function if you want to Get a list of comments within a conversation.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#98744685-35c9-4293-a082-594cb7a6ec76>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val comments = withContext(Dispatchers.IO) {
                commentClient.listComments(
                    conversationid = "test-conversation-id123",
                    cursor = null, // OPTIONAL
                    limit = null, // OPTIONAL
                    direction = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
                    sort = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies"
                    includechildren = true, // (optional, default is false) If false, this returns all reply nodes that are immediate children of the provided parent id. If true, it includes all replies under the parent id and all the children of those replies and so on.
                    includeinactive = true, // (optional, default is false) If true, return comments that are inactive (for example, disabled by moderation)
                )
            }

            // Resolve `comments` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.listComments(
            conversationid = "test-conversation-id123",
            cursor = null, // OPTIONAL
            limit = null, // OPTIONAL
            direction = null, // OPTIONAL, defaults to "forward", Must be "forward" or "backward"
            sort = null, // OPTIONAL, defaults to "oldest", Either "oldest", "newest", "likes", "votescore", "mostreplies"
            includechildren = true, // (optional, default is false) If false, this returns all reply nodes that are immediate children of the provided parent id. If true, it includes all replies under the parent id and all the children of those replies and so on.
            includeinactive = true, // (optional, default is false) If true, return comments that are inactive (for example, disabled by moderation)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { comments ->
                // Resolve `comments` (ex. Display prompt OR Update UI)
            }
```

## List Replies Batch

Invoke this function if you want to get a list of replies to multiple parent comments quickly, in 1 request.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#b2d397cf-5d25-4752-b96e-71374b4799bb>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val batchCommentReplies = withContext(Dispatchers.IO) {
                commentClient.listRepliesBatch(
                    conversationid = "test-conversation-id123",
                    cursor = null, // OPTIONAL
                    childlimit = 50,    // (Optional, default = 50).
                    parentids = listOf("test-comment-id123", "test-comment-id456"), //  (Required). A list of parent comment ID(s) to which comment replies will be fetched from.
                    includeinactive = true, // (optional, default is false) If true, return comments that are inactive (for example, disabled by moderation)
                )
            }

            // Resolve `batchCommentReplies` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.listRepliesBatch(
            conversationid = "test-conversation-id123",
            cursor = null, // OPTIONAL
            childlimit = 50,    // (Optional, default = 50).
            parentids = listOf("test-comment-id123", "test-comment-id456"), //  (Required). A list of parent comment ID(s) to which comment replies will be fetched from.
            includeinactive = true, // (optional, default is false) If true, return comments that are inactive (for example, disabled by moderation)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { batchCommentReplies ->
                // Resolve `batchCommentReplies` (ex. Display prompt OR Update UI)
            }
```

## React To Comment ("Like")

Invoke this function if you want to add or remove a reaction to a comment.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#df659fc4-0bb8-4d93-845d-c61579a1f0f8>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val reactedComment = withContext(Dispatchers.IO) {
                commentClient.reactToComment(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123"
                    request = ReactToCommentRequest(
                        userid = "test-user-id123", // ID of the User who is attempting to react on the comment
                        reaction = ReactionType.LIKE,   // Any arbitratry reaction string constant(i.e. "like", etc.)
                        reacted = true,
                    )
                )
            }

            // Resolve `reactedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.reactToComment(
            conversationid = "test-conversation-id123",
            commentid = "test-comment-id123"
            request = ReactToCommentRequest(
                userid = "test-user-id123", // ID of the User who is attempting to react on the comment
                reaction = ReactionType.LIKE,   // Any arbitratry reaction string constant(i.e. "like", etc.)
                reacted = true,
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { reactedComment ->
                // Resolve `reactedComment` (ex. Display prompt OR Update UI)
            }
```

## Vote on Comment

Invoke this function if you want to UPVOTE, DOWNVOTE, or REMOVE VOTE from a comment.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#82ffbc3a-01fe-4f1d-a7b1-62440179dfa5>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val votedComment = withContext(Dispatchers.IO) {
                commentClient.voteOnComment(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123"
                    request = VoteOnCommentRequest(
                        vote = VoteType.Up,
                        userid = "test-user-id123", // ID of the User who is attempting to vote on the comment
                    )
                )
            }

            // Resolve `votedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.voteOnComment(
            conversationid = "test-conversation-id123",
            commentid = "test-comment-id123"
            request = VoteOnCommentRequest(
                vote = VoteType.Up,
                userid = "test-user-id123", // ID of the User who is attempting to vote on the comment
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { votedComment ->
                // Resolve `votedComment` (ex. Display prompt OR Update UI)
            }
```

## Report Comment

Invoke this function if you want to REPORT a comment to the moderation team.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#8b48a6e4-8fda-4a3d-b172-f4db50fb0a40>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val reportedComment = withContext(Dispatchers.IO) {
                commentClient.reportComment(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123"
                    request = ReportCommentRequest(
                        userid = "test-user-id123", // ID of the User who is attempting to report the comment
                        reporttype = ReportType.ABUSE,
                    )
                )
            }

            // Resolve `reportedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.reportComment(
            conversationid = "test-conversation-id123",
            commentid = "test-comment-id123"
            request = ReportCommentRequest(
                userid = "test-user-id123", // ID of the User who is attempting to report the comment
                reporttype = ReportType.ABUSE,
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { reportedComment ->
                // Resolve `reportedComment` (ex. Display prompt OR Update UI)
            }
```

## Update Comment

Invoke this function if you want to UPDATE the contents of an existing comment.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#b02ee426-5a84-4203-93b4-989ad43fe227>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val updatedComment = withContext(Dispatchers.IO) {
                commentClient.updateComment(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123"
                    request = UpdateCommentRequest(
                        userid = "test-user-id123", // ID of the User who is attempting to update the comment
                        body = "Hello UPDATED comment!",
                    )
                )
            }

            // Resolve `updatedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.updateComment(
            conversationid = "test-conversation-id123",
            commentid = "test-comment-id123"
            request = UpdateCommentRequest(
                userid = "test-user-id123", // ID of the User who is attempting to update the comment
                body = "Hello UPDATED comment!",
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { updatedComment ->
                // Resolve `updatedComment` (ex. Display prompt OR Update UI)
            }
```

## Flag Comment As Deleted

Invoke this function if you want to logically delete comment(body becomes "(comment deleted)" and deleted = true).

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#5e14e5ea-e8b6-46e0-9cb8-263f695ea652>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val logicallyDeletedComment = withContext(Dispatchers.IO) {
                commentClient.flagCommentLogicallyDeleted(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123"
                    userid = "test-user-id123", // ID of the User who is attempting to logically delete the comment
                    deleted = true,
                    permanentifnoreplies = false,   // If not provided, defaults to false.
                )
            }

            // Resolve `logicallyDeletedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.flagCommentLogicallyDeleted(
            conversationid = "test-conversation-id123",
            commentid = "test-comment-id123"
            userid = "test-user-id123", // ID of the User who is attempting to logically delete the comment
            deleted = true,
            permanentifnoreplies = false,   // If not provided, defaults to false.
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { logicallyDeletedComment ->
                // Resolve `logicallyDeletedComment` (ex. Display prompt OR Update UI)
            }
```

## Delete Comment (permanent)

Invoke this function if you want to DELETE a comment and all replies to that comment.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#2b353c85-c5b7-4065-a14d-3960976f91a6>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val deletedComment = withContext(Dispatchers.IO) {
                commentClient.permanentlyDeleteComment(
                    conversationid = "test-conversation-id123",
                    commentid = "test-comment-id123",
                )
            }

            // Resolve `deletedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

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

## List Comments in Moderation Queue

Invoke this function if you want to list all the comments in the moderation queue.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#d98100c4-6be8-415c-9c08-f6bcbc039566>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val commentsInModerationQueue = withContext(Dispatchers.IO) {
                commentClient.listCommentsInModerationQueue(
                    conversationid = "test-conversation-id123",  // OPTIONAL, limit to comments under specified conversation ID
                    limit = null, // OPTIONAL
                    cursor = null, // OPTIONAL
                    filterHandle = null, // OPTIONAL
                    filterKeyword = null, // OPTIONAL
                    filterModerationState = CommentFilterModerationState.Approved, // OPTIONAL
                )
            }

            // Resolve `commentsInModerationQueue` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.listCommentsInModerationQueue(
            conversationid = "test-conversation-id123",  // OPTIONAL, limit to comments under specified conversation ID
            limit = null, // OPTIONAL
            cursor = null, // OPTIONAL
            filterHandle = null, // OPTIONAL
            filterKeyword = null, // OPTIONAL
            filterModerationState = CommentFilterModerationState.Approved, // OPTIONAL
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { commentsInModerationQueue ->
                // Resolve `commentsInModerationQueue` (ex. Display prompt OR Update UI)
            }
```

## Approve Message in Queue

Invoke this function if you want to APPROVE/REJECT a message in the moderation queue.

Refer to the SportsTalk API Documentation for more details:

<https://apiref.sportstalk247.com/?version=latest#71eb7668-f9d1-4ecf-9e07-1f64699ff071>

Below is a code sample on how to use this SDK feature:

``` tabs::

    .. code-tab:: kotlin sdk-coroutine

        // Launch thru coroutine block
        // https://developer.android.com/topic/libraries/architecture/coroutines
        lifecycleScope.launch {
            // Switch to IO Coroutine Context(Operation will be executed on IO Thread)
            val approvedComment = withContext(Dispatchers.IO) {
                commentClient.approveMessageInQueue(
                    commentid = "test-comment-id123",
                    request = ApproveMessageRequest(
                        approve = true  // Set true to APPROVE. Otherwise, false to REJECT.
                    )
                )
            }

            // Resolve `approvedComment` from HERE onwards(ex. update UI displaying the response data)...
        }

    .. code-tab:: kotlin sdk-reactive-rx2

        val rxDisposeBag = CompositeDisposable()

        commentClient.approveMessageInQueue(
            commentid = "test-comment-id123",
            request = ApproveMessageRequest(
                approve = true  // Set true to APPROVE. Otherwise, false to REJECT.
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe { approvedComment ->
                // Resolve `approvedComment` (ex. Display prompt OR Update UI)
            }
```

