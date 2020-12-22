package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.datamodels.DateUtils
import com.sportstalk.ServiceFactory
import com.sportstalk.api.polling.coroutines.allEventUpdates
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.service.UserService
import com.sportstalk.datamodels.*
import com.sportstalk.datamodels.users.*
import com.sportstalk.datamodels.chat.*
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.builtins.ArraySerializer
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
import kotlin.test.fail

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.KITKAT])
class ChatServiceTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var userService: UserService
    private lateinit var chatService: ChatService
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
                appId = appInfo?.metaData?.getString("sportstalk.api.app_id")!!,
                apiToken = appInfo.metaData?.getString("sportstalk.api.auth_token")!!,
                endpoint = appInfo.metaData?.getString("sportstalk.api.url.endpoint")!!
        )
        json = ServiceFactory.RestApi.json
        userService = ServiceFactory.User.get(config)
        chatService = ServiceFactory.Chat.get(config)

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
            try {
                userService.deleteUser(userId = id)
            } catch (err: Throwable) {
            }
        }
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private suspend fun deleteTestChatRooms(vararg chatRoomIds: String?) {
        for (id in chatRoomIds) {
            id ?: continue
            try {
                chatService.deleteRoom(chatRoomId = id)
            } catch (err: Throwable) {
            }
        }
    }

    @Test
    fun `0-ERROR-403) Request is not authorized with a token`() = runBlocking {
        val userCaseChatService = ServiceFactory.Chat.get(
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
                userCaseChatService.createRoom(request = testInputRequest)
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
    fun `A) Create Room`() = runBlocking {
        // GIVEN
        val testExpectedData = TestData.chatRooms(config.appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testExpectedData.name!!,
                customid = testExpectedData.customid,
                description = testExpectedData.description,
                moderation = testExpectedData.moderation,
                enableactions = testExpectedData.enableactions,
                enableenterandexit = testExpectedData.enableenterandexit,
                enableprofanityfilter = testExpectedData.enableprofanityfilter,
                delaymessageseconds = testExpectedData.delaymessageseconds,
                roomisopen = testExpectedData.open,
                maxreports = testExpectedData.maxreports
        )

        val testExpectedResult = ChatRoom(
                kind = "chat.room",
                appid = testExpectedData.appid,
                name = testExpectedData.name,
                customid = testExpectedData.customid,
                description = testExpectedData.description,
                moderation = testExpectedData.moderation,
                enableactions = testExpectedData.enableactions,
                enableenterandexit = testExpectedData.enableenterandexit,
                enableprofanityfilter = testExpectedData.enableprofanityfilter,
                delaymessageseconds = testExpectedData.delaymessageseconds,
                open = testExpectedData.open,
                maxreports = testExpectedData.maxreports
        )

        // WHEN
        val testActualResult = chatService.createRoom(
                request = testInputRequest
        )

        // THEN
        println(
                "`Create Room`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatRoom.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.appid == testExpectedResult.appid }
        assertTrue { testActualResult.name == testExpectedResult.name }
        assertTrue { testActualResult.customid == testExpectedResult.customid }
        assertTrue { testActualResult.description == testExpectedResult.description }
        assertTrue { testActualResult.moderation == testExpectedResult.moderation }
        assertTrue { testActualResult.enableactions == testExpectedResult.enableactions }
        assertTrue { testActualResult.enableenterandexit == testExpectedResult.enableenterandexit }
        assertTrue { testActualResult.enableprofanityfilter == testExpectedResult.enableprofanityfilter }
        assertTrue { testActualResult.delaymessageseconds == testExpectedResult.delaymessageseconds }
        assertTrue { testActualResult.open == testExpectedResult.open }
        assertTrue { testActualResult.maxreports == testExpectedResult.maxreports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.id)
    }

    @Test
    fun `A-ERROR-404-User-not-found) Create Room`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateChatRoomRequest(
                userid = "NON-Existing-User-ID"
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.createRoom(request = testInputRequest)
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-User-not-found - Create Room`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified ownerid ${testInputRequest.userid!!} was not found" }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `B - 1) Get Room Details`() = runBlocking {
        // GIVEN
        val testData = TestData.chatRooms(config.appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testData.name!!,
                customid = testData.customid,
                description = testData.description,
                moderation = testData.moderation,
                enableactions = testData.enableactions,
                enableenterandexit = testData.enableenterandexit,
                enableprofanityfilter = testData.enableprofanityfilter,
                delaymessageseconds = testData.delaymessageseconds,
                roomisopen = testData.open,
                maxreports = testData.maxreports
        )
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest)

        val testExpectedResult = testCreatedChatRoomData.copy()

        // WHEN
        val testActualResult = chatService.getRoomDetails(
                chatRoomId = testCreatedChatRoomData.id!!
        )

        // THEN
        println(
                "`Get Room Details`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatRoom.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.name == testExpectedResult.name }
        assertTrue { testActualResult.customid == testExpectedResult.customid }
        assertTrue { testActualResult.description == testExpectedResult.description }
        assertTrue { testActualResult.moderation == testExpectedResult.moderation }
        assertTrue { testActualResult.enableactions == testExpectedResult.enableactions }
        assertTrue { testActualResult.enableenterandexit == testExpectedResult.enableenterandexit }
        assertTrue { testActualResult.enableprofanityfilter == testExpectedResult.enableprofanityfilter }
        assertTrue { testActualResult.delaymessageseconds == testExpectedResult.delaymessageseconds }
        assertTrue { testActualResult.open == testExpectedResult.open }
        assertTrue { testActualResult.maxreports == testExpectedResult.maxreports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.id)
    }

    @Test
    fun `B-ERROR-404) Get Room Details`() = runBlocking {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.getRoomDetails(testInputRoomId)
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get Room Details`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified roomId was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `B - 2) Get Room Details - By Custom ID`() = runBlocking {
        // GIVEN
        val testData = TestData.chatRooms(config.appId).first()
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(
                request = CreateChatRoomRequest(
                        name = testData.name!!,
                        customid = testData.customid,
                        description = testData.description,
                        moderation = testData.moderation,
                        enableactions = testData.enableactions,
                        enableenterandexit = testData.enableenterandexit,
                        enableprofanityfilter = testData.enableprofanityfilter,
                        delaymessageseconds = testData.delaymessageseconds,
                        roomisopen = testData.open,
                        maxreports = testData.maxreports
                )
        )

        val testExpectedResult = testCreatedChatRoomData.copy()

        // WHEN
        val testActualResult = chatService.getRoomDetailsByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!
        )

        // THEN
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.id == testExpectedResult.id }
        assertTrue { testActualResult.name == testExpectedResult.name }
        assertTrue { testActualResult.description == testExpectedResult.description }
        assertTrue { testActualResult.customid == testExpectedResult.customid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
    }

    @Test
    fun `B-ERROR-404) Get Room Details - By Custom ID`() = runBlocking {
        // GIVEN
        val testInputCustomRoomId = "NON-Existing-Custom-Room-ID"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.getRoomDetailsByCustomId(testInputCustomRoomId)
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get Room Details - By Custom ID`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified roomId was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `C) Delete Room`() = runBlocking {
        // GIVEN
        val testData = TestData.chatRooms(config.appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testData.name!!,
                customid = testData.customid,
                description = testData.description,
                moderation = testData.moderation,
                enableactions = testData.enableactions,
                enableenterandexit = testData.enableenterandexit,
                enableprofanityfilter = testData.enableprofanityfilter,
                delaymessageseconds = testData.delaymessageseconds,
                roomisopen = testData.open,
                maxreports = testData.maxreports
        )
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest)

        val testExpectedResult = DeleteChatRoomResponse(
                kind = "deleted.room",
                deletedEventsCount = 0,
                room = testCreatedChatRoomData
        )

        // WHEN
        val testActualResult = chatService.deleteRoom(
                chatRoomId = testCreatedChatRoomData.id!!
        )

        // THEN
        println(
                "`Delete Room`() -> testActualResult = \n" +
                        json.encodeToString(
                                DeleteChatRoomResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.room == testExpectedResult.room }
    }

    @Test
    fun `C-ERROR-404) Delete Room`() = runBlocking {
        // GIVEN
        val testInputCustomRoomId = "NON-Existing-Room-ID"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.deleteRoom(testInputCustomRoomId)
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Delete Room`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specifed room does not exist." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `D) Update Room`() = runBlocking {
        // GIVEN
        val testData = TestData.chatRooms(config.appId).first()
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(
                request = CreateChatRoomRequest(
                        name = testData.name!!,
                        customid = testData.customid,
                        description = testData.description,
                        moderation = testData.moderation,
                        enableactions = testData.enableactions,
                        enableenterandexit = testData.enableenterandexit,
                        enableprofanityfilter = testData.enableprofanityfilter,
                        delaymessageseconds = testData.delaymessageseconds,
                        roomisopen = testData.open,
                        maxreports = testData.maxreports
                )
        )

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = UpdateChatRoomRequest(
                name = "${testData.name!!}-updated",
                customid = "${testData.customid}-updated(${System.currentTimeMillis()})",
                description = "${testData.description}-updated",
                enableactions = !testData.enableactions!!,
                enableenterandexit = !testData.enableenterandexit!!,
                maxreports = 30L
        )

        val testExpectedResult = testCreatedChatRoomData.copy(
                name = testInputRequest.name,
                customid = testInputRequest.customid,
                description = testInputRequest.description,
                enableactions = testInputRequest.enableactions,
                enableenterandexit = testInputRequest.enableenterandexit,
                maxreports = testInputRequest.maxreports
        )

        // WHEN
        val testActualResult = chatService.updateRoom(
                chatRoomId = testInputChatRoomId,
                request = testInputRequest
        )

        // THEN
        println(
                "`Update Room`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatRoom.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.id == testExpectedResult.id }
        assertTrue { testActualResult.name == testExpectedResult.name }
        assertTrue { testActualResult.customid == testExpectedResult.customid }
        assertTrue { testActualResult.description == testExpectedResult.description }
        assertTrue { testActualResult.enableactions == testExpectedResult.enableactions }
        assertTrue { testActualResult.enableenterandexit == testExpectedResult.enableenterandexit }
        assertTrue { testActualResult.maxreports == testExpectedResult.maxreports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.id)
    }

    @Test
    fun `D-ERROR-404) Update Room`() = runBlocking {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testData = TestData.chatRooms(config.appId).first()
        val testInputRequest = UpdateChatRoomRequest(
                name = "${testData.name!!}-updated",
                customid = "${testData.customid}-updated(${System.currentTimeMillis()})",
                description = "${testData.description}-updated",
                enableactions = !testData.enableactions!!,
                enableenterandexit = !testData.enableenterandexit!!,
                maxreports = 30L
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.updateRoom(
                        chatRoomId = testInputRoomId,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Update Room`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified roomid could not be found: $testInputRoomId" }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `E) List Rooms`() = runBlocking {
        // GIVEN
        val testData = TestData.chatRooms(config.appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testData.name!!,
                customid = testData.customid,
                description = testData.description,
                moderation = testData.moderation,
                enableactions = testData.enableactions,
                enableenterandexit = testData.enableenterandexit,
                enableprofanityfilter = testData.enableprofanityfilter,
                delaymessageseconds = testData.delaymessageseconds,
                roomisopen = testData.open,
                maxreports = testData.maxreports
        )
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest)

        val testExpectedResult = ListRoomsResponse(
                kind = "list.chatrooms",
                rooms = listOf(testCreatedChatRoomData)
        )

        // WHEN
        val testActualResult = chatService.listRooms(
                limit = 100/*,
                cursor = testCreatedChatRoomData.id!!*/
        )

        // THEN
        println(
                "`List Rooms`() -> testActualResult = \n" +
                        json.encodeToString(
                                ListRoomsResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.rooms.containsAll(testExpectedResult.rooms) }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
    }

    @Test
    fun `F) Join Room - Authenticated User`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "${testUserData.handle}_${RandomString.make(6)}",
                displayname = testUserData.displayname,
                pictureurl = testUserData.pictureurl,
                profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

        val testChatRoomData = TestData.chatRooms(config.appId).first()
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

        val testExpectedResult = JoinChatRoomResponse(
                kind = "chat.joinroom",
                user = testCreatedUserData,
                room = testCreatedChatRoomData.copy(inroom = testCreatedChatRoomData.inroom!! + 1)
        )

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!,
                handle = testCreatedUserData.handle!!
        )

        // WHEN
        val testActualResult = chatService.joinRoom(
                chatRoomId = testInputChatRoomId,
                request = testInputRequest
        )

        // THEN
        println(
                "`Join Room - Authenticated User`() -> testActualResult = \n" +
                        json.encodeToString(
                                JoinChatRoomResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.user?.userid == testExpectedResult.user?.userid }
        assertTrue { testActualResult.user?.handle == testExpectedResult.user?.handle }
        assertTrue { testActualResult.room?.id == testExpectedResult.room?.id }
        assertTrue { testActualResult.room?.name == testExpectedResult.room?.name }
        assertTrue { testActualResult.room?.description == testExpectedResult.room?.description }

        // Also, assert that ChatRoomEventCursor is currently stored
        assertTrue { testActualResult.eventscursor?.cursor == chatService.chatRoomEventCursor[testInputChatRoomId] }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `G) Join Room - Anonymous User`() = runBlocking {
        // GIVEN
        val testChatRoomData = TestData.chatRooms(config.appId).first()
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

        val testExpectedResult = JoinChatRoomResponse(
                kind = "chat.joinroom",
                user = null,
                room = null
        )
        // WHEN
        val testActualResult = chatService.joinRoom(
                chatRoomIdOrLabel = testCreatedChatRoomData.id!!
        )

        // THEN
        println(
                "`Join Room - Anonymous User`() -> testActualResult = \n" +
                        json.encodeToString(
                                JoinChatRoomResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.user == null }
        assertTrue { testActualResult.room == testCreatedChatRoomData }

        // Also, assert that ChatRoomEventCursor is currently stored
        assertTrue { testActualResult.eventscursor?.cursor == chatService.chatRoomEventCursor[testCreatedChatRoomData.id!!] }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
    }

    @Test
    fun `G-ERROR-404-Room-not-found) Join Room`() = runBlocking {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testInputRequest = JoinChatRoomRequest(
                userid = "non-existing-user"
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.joinRoom(
                        chatRoomId = testInputRoomId,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified roomid '${testInputRoomId}' was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `H) Join Room - By Custom ID`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "${testUserData.handle}_${RandomString.make(6)}",
                displayname = testUserData.displayname,
                pictureurl = testUserData.pictureurl,
                profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

        val testExpectedResult = JoinChatRoomResponse(
                kind = "chat.joinroom",
                user = testCreatedUserData/*,
                        room = null*/
        )

        val testRoomData = TestData.chatRooms(config.appId).first()
        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(
                request = CreateChatRoomRequest(
                        name = testRoomData.name!!,
                        customid = testRoomData.customid,
                        description = testRoomData.description,
                        moderation = testRoomData.moderation,
                        enableactions = testRoomData.enableactions,
                        enableenterandexit = testRoomData.enableenterandexit,
                        enableprofanityfilter = testRoomData.enableprofanityfilter,
                        delaymessageseconds = testRoomData.delaymessageseconds,
                        roomisopen = testRoomData.open,
                        maxreports = testRoomData.maxreports
                )
        )

        val testInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!,
                handle = testCreatedUserData.handle
        )

        // WHEN
        val testActualResult = chatService.joinRoomByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Join Room - By Custom ID`() -> testActualResult = \n" +
                        json.encodeToString(
                                JoinChatRoomResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.user?.userid == testExpectedResult.user?.userid }
        assertTrue { testActualResult.user == testExpectedResult.user }
        assertTrue { testActualResult.room?.customid == testCreatedChatRoomData.customid }

        // Also, assert that ChatRoomEventCursor is currently stored
        assertTrue { testActualResult.eventscursor?.cursor?.takeIf { it.isNotEmpty() } == chatService.chatRoomEventCursor[testCreatedChatRoomData.id!!] }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.room?.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `I) List Room Participants`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "${testUserData.handle}_${RandomString.make(6)}",
                displayname = testUserData.displayname,
                pictureurl = testUserData.pictureurl,
                profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

        val testChatRoomData = TestData.chatRooms(config.appId).first()
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testInputJoinRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )

        // WHEN
        val testJoinChatRoomData = chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testInputJoinRequest
        )

        val testExpectedResult = ListChatRoomParticipantsResponse(
                kind = Kind.CHAT_LIST_PARTICIPANTS,
                participants = listOf(
                        ChatRoomParticipant(
                                kind = "chat.participant",
                                user = testJoinChatRoomData.user!!
                        )
                )
        )

        val testInputChatRoomId = testJoinChatRoomData.room?.id!!
        val testInputLimit = 10

        // WHEN
        val testActualResult = chatService.listRoomParticipants(
                chatRoomId = testInputChatRoomId,
                limit = testInputLimit
        )

        // THEN
        println(
                "`List Room Participants`() -> testActualResult = \n" +
                        json.encodeToString(
                                ListChatRoomParticipantsResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.participants.first().kind == testExpectedResult.participants.first().kind }
        assertTrue { testActualResult.participants.first().user!!.userid == testExpectedResult.participants.first().user!!.userid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `I-ERROR-404) Join Room`() = runBlocking {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testInputRequest = JoinChatRoomRequest(
                userid = "non-existing-user"
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.joinRoom(
                        chatRoomId = testInputRoomId,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified roomid '${testInputRoomId}' was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `J) Exit a Room`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testExpectedResult = Any()

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!

        // WHEN
        val testActualResult = chatService.exitRoom(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId
        )

        // THEN
        println(
                "`Exit a Room`() -> testActualResult"
        )

        assertTrue { testActualResult is Any }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `J-ERROR-404) Exit a Room`() = runBlocking {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testInputRequest = JoinChatRoomRequest(
                userid = "non-existing-user"
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.joinRoom(
                        chatRoomId = testInputRoomId,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified roomid '${testInputRoomId}' was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `K) Get Updates`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testSendMessageInputRequest
        ).speech!!

        val testExpectedResult = GetUpdatesResponse(
                kind = "list.chatevents",
                /*cursor = "",*/
                more = false,
                itemcount = 1,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.getUpdates(
                chatRoomId = testCreatedChatRoomData.id!!/*,
                cursor = null*/
        )

        // THEN
        println(
                "`Get Updates`() -> testActualResult = \n" +
                        json.encodeToString(
                                GetUpdatesResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.itemcount!! >= testExpectedResult.itemcount!! }
        assertTrue { testActualResult.more == testExpectedResult.more }
        assertTrue {
            testActualResult.events.any { ev ->
                ev.id == testSendMessageData.id
                        && ev.kind == testSendMessageData.kind
                        && ev.body == testSendMessageData.body
                        && ev.eventtype == testSendMessageData.eventtype
            }
        }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `K-ERROR-404) Get Updates`() = runBlocking {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.getUpdates(
                        chatRoomId = testInputRoomId
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get Updates`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified room was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `K-1) All Event Updates`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testSendMessageInputRequest
        ).speech!!

        val testExpectedResult = GetUpdatesResponse(
                kind = "list.chatevents",
                /*cursor = "",*/
                more = false,
                itemcount = 1,
                events = listOf(testSendMessageData)
        )

        val chatRoomId = testCreatedChatRoomData.id!!
        chatService.roomSubscriptions.add(chatRoomId)

        val allEventUpdates = MutableSharedFlow<List<ChatEvent>>()

        // WHEN
        val job = chatService.allEventUpdates(
                chatRoomId = chatRoomId,
                frequency = 500
        )
                .take(2)
                .withIndex()
                .onEach { (index, testActualResult) ->
                    println(
                            "`All Event Updates[$index]`() -> response = \n" +
                                    json.encodeToString(
                                            ArraySerializer(ChatEvent.serializer()),
                                            testActualResult.toTypedArray()
                                    )
                    )

                    if(index == 0) {
                        assertTrue { testActualResult.size == testExpectedResult.itemcount!!.toInt() }
                    } else {
                        assertTrue { testActualResult.isEmpty() }
                    }
                }
                .launchIn(GlobalScope)

        delay(1000)

        job.cancelAndJoin()

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)

        return@runBlocking
    }

    @Test
    fun `L) Message Is Reported`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputReportMessageRequest = ReportMessageRequest(
                reporttype = "abuse",
                userid = testCreatedUserData.userid!!
        )

        val reportMessageResponse = chatService.reportMessage(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputReportMessageRequest
        )

        // WHEN
        val messageIsReported = chatService.messageIsReported(
                which = reportMessageResponse,
                userid = testCreatedUserData.userid!!
        )

        // THEN
        println("`Message Is Reported`() -> messageIsReported = $messageIsReported")

        assertTrue { messageIsReported }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `M) Message Is Reacted To`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputReaction = EventReaction.LIKE

        val testInputRequest = ReactToAMessageRequest(
                userid = testCreatedUserData.userid!!,
                reaction = testInputReaction,
                reacted = true
        )

        val reactToEventResponse = chatService.reactToEvent(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputRequest
        )
        println(
                "`Message Is Reacted To`() -> reactToEventResponse = \n" +
                        json.encodeToString(
                                ChatEvent.serializer(),
                                reactToEventResponse
                        )
        )

        // WHEN
        val messageIsReactedTo = chatService.messageIsReactedTo(
                which = reactToEventResponse,
                userid = testCreatedUserData.userid!!,
                reaction = testInputReaction
        )

        // THEN
        println("`Message Is Reacted To`() -> messageIsReactedTo = $messageIsReactedTo")

        assertTrue { messageIsReactedTo }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `N) List Previous Events`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputLimit = 10
        val testExpectedResult = ListEvents(
                kind = Kind.CHAT_LIST,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.listPreviousEvents(
                chatRoomId = testInputChatRoomId,
                limit = testInputLimit
        )

        // THEN
        println(
                "`List Previous Events`() -> testActualResult = \n" +
                        json.encodeToString(
                                ListEvents.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue {
            testActualResult.events.any { ev ->
                ev.id == testSendMessageData.id
                        && ev.kind == testSendMessageData.kind
                        && ev.roomid == testSendMessageData.roomid
                        && ev.eventtype == testSendMessageData.eventtype
                        && ev.body == testSendMessageData.body
            }
        }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `O) Get Event By ID`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputChatEventId = testSendMessageData.id!!
        val testExpectedResult = testSendMessageData.copy()

        // WHEN
        val testActualResult = chatService.getEventById(
                chatRoomId = testInputChatRoomId,
                eventId = testInputChatEventId
        )

        // THEN
        println(
                "`Get Event By ID`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatEvent.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.id == testExpectedResult.id }
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.roomid == testExpectedResult.roomid }
        assertTrue { testActualResult.body == testExpectedResult.body }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P) List Events History`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputLimit = 10
        val testExpectedResult = ListEvents(
                kind = Kind.CHAT_LIST,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.listEventsHistory(
                chatRoomId = testInputChatRoomId,
                limit = testInputLimit
        )

        // THEN
        println(
                "`List Events History`() -> testActualResult = \n" +
                        json.encodeToString(
                                ListEvents.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue {
            testActualResult.events.any { ev ->
                ev.id == testSendMessageData.id
                        && ev.kind == testSendMessageData.kind
                        && ev.roomid == testSendMessageData.roomid
                        && ev.eventtype == testSendMessageData.eventtype
                        && ev.body == testSendMessageData.body
            }
        }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-1) Execute Chat Command - Speech`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = "chat.executecommand",
                op = "speech",
                room = testCreatedChatRoomData,
                speech = ChatEvent(
                        kind = "chat.event",
                        roomid = testCreatedChatRoomData.id,
                        body = testInputRequest.command,
                        eventtype = "speech",
                        userid = testCreatedUserData.userid,
                        user = testCreatedUserData
                ),
                action = null
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Execute Chat Command - Speech`() -> testActualResult = \n" +
                        json.encodeToString(
                                ExecuteChatCommandResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.op == testExpectedResult.op }
        assertTrue { testActualResult.speech?.kind == testExpectedResult.speech?.kind }
        assertTrue { testActualResult.speech?.roomid == testExpectedResult.speech?.roomid }
        assertTrue { testActualResult.speech?.body == testExpectedResult.speech?.body }
        assertTrue { testActualResult.speech?.eventtype == testExpectedResult.speech?.eventtype }
        assertTrue { testActualResult.speech?.userid == testExpectedResult.speech?.userid }
        assertTrue { testActualResult.speech?.user?.userid == testExpectedResult.speech?.user?.userid }
        assertTrue { testActualResult.action == testExpectedResult.action }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-2) Execute Chat Command - Action`() = runBlocking {
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
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
                name = testChatRoomData.name!!,
                customid = "${testChatRoomData.customid}-${Random.nextInt(100, 999)}",
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInputRequest = ExecuteChatCommandRequest(
                // "/high5 {{handle}}"
                command = "/high5 ${testCreatedUserData.handle!!}",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = "chat.executecommand",
                op = "action",
                room = testCreatedChatRoomData,
                speech = null,
                action = ChatEvent(
                        kind = "chat.event",
                        roomid = testCreatedChatRoomData.id,
                        // "Test 1 gave Test 1 a high 5!"
                        body = "${testCreatedUserData.displayname!!} gave ${testCreatedUserData.displayname!!} a high 5!",
                        eventtype = "action",
                        userid = testCreatedUserData.userid,
                        user = testCreatedUserData
                )
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Execute Chat Command - Action`() -> testActualResult = \n" +
                        json.encodeToString(
                                ExecuteChatCommandResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.op == testExpectedResult.op }
        assertTrue { testActualResult.speech == null }
        assertTrue { testActualResult.action?.kind == testExpectedResult.action?.kind }
        assertTrue { testActualResult.action?.roomid == testExpectedResult.action?.roomid }
        assertTrue { testActualResult.action?.body == testExpectedResult.action?.body }
        assertTrue { testActualResult.action?.eventtype == testExpectedResult.action?.eventtype }
        assertTrue { testActualResult.action?.userid == testExpectedResult.action?.userid }
        assertTrue { testActualResult.action?.user?.userid == testExpectedResult.action?.user?.userid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-3) Execute Chat Command - Reply to a Message - Threaded`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).speech!!


        val testInputRequest = SendThreadedReplyRequest(
                body = "This is Jessy, replying to your greetings yow!!!",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = Kind.CHAT/*"chat.executecommand"*/,
                op = null/*"speech"*/,
                room = null,
                speech = null/*ChatEvent(
                        kind = "chat.event",
                        roomid = testCreatedChatRoomData.id,
                        body = testInputRequest.body,
                        eventtype = "reply",
                        userid = testCreatedUserData.userid,
                        user = testCreatedUserData,
                        replyto = testInitialSendMessage
                )*/,
                action = null
        )

        // WHEN
        val testActualResult = chatService.sendThreadedReply(
                chatRoomId = testCreatedChatRoomData.id!!,
                replyTo = testInitialSendMessage.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Execute Chat Command - Reply to a Message - Threaded`() -> testActualResult = \n" +
                        json.encodeToString(
                                ExecuteChatCommandResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.op == testExpectedResult.op }
        assertTrue { testActualResult.room?.id == testExpectedResult.room?.id }
        assertTrue { testActualResult.speech?.roomid == testExpectedResult.speech?.roomid }
        assertTrue { testActualResult.speech?.body == testExpectedResult.speech?.body }
        assertTrue { testActualResult.speech?.eventtype == testExpectedResult.speech?.eventtype }
        assertTrue { testActualResult.speech?.userid == testExpectedResult.speech?.userid }
        assertTrue { testActualResult.speech?.user?.userid == testExpectedResult.speech?.user?.userid }
        assertTrue { testActualResult.speech?.replyto?.id == testExpectedResult.speech?.replyto?.id }
        assertTrue { testActualResult.speech?.replyto?.kind == testExpectedResult.speech?.replyto?.kind }
        assertTrue { testActualResult.speech?.replyto?.body == testExpectedResult.speech?.replyto?.body }
        assertTrue { testActualResult.action == null }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-4) Execute Chat Command - Purge User Messages`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
        val testUserID = RandomString.make(4)
        val testCreateUserInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_$testUserID",
                displayname = testUserData.displayname,
                pictureurl = testUserData.pictureurl,
                profileurl = testUserData.profileurl
        )
        val testAdminID = RandomString.make(4)
        val testCreateAdminInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "test_admin_$testAdminID",
                displayname = "Test Admin $testAdminID"
        )

        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)
        val testCreatedAdminData = userService.createOrUpdateUser(request = testCreateAdminInputRequest)

        val testChatRoomData = TestData.chatRooms(config.appId).first()
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )
        val testAdminJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedAdminData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testAdminJoinRoomInputRequest
        )

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Send test message
        chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )

        val testExpectedResult = ExecuteChatCommandResponse(
                kind = Kind.API/*"chat.executecommand"*/,
                message = "The user's 1 messages were purged."
        )

        // WHEN
        try {
            val testActualResult = chatService.executeChatCommand(
                    chatRoomId = testCreatedChatRoomData.id!!,
                    request = ExecuteChatCommandRequest(
                            command = "*purge zola ${testCreatedUserData.handle!!}",
                            userid = testCreatedUserData.userid!!
                    )
            )

            // THEN
            println(
                    "`Execute Chat Command - Purge User Messages`() -> testActualResult = \n" +
                            json.encodeToString(
                                    ExecuteChatCommandResponse.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.message == testExpectedResult.message }
        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid, testCreatedAdminData.userid)
        }
    }

    @Test
    fun `P-5) Execute Chat Command - Admin Command`() = runBlocking {
        // TODO:: Admin password is hardcoded as "zola".
    }

    @Test
    fun `P-6) Execute Chat Command - Admin - Delete All Events`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInputRequest = ExecuteChatCommandRequest(
                command = "*deleteallevents ${TestData.ADMIN_PASSWORD}",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = "chat.executecommand",
                op = "admin",
                room = null,
                speech = null,
                action = null
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Execute Chat Command - Admin - Delete All Events`() -> testActualResult = \n" +
                        json.encodeToString(
                                ExecuteChatCommandResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.op == testExpectedResult.op }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-7) Send Quoted Reply`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).speech!!


        val testInputRequest = SendQuotedReplyRequest(
                userid = testCreatedUserData.userid!!,
                body = "This is Jessy, replying to your greetings yow!!!"
        )
        val testExpectedResult = ChatEvent(
                kind = /*"chat.event"*/Kind.CHAT,
                roomid = testCreatedChatRoomData.id,
                body = testInputRequest.body,
                eventtype = EventType.QUOTE,
                userid = testCreatedUserData.userid,
                user = testCreatedUserData,
                replyto = testInitialSendMessage
        )

        // WHEN
        val testActualResult = chatService.sendQuotedReply(
                chatRoomId = testCreatedChatRoomData.id!!,
                replyTo = testInitialSendMessage.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Send Quoted Reply`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatEvent.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.roomid == testExpectedResult.roomid }
        assertTrue { testActualResult.body == testExpectedResult.body }
        assertTrue { testActualResult.eventtype == testExpectedResult.eventtype }
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.user?.userid == testExpectedResult.user?.userid }
        assertTrue { testActualResult.replyto?.id == testExpectedResult.replyto?.id }
        assertTrue { testActualResult.replyto?.kind == testExpectedResult.replyto?.kind }
        assertTrue { testActualResult.replyto?.body == testExpectedResult.replyto?.body }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-8) Execute Chat Command - Announcement`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testInputRequest = ExecuteChatCommandRequest(
                command = "This is a test announcement!",
                userid = testCreatedUserData.userid!!,
                eventtype = EventType.ANNOUNCEMENT
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = "chat.executecommand",
                op = EventType.SPEECH,
                room = testCreatedChatRoomData,
                speech = ChatEvent(
                        kind = "chat.event",
                        roomid = testCreatedChatRoomData.id,
                        body = testInputRequest.command,
                        eventtype = EventType.ANNOUNCEMENT,
                        userid = testCreatedUserData.userid,
                        user = testCreatedUserData
                ),
                action = null
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Execute Chat Command - Announcement`() -> testActualResult = \n" +
                        json.encodeToString(
                                ExecuteChatCommandResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.op == testExpectedResult.op }
        assertTrue { testActualResult.speech?.kind == testExpectedResult.speech?.kind }
        assertTrue { testActualResult.speech?.roomid == testExpectedResult.speech?.roomid }
        assertTrue { testActualResult.speech?.body == testExpectedResult.speech?.body }
        assertTrue { testActualResult.speech?.eventtype == testExpectedResult.speech?.eventtype }
        assertTrue { testActualResult.speech?.userid == testExpectedResult.speech?.userid }
        assertTrue { testActualResult.speech?.user?.userid == testExpectedResult.speech?.user?.userid }
        assertTrue { testActualResult.action == testExpectedResult.action }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `P-ERROR-404-User-NOT-found) Execute Chat Command`() = runBlocking {
        // GIVEN
        // GIVEN
        val testInputUserId = "non-existing-user-id"
        val testChatRoomData = TestData.chatRooms(config.appId).first()
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

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow error test",
                userid = testInputUserId
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.executeChatCommand(
                        chatRoomId = testCreatedChatRoomData.id!!,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-User-NOT-found - Execute Chat Command`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified user was not found" }
            assertTrue { err.code == 404 }

            throw err
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
        }

        return@runBlocking
    }

    @Test
    fun `P-ERROR-412-User-not-yet-joined) Execute Chat Command`() = runBlocking {
        // GIVEN
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

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow error test",
                userid = testCreatedUserData.userid!!
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.executeChatCommand(
                        chatRoomId = testCreatedChatRoomData.id!!,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-User-not-yet-joined - Execute Chat Command`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "A user cannot execute commands in a room unless the user has joined the room." }
            assertTrue { err.code == 412 }

            throw err
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid)
        }

        return@runBlocking
    }


    @Test
    fun `P-ERROR-404-REPLY-NOT-FOUND) Execute Chat Command`() = runBlocking {
        // GIVEN
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testReplyToIdNonExisting = "non-existing-ID"
        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow error test",
                userid = testCreatedUserData.userid!!,
                replyto = testReplyToIdNonExisting
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.executeChatCommand(
                        chatRoomId = testCreatedChatRoomData.id!!,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-REPLY-NOT-FOUND - Execute Chat Command`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The message you want to reply to can't be found." }
            assertTrue { err.code == 404 }
            assertTrue { err.data?.get("kind") == Kind.CHAT_COMMAND }
            assertTrue { err.data?.get("op") == "speech" }

            throw err
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid)
        }

        return@runBlocking
    }


    @Test
    fun `Q) List Messages By User`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!
        val testInputLimit = 10
        val testExpectedResult = ListMessagesByUser(
                kind = "list.chatevents",
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.listMessagesByUser(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId,
                limit = testInputLimit
        )

        // THEN
        println(
                "`List Messages By User`() -> testActualResult = \n" +
                        json.encodeToString(
                                ListMessagesByUser.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue {
            testActualResult.events.any { ev ->
                ev.id == testSendMessageData.id
                        && ev.eventtype == testSendMessageData.eventtype
                        && ev.body == testSendMessageData.body
            }
        }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `R) Bounce User - Ban user`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val bounceMessageBody = "`${testCreatedUserData.handle}` has been banned."
        val testInputRequest = BounceUserRequest(
                userid = testCreatedUserData.userid!!,
                bounce = true,
                announcement = bounceMessageBody
        )
        val testExpectedResult = BounceUserResponse(
                kind = Kind.BOUNCE_USER,
                event = ChatEvent(
                        body = bounceMessageBody,
                        eventtype = EventType.BOUNCE,
                        userid = testCreatedUserData.userid!!
                ),
                room = testCreatedChatRoomData.copy(
                        bouncedusers = listOf(testCreatedUserData.userid!!)
                )
        )

        // WHEN
        val testActualResult = chatService.bounceUser(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Bounce User - Ban user`() -> testActualResult = \n" +
                        json.encodeToString(
                                BounceUserResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.event?.body == testExpectedResult.event?.body }
        assertTrue { testActualResult.event?.eventtype == testExpectedResult.event?.eventtype }
        assertTrue { testActualResult.event?.userid == testExpectedResult.event?.userid }
        assertTrue { testActualResult.room?.bouncedusers == testExpectedResult.room?.bouncedusers }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `S) Delete Event`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputEventId = testSendMessageData.id!!
        val testInputUserId = testCreatedUserData.userid!!
        val testExpectedResult = DeleteEventResponse(
                kind = Kind.DELETED_COMMENT,
                permanentdelete = true,
                event = testSendMessageData.copy(
                        deleted = true
                )
        )

        // WHEN
        val testActualResult = chatService.deleteEvent(
                chatRoomId = testInputChatRoomId,
                eventId = testInputEventId,
                userid = testInputUserId
        )

        // THEN
        println(
                "`Delete Event`() -> testActualResult = \n" +
                        json.encodeToString(
                                DeleteEventResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.permanentdelete == testExpectedResult.permanentdelete }
        assertTrue { testActualResult.event?.id == testExpectedResult.event?.id }
        assertTrue { testActualResult.event?.userid == testExpectedResult.event?.userid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `T) Remove a Message - Logically Delete`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputEventId = testSendMessageData.id!!
        val testInputUserId = testCreatedUserData.userid!!
        val testInputDeleted = true
        val testInputPermanentIfNoReplies = false
        val testExpectedResult = DeleteEventResponse(
                kind = Kind.DELETED_COMMENT,
                permanentdelete = false,
                event = testSendMessageData.copy(
                        deleted = testInputDeleted
                )
        )

        // WHEN
        val testActualResult = chatService.setMessageAsDeleted(
                chatRoomId = testInputChatRoomId,
                eventId = testInputEventId,
                userid = testInputUserId,
                deleted = testInputDeleted,
                permanentifnoreplies = testInputPermanentIfNoReplies
        )

        // THEN
        println(
                "`Remove a Message - Logically Delete`() -> testActualResult = \n" +
                        json.encodeToString(
                                DeleteEventResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.permanentdelete == testExpectedResult.permanentdelete }
        assertTrue { testActualResult.event?.id == testExpectedResult.event?.id }
        assertTrue { testActualResult.event?.deleted == testExpectedResult.event?.deleted }
        assertTrue { testActualResult.event?.userid == testExpectedResult.event?.userid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `U) Report a Message`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputRequest = ReportMessageRequest(
                reporttype = "abuse",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = testSendMessageData.copy(
                active = false,
                moderation = "flagged",
                reports = listOf(
                        ChatEventReport(
                                userid = testInputRequest.userid,
                                reason = testInputRequest.reporttype
                        )
                )
        )

        // WHEN
        val testActualResult = chatService.reportMessage(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`Report a Message`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatEvent.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.id == testExpectedResult.id }
        assertTrue { testActualResult.active == testExpectedResult.active }
        assertTrue { testActualResult.moderation == testExpectedResult.moderation }
        assertTrue { testActualResult.reports == testExpectedResult.reports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `U-ERROR-404-EVENT-NOT-FOUND) Report a Message`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        )

        val testChatIdNonExisting = "non-existing-ID"
        val testInputRequest = ReportMessageRequest(
                reporttype = "abuse",
                userid = testCreatedUserData.userid!!
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.reportMessage(
                        chatRoomId = testCreatedChatRoomData.id!!,
                        eventId = testChatIdNonExisting,
                        request = testInputRequest
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-EVENT-NOT-FOUND - Report a Message`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified event was not found." }
            assertTrue { err.code == 404 }
            assertTrue { err.data?.get("eventId") == testChatIdNonExisting }

            throw err
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid)
        }

        return@runBlocking
    }

    @Test
    fun `V) React to a Message`() = runBlocking {
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

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
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

        val testInputRequest = ReactToAMessageRequest(
                userid = testCreatedUserData.userid!!,
                reaction = EventReaction.LIKE,
                reacted = true
        )
        val testExpectedResult = ChatEvent(
                kind = "chat.event",
                roomid = testCreatedChatRoomData.id,
                body = "",
                eventtype = "reaction",
                userid = testInputRequest.userid,
                replyto = testSendMessageData.copy(
                        reactions = listOf(
                                ChatEventReaction(
                                        type = testInputRequest.reaction,
                                        count = 1,
                                        users = listOf(
                                                User(
                                                        userid = testCreatedUserData.userid!!,
                                                        handle = testCreatedUserData.handle!!,
                                                        displayname = testCreatedUserData.displayname!!
                                                )
                                        )
                                )
                        )
                )
        )

        // WHEN
        val testActualResult = chatService.reactToEvent(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputRequest
        )

        // THEN
        println(
                "`React to a Message`() -> testActualResult = \n" +
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
        assertTrue { testActualResult.replyto?.id == testExpectedResult.replyto?.id }
        /* Assert each reaction from event response */
        assertTrue {
            testActualResult.replyto?.reactions!!.any { rxn ->
                testExpectedResult.replyto?.reactions!!.any { expectedRxn ->
                    rxn.type == expectedRxn.type
                            && rxn.count == expectedRxn.count
                            && rxn.users.any { usr -> expectedRxn.users.any { expectedUsr -> usr.userid == expectedUsr.userid } }
                }
            }
        }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    object TestData {
        val ADMIN_PASSWORD = "zola"

        val users = listOf(
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test1",
                        displayname = "Test 1",
                        pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                        profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
                ),
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test2",
                        displayname = "Test 2",
                        pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                        profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
                ),
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test3",
                        displayname = "Test 3",
                        pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                        profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
                ),
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test3",
                        displayname = "Test 3",
                        pictureurl = "http://www.thepresidentshalloffame.com/media/reviews/photos/original/a9/c7/a6/44-1-george-washington-18-1549729902.jpg",
                        profileurl = "http://www.thepresidentshalloffame.com/1-george-washington"
                )
        )

        var _chatRooms: List<ChatRoom>? = null
        fun chatRooms(appId: String): List<ChatRoom> =
                if (_chatRooms != null) _chatRooms!!
                else listOf(
                        ChatRoom(
                                kind = "chat.room",
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
                                kind = "chat.room",
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
                                kind = "chat.room",
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
                                kind = "chat.room",
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