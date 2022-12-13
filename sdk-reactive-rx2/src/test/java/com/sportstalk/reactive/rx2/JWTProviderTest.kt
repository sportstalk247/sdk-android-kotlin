package com.sportstalk.reactive.rx2

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.users.CreateUpdateUserRequest
import com.sportstalk.datamodels.users.ListUserNotificationsResponse
import com.sportstalk.datamodels.users.User
import com.sportstalk.datamodels.users.UserNotification
import com.sportstalk.reactive.rx2.api.JWTProvider
import com.sportstalk.reactive.rx2.service.ChatService
import com.sportstalk.reactive.rx2.service.ChatServiceTest
import com.sportstalk.reactive.rx2.service.UserService
import com.sportstalk.reactive.rx2.service.UserServiceTest
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import kotlinx.serialization.json.Json
import net.bytebuddy.utility.RandomString
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Callable
import kotlin.random.Random
import kotlin.test.assertTrue
import kotlin.test.fail

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.KITKAT])
class JWTProviderTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var jwtProvider: JWTProvider
    private lateinit var userService: UserService
    private lateinit var chatService: ChatService
    private lateinit var json: Json

    private lateinit var rxDisposeBag: CompositeDisposable

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


        val secret = appInfo.metaData?.getString("sportstalk.api.secret")!!
        // ... Derive JWT using SECRET
        val jwt = appInfo.metaData?.getString("sportstalk.api.jwt")!!
        jwtProvider = JWTProvider(
            initialToken = jwt,
            refreshCallback = { _ -> Single.create<String?> { e -> e.onSuccess(jwt) } }
        )

        rxDisposeBag = CompositeDisposable()

        jwtProvider
            .observe()
            .doOnSubscribe {
                rxDisposeBag.add(it)
            }
            .subscribe()

        SportsTalk247.setJWTProvider(
            config = config,
            provider = jwtProvider
        )

        userService = ServiceFactory.User.get(config)
        chatService = ServiceFactory.Chat.get(config)
        json = ServiceFactory.RestApi.json

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
    fun `A) Signed User`() {
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
        val testCreatedUserData = userService
            .createOrUpdateUser(request = testCreateUserInputRequest)
            .blockingGet()

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
        val testCreatedChatRoomData = chatService
            .createRoom(testCreateChatRoomInputRequest)
            .blockingGet()

        // WHEN
        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!,
            handle = testCreatedUserData.handle!!
        )

        try {
            chatService.joinRoom(
                chatRoomId = testInputChatRoomId,
                request = testInputRequest
            ).blockingGet()

            // THEN
            assert(true)    // Passed! No Error encountered.

        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid)
        }
    }

    @Test
    fun `B) Unsigned User`() {
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
        val testCreatedUserData = userService
            .createOrUpdateUser(request = testCreateUserInputRequest)
            .blockingGet()

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
        val testCreatedChatRoomData = chatService
            .createRoom(testCreateChatRoomInputRequest)
            .blockingGet()

        // Set INVALID JWT
        SportsTalk247.setJWTProvider(
            config = config,
            provider = jwtProvider.apply {
                // Just emit an INVALID JWT
                val jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiJ0ZXN0dXNlcjEiLCJyb2xlIjoidXNlciJ9.L43SmGmnKwVyPTMzLLIcY3EUb83A4YPBc0l6778Od_0"
                setToken(jwt)
            }
        )

        // WHEN
        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!,
            handle = testCreatedUserData.handle!!
        )

        val joinRoom = TestObserver<JoinChatRoomResponse>()

        chatService.joinRoom(
            chatRoomId = testInputChatRoomId,
            request = testInputRequest
        )
            .doOnSubscribe { rxDisposeBag.add(it) }
            .doOnDispose {
                // Perform Delete Test Chat Room
                deleteTestChatRooms(testCreatedChatRoomData.id)
                // Perform Delete Test User
                deleteTestUsers(testCreatedUserData.userid)
            }
            .subscribe(joinRoom)

        // THEN
        joinRoom
            .assertError {
                val err = it as? SportsTalkException ?: run {
                    fail()
                }

                return@assertError err.code == 401
            }
    }

    @Test
    fun `C) User chats`() {
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
        val testCreatedUserData = userService
            .createOrUpdateUser(request = testCreateUserInputRequest)
            .blockingGet()

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
        val testCreatedChatRoomData = chatService
            .createRoom(testCreateChatRoomInputRequest)
            .blockingGet()

        // WHEN
        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputJoinRoomRequest = JoinChatRoomRequest(
            userid = testCreatedUserData.userid!!,
            handle = testCreatedUserData.handle!!
        )

        val joinRoomResponse = chatService.joinRoom(
            chatRoomId = testInputChatRoomId,
            request = testInputJoinRoomRequest
        ).blockingGet()

        val testInputRequest = ExecuteChatCommandRequest(
            command = "Yow Jessy, how are you doin'?",
            userid = testCreatedUserData.userid!!
        )

        try {
            chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
            ).blockingGet()

            // THEN
            assert(true)    // Passed! No Error encountered.

        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid)
        }
    }

    @Test
    fun `D) Notifications`() {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
            userid = RandomString.make(16),
            handle = "${testUserData.handle}_${Random.nextInt(100, 999)}",
            displayname = testUserData.displayname,
            pictureurl = testUserData.pictureurl,
            profileurl = testUserData.profileurl
        )

        // Should create a test chat room first
        var testCreatedUserData: User? = null
        var testCreatedChatRoomData: ChatRoom? = null

        try {
            // Should create a test user first
            testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()

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
            testCreatedChatRoomData = chatService
                .createRoom(testCreateChatRoomInputRequest)
                .blockingGet()

            val testInputJoinChatRoomId = testCreatedChatRoomData?.id!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should join test created chat room
            chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
            ).blockingGet()

            val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should send an initial message to the created chat room
            val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData?.id!!,
                request = testInitialSendMessageInputRequest
            )
                .blockingGet()
                .speech!!

            val testInputChatReplyThreadedRequest = SendThreadedReplyRequest(
                body = "This is Jessy, replying to your greetings yow!!!",
                userid = testCreatedUserData?.userid!!
            )

            // Perform Chat Reply - Threaded
            val testChatReplyThreaded = chatService.sendThreadedReply(
                chatRoomId = testCreatedChatRoomData?.id!!,
                replyTo = testInitialSendMessage.id!!,
                request = testInputChatReplyThreadedRequest
            ).blockingGet()

            // WHEN
            val testActualResult = userService.listUserNotifications(
                userId = testInputChatReplyThreadedRequest.userid,
                limit = 10,
                filterNotificationTypes = listOf(UserNotification.Type.CHAT_REPLY),
                cursor = null,
                includeread = false,
                filterChatRoomId = testCreatedChatRoomData.id
            )
                .blockingGet()

            // THEN
            assert(true)    // Passed! No Error encountered.

        } catch(err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Room
            deleteTestChatRooms(testCreatedChatRoomData?.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData?.userid)
        }
    }

    @Test
    fun `E) Leave Room`() {
        // GIVEN
        val testUserData = ChatServiceTest.TestData.users.first()
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
        ).blockingGet()

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!

        try {
            // WHEN
            chatService.exitRoom(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId
            ).blockingGet()

            // THEN
            assert(true)    // Passed! No Error encountered.
        } catch (err: Throwable) {
            fail(err.message)
        } finally {
            // Perform Delete Test Chat Room
            deleteTestChatRooms(testCreatedChatRoomData.id)
            // Perform Delete Test User
            deleteTestUsers(testCreatedUserData.userid)
        }
    }

    @Test
    fun `F) Delete Room`() {
        // GIVEN
        val testData = ChatServiceTest.TestData.chatRooms(config.appId).first()
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
        val testCreatedChatRoomData = chatService
            .createRoom(testInputRequest)
            .blockingGet()

        // WHEN
        chatService.deleteRoom(
            chatRoomId = testCreatedChatRoomData.id!!
        ).blockingGet()

        // THEN
        assert(true)    // Passed! No Error encountered.
    }

}