package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.DateUtils
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.service.UserService
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.Kind
import com.sportstalk.models.SportsTalkException
import com.sportstalk.models.chat.*
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
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

@UnstableDefault
@ImplicitReflectionSerializer
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
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
        userService = ServiceFactory.RestApi.User.get(config)
        chatService = ServiceFactory.RestApi.Chat.get(config)

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
    private fun deleteTestUsers(vararg userIds: String?) {
        for (id in userIds) {
            id ?: continue
            try {
                userService.deleteUser(userId = id).get()
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
                chatService.deleteRoom(chatRoomId = id).get()
            } catch (err: Throwable) {
            }
        }
    }

    @Test
    fun `0-ERROR-403) Request is not authorized with a token`() = runBlocking {
        val userCaseChatService = ServiceFactory.RestApi.Chat.get(
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-403 - Request is not authorized with a token`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `A) Create Room`() {
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
        ).get()

        // THEN
        println(
                "`Create Room`() -> testActualResult = \n" +
                        json.stringify(
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-User-not-found - Create Room`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `B - 1) Get Room Details`() {
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
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest).get()

        val testExpectedResult = testCreatedChatRoomData.copy()

        // WHEN
        val testActualResult = chatService.getRoomDetails(
                chatRoomId = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Get Room Details`() -> testActualResult = \n" +
                        json.stringify(
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get Room Details`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `B - 2) Get Room Details - By Custom ID`() {
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
        ).get()

        val testExpectedResult = testCreatedChatRoomData.copy()

        // WHEN
        val testActualResult = chatService.getRoomDetailsByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!
        ).get()

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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get Room Details - By Custom ID`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `C) Delete Room`() {
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
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest).get()

        val testExpectedResult = DeleteChatRoomResponse(
                kind = "deleted.room",
                deletedEventsCount = 0,
                room = testCreatedChatRoomData
        )

        // WHEN
        val testActualResult = chatService.deleteRoom(
                chatRoomId = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Delete Room`() -> testActualResult = \n" +
                        json.stringify(
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Delete Room`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `D) Update Room`() {
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
        ).get()

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
        ).get()

        // THEN
        println(
                "`Update Room`() -> testActualResult = \n" +
                        json.stringify(
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Update Room`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `E) List Rooms`() {
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
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest).get()

        val testExpectedResult = ListRoomsResponse(
                kind = "list.chatrooms",
                rooms = listOf(testCreatedChatRoomData)
        )

        // WHEN
        val testActualResult = chatService.listRooms(
                limit = 100/*,
                cursor = testCreatedChatRoomData.id!!*/
        ).get()

        // THEN
        println(
                "`List Rooms`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `F) Join Room - Authenticated User`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

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
        ).get()

        // THEN
        println(
                "`Join Room - Authenticated User`() -> testActualResult = \n" +
                        json.stringify(
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

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `G) Join Room - Anonymous User`() {
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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testExpectedResult = JoinChatRoomResponse(
                kind = "chat.joinroom",
                user = null,
                room = null
        )
        // WHEN
        val testActualResult = chatService.joinRoom(
                chatRoomIdOrLabel = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Join Room - Anonymous User`() -> testActualResult = \n" +
                        json.stringify(
                                JoinChatRoomResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.user == null }
        assertTrue { testActualResult.room == testCreatedChatRoomData }
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `H) Join Room - By Custom ID`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        ).get()

        val testInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!,
                handle = testCreatedUserData.handle
        )

        // WHEN
        val testActualResult = chatService.joinRoomByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!,
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Join Room - By Custom ID`() -> testActualResult = \n" +
                        json.stringify(
                                JoinChatRoomResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.user?.userid == testExpectedResult.user?.userid }
        assertTrue { testActualResult.user == testExpectedResult.user }
        assertTrue { testActualResult.room?.customid == testCreatedChatRoomData.customid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.room?.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `I) List Room Participants`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testInputJoinRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )

        // WHEN
        val testJoinChatRoomData = chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testInputJoinRequest
        ).get()

        val testExpectedResult = ListChatRoomParticipantsResponse(
                kind = "list.chatparticipants",
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
        ).get()

        // THEN
        println(
                "`List Room Participants`() -> testActualResult = \n" +
                        json.stringify(
                                ListChatRoomParticipantsResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.participants.first().kind == testExpectedResult.participants.first().kind }
        assertTrue { testActualResult.participants.first().user == testExpectedResult.participants.first().user }

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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `J) Exit a Room`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testExpectedResult = Any()

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!

        // WHEN
        val testActualResult = chatService.exitRoom(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId
        ).get()

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
//        // GIVEN
//        val testInputRoomId = "NON-Existing-Room-ID"
//        val testInputRequest = JoinChatRoomRequest(
//                userid = "non-existing-user"
//        )
//
//        // EXPECT
//        thrown.expect(SportsTalkException::class.java)
//
//        // WHEN
//        try {
//            withContext(Dispatchers.IO) {
//                chatService.joinRoom(
//                        chatRoomId = testInputRoomId,
//                        request = testInputRequest
//                )
//                        .await()
//            }
//        } catch (err: SportsTalkException) {
//            println(
//                    "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
//                            json.stringify(
//                                    SportsTalkException.serializer(),
//                                    err
//                            )
//            )
//            assertTrue { err.kind == Kind.API }
//            assertTrue { err.message == "The specified roomid '${testInputRoomId}' was not found." }
//            assertTrue { err.code == 404 }
//
//            throw err
//        }

        // TODO:: J-ERROR-404) Exit a Room
        assertTrue { true }
        return@runBlocking
    }

    @Test
    fun `K) Get Updates`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testSendMessageInputRequest
        ).get().speech!!

        val testExpectedResult = GetUpdatesResponse(
                kind = "list.chatevents",
                /*cursor = "",*/
                more = false,
                itemcount = 1,
                room = testCreatedChatRoomData,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.getUpdates(
                chatRoomId = testCreatedChatRoomData.id!!/*,
                cursor = null*/
        ).get()

        // THEN
        println(
                "`Get Updates`() -> testActualResult = \n" +
                        json.stringify(
                                GetUpdatesResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.itemcount!! >= testExpectedResult.itemcount!! }
        assertTrue { testActualResult.more == testExpectedResult.more }
        assertTrue { testActualResult.room?.id == testExpectedResult.room?.id }
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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get Updates`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `L) List Previous Events`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!

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
        ).get()

        // THEN
        println(
                "`List Previous Events`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `M) List Events History`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!

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
        ).get()

        // THEN
        println(
                "`List Events History`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `M-1) Execute Chat Command - Speech`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

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
        ).get()

        // THEN
        println(
                "`Execute Chat Command - Speech`() -> testActualResult = \n" +
                        json.stringify(
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

//      TODO:: Broken REST API(Error 500): Execute Chat Command - Action
//    @Test
//    fun `M-2) Execute Chat Command - Action`() {
//        // GIVEN
//        val testUserData = TestData.users.first()
//        val testCreateUserInputRequest = CreateUpdateUserRequest(
//                userid = RandomString.make(16),
//                handle = "${testUserData.handle}_${Random.nextInt(100, 999)}",
//                displayname = testUserData.displayname,
//                pictureurl = testUserData.pictureurl,
//                profileurl = testUserData.profileurl
//        )
//        // Should create a test user first
//        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()
//
//        val testChatRoomData = TestData.chatRooms(config.appId).first()
//        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
//                name = testChatRoomData.name!!,
//                customid = "${testChatRoomData.customid}-${Random.nextInt(100, 999)}",
//                description = testChatRoomData.description,
//                moderation = testChatRoomData.moderation,
//                enableactions = testChatRoomData.enableactions,
//                enableenterandexit = testChatRoomData.enableenterandexit,
//                enableprofanityfilter = testChatRoomData.enableprofanityfilter,
//                delaymessageseconds = testChatRoomData.delaymessageseconds,
//                roomisopen = testChatRoomData.open,
//                maxreports = testChatRoomData.maxreports
//        )
//        // Should create a test chat room first
//        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()
//
//        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
//        val testJoinRoomInputRequest = JoinChatRoomRequest(
//                userid = testCreatedUserData.userid!!
//        )
//        // Test Created User Should join test created chat room
//        chatService.joinRoom(
//                chatRoomId = testInputJoinChatRoomId,
//                request = testJoinRoomInputRequest
//        ).get()
//
//        val testInputRequest = ExecuteChatCommandRequest(
//                // "/high5 {{handle}}"
//                command = "/high5 ${testCreatedUserData.handle!!}",
//                userid = testCreatedUserData.userid!!
//        )
//        val testExpectedResult = ExecuteChatCommandResponse(
//                kind = "chat.executecommand",
//                op = "action",
//                room = testCreatedChatRoomData,
//                speech = null,
//                action = ChatEvent(
//                        kind = "chat.event",
//                        roomid = testCreatedChatRoomData.id,
//                        // "Test 1 gave Test 1 a high 5!"
//                        body = "${testCreatedUserData.displayname!!} gave ${testCreatedUserData.displayname!!} a high 5!",
//                        eventtype = "action",
//                        userid = testCreatedUserData.userid,
//                        user = testCreatedUserData
//                )
//        )
//
//        // WHEN
//        val testActualResult = chatService.executeChatCommand(
//                chatRoomId = testCreatedChatRoomData.id!!,
//                request = testInputRequest
//        ).get()
//
//        // THEN
//        println(
//                "`Execute Chat Command - Action`() -> testActualResult = \n" +
//                        json.stringify(
//                                ExecuteChatCommandResponse.serializer(),
//                                testActualResult
//                        )
//        )
//
//        assertTrue { testActualResult.kind == testExpectedResult.kind }
//        assertTrue { testActualResult.op == testExpectedResult.op }
//        assertTrue { testActualResult.speech == null }
//        assertTrue { testActualResult.action?.kind == testExpectedResult.action?.kind }
//        assertTrue { testActualResult.action?.roomid == testExpectedResult.action?.roomid }
//        assertTrue { testActualResult.action?.body == testExpectedResult.action?.body }
//        assertTrue { testActualResult.action?.eventtype == testExpectedResult.action?.eventtype }
//        assertTrue { testActualResult.action?.userid == testExpectedResult.action?.userid }
//        assertTrue { testActualResult.action?.user?.userid == testExpectedResult.action?.user?.userid }
//
//        // Perform Delete Test Chat Room
//        deleteTestChatRooms(testCreatedChatRoomData.id)
//        // Perform Delete Test User
//        deleteTestUsers(testCreatedUserData.userid)
//    }

    @Test
    fun `M-3) Execute Chat Command - Reply to a Message - Threaded`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!


        val testInputRequest = SendThreadedReplyRequest(
                command = "This is Jessy, replying to your greetings yow!!!",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = "chat.executecommand",
                op = "speech",
                speech = ChatEvent(
                        kind = "chat.event",
                        roomid = testCreatedChatRoomData.id,
                        body = testInputRequest.command,
                        eventtype = "reply",
                        userid = testCreatedUserData.userid,
                        user = testCreatedUserData,
                        replyto = testInitialSendMessage
                ),
                action = null
        )

        // WHEN
        val testActualResult = chatService.sendThreadedReply(
                chatRoomId = testCreatedChatRoomData.id!!,
                replyTo = testInitialSendMessage.id!!,
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Execute Chat Command - Reply to a Message - Threaded`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `M-4) Execute Chat Command - Purge User Messages`() {
        // TODO:: Admin password is hardcoded as "zola".
    }

    @Test
    fun `M-5) Execute Chat Command - Admin Command`() {
        // TODO:: Admin password is hardcoded as "zola".
    }

    @Test
    fun `M-6) Execute Chat Command - Admin - Delete All Events`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

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
        ).get()

        // THEN
        println(
                "`Execute Chat Command - Admin - Delete All Events`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `M-7) Send Quoted Reply`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!


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
        ).get()

        // THEN
        println(
                "`Send Quoted Reply`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `M-ERROR-404-User-NOT-found) Execute Chat Command`() = runBlocking {
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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-User-NOT-found - Execute Chat Command`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `M-ERROR-412-User-not-yet-joined) Execute Chat Command`() = runBlocking {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-User-not-yet-joined - Execute Chat Command`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `M-ERROR-404-REPLY-NOT-FOUND) Execute Chat Command`() = runBlocking {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-REPLY-NOT-FOUND - Execute Chat Command`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `N) List Messages By User`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!

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
        ).get()

        // THEN
        println(
                "`List Messages By User`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `O) Remove a Message`() {
        // TODO:: `Removes a message` API is broken at the moment
    }

    @Test
    fun `P) Report a Message`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!

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
        ).get()

        // THEN
        println(
                "`Report a Message`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `P-ERROR-404-EVENT-NOT-FOUND) Report a Message`() = runBlocking {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

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
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404-EVENT-NOT-FOUND - Report a Message`() -> testActualResult = \n" +
                            json.stringify(
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
    fun `Q) React to a Message`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().speech!!

        val testInputRequest = ReactToAMessageRequest(
                userid = testCreatedUserData.userid!!,
                reaction = "like",
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
        ).get()

        // THEN
        println(
                "`React to a Message`() -> testActualResult = \n" +
                        json.stringify(
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

    @Test
    fun `Q-ERROR-404-EVENT-NOT-FOUND) Report a Message`() = runBlocking {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get()

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get()

        val testChatIdNonExisting = "non-existing-ID"
        val testInputRequest = ReactToAMessageRequest(
                userid = testCreatedUserData.userid!!,
                reaction = "like",
                reacted = true
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                chatService.reactToEvent(
                        chatRoomId = testCreatedChatRoomData.id!!,
                        eventId = testChatIdNonExisting,
                        request = testInputRequest
                )
                        .await()
            }
        } catch (err: SportsTalkException) {
            println(
                    "`R-ERROR-404-EVENT-NOT-FOUND - Report a Message`() -> testActualResult = \n" +
                            json.stringify(
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