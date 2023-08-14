package com.sportstalk.coroutine

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.coroutine.api.JWTProvider
import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.coroutine.service.ChatServiceTest
import com.sportstalk.coroutine.service.UserService
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.users.CreateUpdateUserRequest
import com.sportstalk.datamodels.users.User
import com.sportstalk.datamodels.users.UserNotification
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import net.bytebuddy.utility.RandomString
import org.junit.*
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.assertTrue
import kotlin.test.fail

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
class JWTProviderTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var jwtProvider: JWTProvider
    private lateinit var userService: UserService
    private lateinit var chatService: ChatService
    private lateinit var json: Json

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var jwtProviderJob: Job

    private val testDispatcher = StandardTestDispatcher()

    @Suppress("DEPRECATION")
    @get:Rule
    val thrown: ExpectedException = ExpectedException.none()

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
            appId = "5ffd115386c29223e4de754c",
            apiToken = "Cjh2_2VLhk2iyQUSEsfphAZkrrs6J-Vk2ELL7YzzwWJw",
            endpoint = "https://prod-api.sportstalk247.com/api/v3/"
        )

        val secret = "B28D0982C9D6F6CFBA9637F561B923D7"
        // ... Derive JWT using SECRET
        val jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiJ0ZXN0dXNlcjEiLCJyb2xlIjoidXNlciJ9.H1cMH21k1m6zNFgVhvvxkG1DdTAOCyGCfxMzP-5XT7U"
        jwtProvider = JWTProvider(
            token = jwt,
            tokenRefreshAction = { jwt }
        )

        coroutineScope = CoroutineScope(context = EmptyCoroutineContext)
        jwtProviderJob = jwtProvider
            .observe()
            .launchIn(coroutineScope)

        SportsTalk247.setJWTProvider(
            config = config,
            provider = jwtProvider
        )

        userService = ServiceFactory.User.get(config)
        chatService = ServiceFactory.Chat.get(config)
        json = ServiceFactory.RestApi.json

        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()

        jwtProviderJob.cancel()
        coroutineScope.cancel()
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
    fun `A) Signed User`() = runBlocking {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${RandomString.make(6)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

        val testChatRoomData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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

        delay(250)

        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

        // WHEN
        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!,
            handle = testCreatedUserData.handle!!
        )

        delay(250)

        // THEN
        chatService.joinRoom(
            chatRoomId = testInputChatRoomId,
            request = testInputRequest
        )
        assert(true)    // Passed! No Error encountered.

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `B) Unsigned User`() = runBlocking {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${RandomString.make(6)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

        val testChatRoomData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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

        delay(250)

        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

        val testExpectedResult = JoinChatRoomResponse(
            kind = Kind.JOIN_ROOM,
            user = testCreatedUserData,
            room = testCreatedChatRoomData.copy(inroom = testCreatedChatRoomData.inroom!! + 1)
        )

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!,
            handle = testCreatedUserData.handle!!
        )

        delay(250)

        // Set INVALID JWT
        SportsTalk247.setJWTProvider(
            config = config,
            provider = jwtProvider.apply {
                // Just emit an INVALID JWT
                val jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiJ0ZXN0dXNlcjEiLCJyb2xlIjoidXNlciJ9.L43SmGmnKwVyPTMzLLIcY3EUb83A4YPBc0l6778Od_0"
                setToken(jwt)
            }
        )

        delay(250)

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            chatService.joinRoom(
                chatRoomId = testInputChatRoomId,
                request = testInputRequest
            )
        } catch(err: SportsTalkException) {
            println("Unsigned User -> testActualResult = \n" +
                    json.encodeToString(
                        SportsTalkException.serializer(),
                        err
                    )
            )

            assertTrue { err.code == 401 }

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
    fun `C) User chats`() = runBlocking {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${RandomString.make(6)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

        val testChatRoomData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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

        delay(250)

        // Should create a test chat room first
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

        val testExpectedResult = JoinChatRoomResponse(
            kind = Kind.JOIN_ROOM,
            user = testCreatedUserData,
            room = testCreatedChatRoomData.copy(inroom = testCreatedChatRoomData.inroom!! + 1)
        )

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val joinChatRoomInput = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!,
            handle = testCreatedUserData.handle!!
        )

        delay(250)

        val joinRoomResponse = chatService.joinRoom(
            chatRoomId = testInputChatRoomId,
            request = joinChatRoomInput
        )

        delay(250)

        val testInputRequest = ExecuteChatCommandRequest(
            command = "Yow Jessy, how are you doin'?",
            userid = testCreatedUserData.userid!!
        )

        // WHEN
        chatService.executeChatCommand(
            chatRoomId = testCreatedChatRoomData.id!!,
            request = testInputRequest
        )

        // THEN
        assert(true)    // Passed! No Error encountered.

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `D) Notifications`() = runBlocking {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${RandomString.make(6)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )

        // Should create a test chat room first
        var testCreatedUserData: User? = null
        var testCreatedChatRoomData: ChatRoom? = null

        try {
            // Should create a test user first
            testCreatedUserData =
                userService.createOrUpdateUser(request = testCreateUserInputRequest)

            val testChatRoomData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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

            delay(250)

            // Should create a test chat room first
            testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

            val testInputJoinChatRoomId = testCreatedChatRoomData?.id!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData?.userid!!
            )

            delay(250)

            // Test Created User Should join test created chat room
            chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
            )

            val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData?.userid!!
            )

            delay(250)

            // Test Created User Should send an initial message to the created chat room
            chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData?.id!!,
                request = testInitialSendMessageInputRequest
            ).speech!!

            delay(250)


            delay(250)

            // WHEN
            userService.listUserNotifications(
                userId = testCreatedUserData?.userid!!,
                limit = 10,
                filterNotificationTypes = listOf(UserNotification.Type.CHAT_REPLY),
                cursor = null,
                includeread = false,
                filterChatRoomId = testCreatedChatRoomData.id
            )

            // THEN
            assert(true)    // Passed! No Error encountered.

        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Room
            deleteTestChatRooms(testCreatedChatRoomData?.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData?.userid)
        }

        return@runBlocking
    }

    @Test
    fun `E) Leave Room`() = runBlocking {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${RandomString.make(6)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )

        // Should create a test chat room first
        var testCreatedUserData: User? = null
        var testCreatedChatRoomData: ChatRoom? = null

        try {
            // Should create a test user first
            testCreatedUserData =
                userService.createOrUpdateUser(request = testCreateUserInputRequest)

            val testChatRoomData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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

            delay(250)

            // Should create a test chat room first
            testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

            val testInputJoinChatRoomId = testCreatedChatRoomData?.id!!
            val testInputUserId = testCreatedUserData?.userid!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testInputUserId
            )

            delay(250)

            // Test Created User Should join test created chat room
            chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
            )

            delay(250)

            // WHEN
            chatService.exitRoom(
                chatRoomId = testInputJoinChatRoomId,
                userId = testInputUserId
            )

            // THEN
            assert(true)    // Passed! No Error encountered.

        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Room
            deleteTestChatRooms(testCreatedChatRoomData?.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData?.userid)
        }

        return@runBlocking
    }

    @Test
    fun `F) Delete Room`() = runBlocking {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${RandomString.make(6)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )

        // Should create a test chat room first
        var testCreatedUserData: User? = null
        var testCreatedChatRoomData: ChatRoom? = null

        try {
            // Should create a test user first
            testCreatedUserData =
                userService.createOrUpdateUser(request = testCreateUserInputRequest)

            delay(250)

            val testChatRoomData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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
            testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

            val testInputDeleteChatRoomId = testCreatedChatRoomData?.id!!
            val testInputUserId = testCreatedUserData?.userid!!

            delay(250)

            // WHEN
            chatService.deleteRoom(chatRoomId = testInputDeleteChatRoomId)

            // THEN
            assert(true)    // Passed! No Error encountered.

        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData?.userid)
        }

        return@runBlocking
    }




}