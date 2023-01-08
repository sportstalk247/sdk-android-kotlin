package com.sportstalk.coroutine.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.coroutine.ServiceFactory
import com.sportstalk.datamodels.DateUtils
import com.sportstalk.datamodels.*
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.chat.moderation.*
import com.sportstalk.datamodels.users.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import net.bytebuddy.utility.RandomString
import org.junit.*
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random
import kotlin.test.assertTrue

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.KITKAT])
class ChatModerationServiceTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var userService: UserService
    private lateinit var chatService: ChatService
    private lateinit var chatModerationService: ChatModerationService
    private lateinit var json: Json

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val thrown = ExpectedException.none()

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get().applicationContext
        val appInfo =
                try {
                    context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                } catch (err: Throwable) {
                    err.printStackTrace()
                    null
                }

        config = ClientConfig(
            appId = "602e6fc50c916c171cb9a4e8",
            apiToken = "P1slSgD5l0yYBTWixyZ3_gGt69p5SOu0KEuGYLBXY8sw",
            endpoint = "https://api.sportstalk247.com/api/v3"
        )
        json = ServiceFactory.RestApi.json
        userService = ServiceFactory.User.get(config)
        chatService = ServiceFactory.Chat.get(config)
        chatModerationService = ServiceFactory.ChatModeration.get(config)

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp() {
        testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private suspend fun deleteTestUsers(vararg userIds: String?) {
        for (id in userIds) {
            id ?: continue
            userService.deleteUser(userId = id)
        }
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private suspend fun deleteTestChatRooms(vararg chatRoomIds: String?) {
        for (id in chatRoomIds) {
            id ?: continue
            chatService.deleteRoom(chatRoomId = id)
        }
    }

    @Test
    fun `0-ERROR-403) Request is not authorized with a token`() = runBlocking {
        val userCaseChatModService = ServiceFactory.ChatModeration.get(
                config.copy(
                        apiToken = "not-a-valid-auth-api-token"
                )
        )

        // GIVEN
        val testInputRequest = CreateChatRoomRequest(
                /*userid = "NON-Existing-User-ID"*/
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userCaseChatModService.approveMessage(
                        eventId = "non-existing-event-id",
                        approve = true
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-403 - Request is not authorized with a token`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "Request is not authorized with a token." }
            assertTrue { err.code == 403 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `A) Approve Message`() = runBlocking {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).speech!!

        val testInputRequest = ApproveMessageRequest(
                approve = true
        )
        val testExpectedResult = testSendMessageData.copy(
                moderation = "approved"
        )

        // WHEN
        val testActualResult = chatModerationService.approveMessage(
                eventId = testSendMessageData.id!!,
                approve = testInputRequest.approve
        )

        // THEN
        println(
                "`Approve Message`() -> testActualResult = \n" +
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
    fun `A-ERROR-404) Approve Message`() = runBlocking {

        // GIVEN
        val testInputNonExistingEventId = "non-existing-event-id"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatModerationService.approveMessage(
                        eventId = testInputNonExistingEventId,
                        approve = true
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Approve Message`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified event was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `B) List Messages Needing Moderation`() = runBlocking {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).speech!!

        val testInputRequest = ApproveMessageRequest(
                approve = true
        )
        val testExpectedResult = ListMessagesNeedingModerationResponse(
                kind = Kind.CHAT_LIST,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatModerationService.listMessagesNeedingModeration()

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
                        handle = "handle_test1_${TestData.USER_HANDLE_RANDOM_NUM.nextInt(99)}",
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