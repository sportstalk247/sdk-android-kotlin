package com.sportstalk.reactive.rx2.service

import android.app.Activity
import android.content.Context
import android.os.Build
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.DateUtils
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.chat.moderation.ApproveMessageRequest
import com.sportstalk.datamodels.chat.moderation.ListMessagesNeedingModerationResponse
import com.sportstalk.datamodels.users.CreateUpdateUserRequest
import com.sportstalk.datamodels.users.User
import com.sportstalk.reactive.rx2.ServiceFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import kotlinx.serialization.json.Json
import net.bytebuddy.utility.RandomString
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.test.assertTrue
import kotlin.test.fail

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class ChatModerationServiceTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var userService: UserService
    private lateinit var chatService: ChatService
    private lateinit var chatModerationService: ChatModerationService
    private lateinit var json: Json

    private lateinit var rxDisposeBag: CompositeDisposable

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get().applicationContext

        config = ClientConfig(
            appId = "63c16f13c3e89411881ba085",
            apiToken = "cXSVhVOVYEewANzl7CuoWgw08gtq8FTUS4nxI_pHcQKg",
            endpoint = "https://api.sportstalk247.com/api/v3"
        )
        json = ServiceFactory.RestApi.json
        userService = ServiceFactory.User.get(config)
        chatService = ServiceFactory.Chat.get(config)
        chatModerationService = ServiceFactory.ChatModeration.get(config)

        rxDisposeBag = CompositeDisposable()
    }

    @After
    fun cleanUp() {
        rxDisposeBag.dispose()
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private fun deleteTestUsers(vararg userIds: String?) {
        for (id in userIds) {
            id ?: continue
            try {
                userService.deleteUser(userId = id).blockingGet()
            } catch (err: Throwable) {
            }
        }
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private fun deleteTestChatRooms(vararg chatRoomIds: String?) {
        for (id in chatRoomIds) {
            id ?: continue
            try {
                chatService.deleteRoom(chatRoomId = id).blockingGet()
            } catch (err: Throwable) {
            }
        }
    }

    @Test
    fun `0-ERROR-403) Request is not authorized with a token`() {
        val userCaseChatService = ServiceFactory.Chat.get(
            config.copy(
                apiToken = "not-a-valid-auth-api-token"
            )
        )

        // GIVEN
        val testInputRequest = CreateChatRoomRequest(
            /*userid = "NON-Existing-User-ID"*/
        )
        val createRoom = TestObserver<ChatRoom>()

        // WHEN
        userCaseChatService.createRoom(request = testInputRequest)
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe(createRoom)

        // THEN
        createRoom
            .assertError {
                val err = it as? SportsTalkException ?: run {
                    fail()
                }

                println(
                    "`ERROR-403 - Request is not authorized with a token`() -> testActualResult = \n" +
                            json.encodeToString(
                                SportsTalkException.serializer(),
                                err
                            )
                )

                return@assertError err.kind == Kind.API
                        && err.message == "Request is not authorized with a token."
                        && err.code == 401
            }
    }

    @Test
    fun `A-1) Approve Message - Pre-moderated - Approved`() {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${Random.nextInt(100, 999)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService
            .createOrUpdateUser(request = testCreateUserInputRequest)
            .blockingGet()

        val testChatRoomData = TestData.chatRooms(config.appId).first()
            // Moderation MUST BE SET to "pre"
            .copy(moderation = "pre")
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
            name = testChatRoomData.name!!,
            customid = testChatRoomData.customid,
            description = testChatRoomData.description,
            moderation = ModerationType.pre/*testChatRoomData.moderation*/, // "pre"-moderated
            enableactions = testChatRoomData.enableactions,
            enableenterandexit = testChatRoomData.enableenterandexit,
            enableprofanityfilter = testChatRoomData.enableprofanityfilter,
            delaymessageseconds = testChatRoomData.delaymessageseconds,
            roomisopen = testChatRoomData.open,
            maxreports = testChatRoomData.maxreports,
            customtags = listOf("tagA", "tagB")
        )
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService
            .createRoom(testCreateChatRoomInputRequest)
            .blockingGet()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
            chatRoomId = testInputJoinChatRoomId,
            request = testJoinRoomInputRequest
        )
            .delay(300, TimeUnit.MILLISECONDS)
            .blockingGet()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
            command = "Yow Jessy, how are you doin'?",
            userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
            chatRoomId = testCreatedChatRoomData.id!!,
            request = testInitialSendMessageInputRequest
        )
            .delay(300, TimeUnit.MILLISECONDS)
            .blockingGet()
            .speech!!

        assertTrue { testSendMessageData.moderation == ModerationType.pending }

        val testInputRequest = ApproveMessageRequest(
            approve = true
        )
        val testExpectedResult = testSendMessageData.copy(
            moderation = ModerationType.approved
        )

        // WHEN
        val testActualResult = chatModerationService.approveMessage(
            eventId = testSendMessageData.id!!,
            approve = testInputRequest.approve
        )
            .delay(1000, TimeUnit.MILLISECONDS)
            .blockingGet()

        // THEN
        println(
            "`Approve Message - Pre-moderated - Approved`() -> testActualResult = \n" +
                    json.encodeToString(
                        ChatEvent.serializer(),
                        testActualResult
                    )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.body == testExpectedResult.body }
        assertTrue { testActualResult.eventtype == testExpectedResult.eventtype }
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.moderation == testExpectedResult.moderation }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)

    }

    @Test
    fun `A-1) Approve Message - Pre-moderated - Rejected`() {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${Random.nextInt(100, 999)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService
            .createOrUpdateUser(request = testCreateUserInputRequest)
            .blockingGet()

        val testChatRoomData = TestData.chatRooms(config.appId).first()
            // Moderation MUST BE SET to "pre"
            .copy(moderation = "pre")
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
            name = testChatRoomData.name!!,
            customid = testChatRoomData.customid,
            description = testChatRoomData.description,
            moderation = ModerationType.pre/*testChatRoomData.moderation*/, // "pre"-moderated
            enableactions = testChatRoomData.enableactions,
            enableenterandexit = testChatRoomData.enableenterandexit,
            enableprofanityfilter = testChatRoomData.enableprofanityfilter,
            delaymessageseconds = testChatRoomData.delaymessageseconds,
            roomisopen = testChatRoomData.open,
            maxreports = testChatRoomData.maxreports,
            customtags = listOf("tagA", "tagB")
        )
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService
            .createRoom(testCreateChatRoomInputRequest)
            .blockingGet()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
            chatRoomId = testInputJoinChatRoomId,
            request = testJoinRoomInputRequest
        )
            .delay(300, TimeUnit.MILLISECONDS)
            .blockingGet()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
            command = "Yow Jessy, how are you doin'?",
            userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
            chatRoomId = testCreatedChatRoomData.id!!,
            request = testInitialSendMessageInputRequest
        )
            .delay(300, TimeUnit.MILLISECONDS)
            .blockingGet()
            .speech!!

        assertTrue { testSendMessageData.moderation == ModerationType.pending }

        val testInputRequest = ApproveMessageRequest(
            approve = false
        )
        val testExpectedResult = testSendMessageData.copy(
            moderation = ModerationType.rejected
        )

        // WHEN
        val testActualResult = chatModerationService.approveMessage(
            eventId = testSendMessageData.id!!,
            approve = testInputRequest.approve
        )
            .delay(1500, TimeUnit.MILLISECONDS)
            .blockingGet()

        // THEN
        println(
            "`Approve Message - Pre-moderated - Approved`() -> testActualResult = \n" +
                    json.encodeToString(
                        ChatEvent.serializer(),
                        testActualResult
                    )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.body == testExpectedResult.body }
        assertTrue { testActualResult.eventtype == testExpectedResult.eventtype }
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.moderation == testExpectedResult.moderation }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)

    }

    @Test
    fun `A-ERROR-404) Approve Message`() {

        // GIVEN
        val testInputNonExistingEventId = "non-existing-event-id"
        val approveMessage = TestObserver<ChatEvent>()

        // THEN
        chatModerationService.approveMessage(
            eventId = testInputNonExistingEventId,
            approve = true,
        )
            .doOnSubscribe { rxDisposeBag.add(it) }
            .subscribe(approveMessage)

        // WHEN
        approveMessage.assertError {
            val err = it as? SportsTalkException ?: run {
                fail()
            }

            println(
                "`ERROR-404 - Approve Message`() -> testActualResult = \n" +
                        json.encodeToString(
                            SportsTalkException.serializer(),
                            err
                        )
            )

            return@assertError err.kind == Kind.API
                    && err.message == "The specified event was not found."
                    && err.code == 404
        }

    }

    @Test
    fun `A-ERROR-400) Approve Message - Not in a Moderatable State`() {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${Random.nextInt(100, 999)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )

        var testCreatedUserData: User? = null
        var testCreatedChatRoomData: ChatRoom? = null
        try {
            // Should create a test user first
            testCreatedUserData =
                userService.createOrUpdateUser(request = testCreateUserInputRequest)
                    .blockingGet()

            val testChatRoomData = TestData.chatRooms(config.appId).first()
                // Moderation MUST BE SET to "pre"
                .copy(moderation = "pre")
            val testCreateChatRoomInputRequest = CreateChatRoomRequest(
                name = testChatRoomData.name!!,
                customid = testChatRoomData.customid,
                description = testChatRoomData.description,
                moderation = ModerationType.post/*testChatRoomData.moderation*/, // "post"-moderated
                enableactions = testChatRoomData.enableactions,
                enableenterandexit = testChatRoomData.enableenterandexit,
                enableprofanityfilter = testChatRoomData.enableprofanityfilter,
                delaymessageseconds = testChatRoomData.delaymessageseconds,
                roomisopen = testChatRoomData.open,
                maxreports = testChatRoomData.maxreports
            )
            // Should create a test chat room first
            testCreatedChatRoomData =
                chatService
                    .createRoom(testCreateChatRoomInputRequest)
                    .blockingGet()

            val testInputChatRoomId = testCreatedChatRoomData.id!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!,
                handle = testCreatedUserData.handle!!
            )
            // Test Created User Should join test created chat room
            chatService.joinRoom(
                chatRoomId = testInputChatRoomId,
                request = testJoinRoomInputRequest
            )
                .delay(300L, TimeUnit.MILLISECONDS )
                .blockingGet()

            val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
            )
            // Test Created User Should send a message to the created chat room
            val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
            )
                .delay(300L, TimeUnit.MILLISECONDS)
                .blockingGet()
                .speech!!

            val testInputRequest = ApproveMessageRequest(
                approve = true
            )
            val testExpectedResult = testSendMessageData.copy(
                moderation = ModerationType.na
            )
            val approveMessage = TestObserver<ChatEvent>()

            // THEN
            chatModerationService.approveMessage(
                eventId = testSendMessageData.id!!,
                approve = testInputRequest.approve,
            )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnDispose {
                    // Perform Delete Test Chat Room
                    deleteTestChatRooms(testCreatedChatRoomData?.id)
                    // Perform Delete Test User
                    deleteTestUsers(testCreatedUserData?.userid)
                }
                .subscribe(approveMessage)

            approveMessage
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                        "`A-ERROR-400) Approve Message - Not in a Moderatable State`() -> testActualResult = \n" +
                                json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                                )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified event  is not in a moderatable state."
                            && err.code == 400
                }

        } catch (err: SportsTalkException) {
            err.printStackTrace()

            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData?.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData?.userid)

            fail(err.message)
        }
    }

    @Test
    fun `B) List Messages Needing Moderation`() {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = testUserData.handle,
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService
            .createOrUpdateUser(request = testCreateUserInputRequest)
            .blockingGet()

        val testChatRoomData = TestData.chatRooms(config.appId).first()
            // Moderation MUST BE SET to "pre"
            .copy(moderation = "pre")
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
            name = testChatRoomData.name!!,
            customid = testChatRoomData.customid,
            description = testChatRoomData.description,
            moderation = testChatRoomData.moderation,
            enableactions = testChatRoomData.enableactions,
            enableenterandexit = testChatRoomData.enableenterandexit,
            enableprofanityfilter = testChatRoomData.enableprofanityfilter,
            delaymessageseconds = testChatRoomData.delaymessageseconds,
            roomisopen = testChatRoomData.open,
            maxreports = testChatRoomData.maxreports
        )
        // Should create a test chat room first
        val testCreatedChatRoomData =
            chatService.createRoom(testCreateChatRoomInputRequest)
                .blockingGet()

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
            chatRoomId = testInputChatRoomId,
            request = testJoinRoomInputRequest
        )
            .delay(300L, TimeUnit.MILLISECONDS)
            .blockingGet()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
            command = "Yow Jessy, how are you doin'?",
            userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
            chatRoomId = testCreatedChatRoomData.id!!,
            request = testInitialSendMessageInputRequest
        )
            .delay(300L, TimeUnit.MILLISECONDS)
            .blockingGet()
            .speech!!

        val testInputRequest = ApproveMessageRequest(
            approve = true
        )
        val testExpectedResult = ListMessagesNeedingModerationResponse(
            kind = Kind.CHAT_LIST,
            events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatModerationService
            .listMessagesNeedingModeration()
            .delay(300L, TimeUnit.MILLISECONDS)
            .blockingGet()

        // THEN
        println(
            "`List Messages Needing Moderation`() -> testActualResult = \n" +
                    json.encodeToString(
                        ListMessagesNeedingModerationResponse.serializer(),
                        testActualResult
                    )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue {
            testActualResult.events.any { ev ->
                ev.id == testSendMessageData.id
                        && ev.userid == testSendMessageData.userid
                        && ev.body == testSendMessageData.body
                        && ev.eventtype == testSendMessageData.eventtype
            }
        }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    object TestData {

        private val USER_HANDLE_RANDOM_NUM = Random(System.currentTimeMillis())

        val users = listOf(
            User(
                kind = Kind.USER,
                userid = RandomString.make(16),
                handle = "handle_test1_${USER_HANDLE_RANDOM_NUM.nextInt(99)}",
                displayname = "Test 1",
                pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
            ),
            User(
                kind = Kind.USER,
                userid = RandomString.make(16),
                handle = "handle_test2_${USER_HANDLE_RANDOM_NUM.nextInt(99)}",
                displayname = "Test 2",
                pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
            ),
            User(
                kind = Kind.USER,
                userid = RandomString.make(16),
                handle = "handle_test3_${USER_HANDLE_RANDOM_NUM.nextInt(99)}",
                displayname = "Test 3",
                pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
            ),
            User(
                kind = Kind.USER,
                userid = RandomString.make(16),
                handle = "handle_test4_${USER_HANDLE_RANDOM_NUM.nextInt(99)}",
                displayname = "Test 4",
                pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
            )
        )

        var _chatRooms: List<ChatRoom>? = null
        fun chatRooms(appId: String): List<ChatRoom> =
            if (_chatRooms != null) _chatRooms!!
            else listOf(
                ChatRoom(
                    kind = Kind.ROOM,
                    id = RandomString.make(16),
                    appid = appId,
                    ownerid = null,
                    name = "Test Chat Room 1",
                    description = "This is a test chat room 1.",
                    customtype = null,
                    customid = "test-room-1",
                    custompayload = null,
                    customtags = listOf(),
                    customfield1 = null,
                    customfield2 = null,
                    enableactions = true,
                    enableenterandexit = true,
                    open = true,
                    inroom = 1,
                    added = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    whenmodified = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    moderation = "post",
                    maxreports = 0L,
                    enableprofanityfilter = true,
                    delaymessageseconds = 0L
                ),
                ChatRoom(
                    kind = Kind.ROOM,
                    id = RandomString.make(16),
                    appid = appId,
                    ownerid = null,
                    name = "Test Chat Room 2",
                    description = "This is a test chat room 2.",
                    customtype = null,
                    customid = "test-room-2",
                    custompayload = null,
                    customtags = listOf(),
                    customfield1 = null,
                    customfield2 = null,
                    enableactions = false,
                    enableenterandexit = false,
                    open = false,
                    inroom = 1,
                    added = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    whenmodified = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    moderation = "post",
                    maxreports = 0L,
                    enableprofanityfilter = false,
                    delaymessageseconds = 0L
                ),
                ChatRoom(
                    kind = Kind.ROOM,
                    id = RandomString.make(16),
                    appid = appId,
                    ownerid = null,
                    name = "Test Chat Room 3",
                    description = "This is a test chat room 3.",
                    customtype = null,
                    customid = "test-room-3",
                    custompayload = null,
                    customtags = listOf(),
                    customfield1 = null,
                    customfield2 = null,
                    enableactions = true,
                    enableenterandexit = true,
                    open = false,
                    inroom = 1,
                    added = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    whenmodified = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    moderation = "post",
                    maxreports = 0L,
                    enableprofanityfilter = false,
                    delaymessageseconds = 0L
                ),
                ChatRoom(
                    kind = Kind.ROOM,
                    id = RandomString.make(16),
                    appid = appId,
                    ownerid = null,
                    name = "Test Chat Room 4",
                    description = "This is a test chat room 4.",
                    customtype = null,
                    customid = "test-room-4",
                    custompayload = null,
                    customtags = listOf(),
                    customfield1 = null,
                    customfield2 = null,
                    enableactions = false,
                    enableenterandexit = false,
                    open = true,
                    inroom = 1,
                    added = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    whenmodified = DateUtils.toUtcISODateTime(System.currentTimeMillis()),
                    moderation = "post",
                    maxreports = 0L,
                    enableprofanityfilter = true,
                    delaymessageseconds = 0L
                )
            )
    }

}