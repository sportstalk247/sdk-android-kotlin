package com.sportstalk.reactive.rx2.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.DateUtils
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.users.CreateUpdateUserRequest
import com.sportstalk.datamodels.users.User
import com.sportstalk.reactive.rx2.ServiceFactory
import com.sportstalk.reactive.rx2.api.polling.allEventUpdates
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import kotlinx.serialization.builtins.ArraySerializer
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
import java.util.concurrent.TimeUnit
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
        json = ServiceFactory.RestApi.json
        userService = ServiceFactory.User.get(config)
        chatService = ServiceFactory.Chat.get(config)

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
                            && err.code == 403
                }
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
                kind = Kind.ROOM,
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
        ).blockingGet()

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
    fun `A-ERROR-404-User-not-found) Create Room`() {
        // GIVEN
        val testInputRequest = CreateChatRoomRequest(
                userid = "NON-Existing-User-ID"
        )

        val createRoom = TestObserver<ChatRoom>()

        // WHEN
        chatService.createRoom(request = testInputRequest)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(createRoom)

        // THEN
        createRoom
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-User-not-found - Create Room`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified ownerid ${testInputRequest.userid!!} was not found"
                            && err.code == 404
                }
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
        val testCreatedChatRoomData = chatService
                .createRoom(testInputRequest)
                .blockingGet()

        val testExpectedResult = testCreatedChatRoomData.copy()

        // WHEN
        val testActualResult = chatService.getRoomDetails(
                chatRoomId = testCreatedChatRoomData.id!!
        ).blockingGet()

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
    fun `B-ERROR-404) Get Room Details`() {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val getRoomDetails = TestObserver<ChatRoom>()

        // WHEN
        chatService.getRoomDetails(testInputRoomId)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(getRoomDetails)

        // THEN
        getRoomDetails
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Get Room Details`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified roomId was not found."
                            && err.code == 404
                }
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
        ).blockingGet()

        val testExpectedResult = testCreatedChatRoomData.copy()

        // WHEN
        val testActualResult = chatService.getRoomDetailsByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!
        ).blockingGet()

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
    fun `B-ERROR-404) Get Room Details - By Custom ID`() {
        // GIVEN
        val testInputCustomRoomId = "NON-Existing-Custom-Room-ID"
        val getRoomDetailsByCustomId = TestObserver<ChatRoom>()

        // WHEN
        chatService.getRoomDetailsByCustomId(testInputCustomRoomId)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(getRoomDetailsByCustomId)

        // THEN
        getRoomDetailsByCustomId
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Get Room Details - By Custom ID`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified roomId was not found."
                            && err.code == 404
                }
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
        val testCreatedChatRoomData = chatService
                .createRoom(testInputRequest)
                .blockingGet()

        val testExpectedResult = DeleteChatRoomResponse(
                kind = Kind.DELETED_ROOM,
                deletedEventsCount = 0,
                room = testCreatedChatRoomData
        )

        // WHEN
        val testActualResult = chatService.deleteRoom(
                chatRoomId = testCreatedChatRoomData.id!!
        ).blockingGet()

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
    fun `C-ERROR-404) Delete Room`() {
        // GIVEN
        val testInputCustomRoomId = "NON-Existing-Room-ID"
        val deleteRoom = TestObserver<DeleteChatRoomResponse>()

        // WHEN
        chatService.deleteRoom(testInputCustomRoomId)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(deleteRoom)

        // THEN
        deleteRoom
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Delete Room`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specifed room does not exist."
                            && err.code == 404
                }
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
        ).blockingGet()

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
        ).blockingGet()

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
    fun `D-ERROR-404) Update Room`() {
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
        val updateRoom = TestObserver<ChatRoom>()

        // WHEN
        chatService.updateRoom(
                chatRoomId = testInputRoomId,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(updateRoom)

        // THEN
        updateRoom
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Update Room`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified roomid could not be found: $testInputRoomId"
                            && err.code == 404
                }
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
        val testCreatedChatRoomData = chatService
                .createRoom(testInputRequest)
                .blockingGet()

        val testExpectedResult = ListRoomsResponse(
                kind = Kind.ROOM_LIST,
                rooms = listOf(testCreatedChatRoomData)
        )

        // WHEN
        val testActualResult = chatService.listRooms(
                limit = 100/*,
                cursor = testCreatedChatRoomData.id!!*/
        ).blockingGet()

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

//  Join Room - Anonymous User No Longer Supported
//    @Test
//    fun `G) Join Room - Anonymous User`() {
//        // GIVEN
//        val testChatRoomData = TestData.chatRooms(config.appId).first()
//        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
//                name = testChatRoomData.name!!,
//                customid = testChatRoomData.customid,
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
//        val testCreatedChatRoomData = chatService
//                .createRoom(testCreateChatRoomInputRequest)
//                .blockingGet()
//
//        val testExpectedResult = JoinChatRoomResponse(
//                kind = Kind.JOIN_ROOM,
//                user = null,
//                room = null
//        )
//        // WHEN
//        val testActualResult = chatService.joinRoom(
//                chatRoomIdOrLabel = testCreatedChatRoomData.id!!
//        ).blockingGet()
//
//        // THEN
//        println(
//                "`Join Room - Anonymous User`() -> testActualResult = \n" +
//                        json.encodeToString(
//                                JoinChatRoomResponse.serializer(),
//                                testActualResult
//                        )
//        )
//
//        assertTrue { testActualResult.kind == testExpectedResult.kind }
//        assertTrue { testActualResult.user == null }
//        assertTrue { testActualResult.room == testCreatedChatRoomData }
//
//        // Also, assert that ChatRoomEventCursor is currently stored
//        assertTrue { testActualResult.eventscursor?.cursor == chatService.chatRoomEventCursor[testCreatedChatRoomData.id!!] }
//
//        // Perform Delete Test Chat Room
//        deleteTestChatRooms(testCreatedChatRoomData.id)
//    }

    @Test
    fun `G-ERROR-404-Room-not-found) Join Room`() {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testInputRequest = JoinChatRoomRequest(
                userid = "non-existing-user"
        )

        val joinRoom = TestObserver<JoinChatRoomResponse>()

        // WHEN
        chatService.joinRoom(
                chatRoomId = testInputRoomId,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(joinRoom)

        // THEN
        joinRoom
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified roomid '${testInputRoomId}' was not found."
                            && err.code == 404
                }
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
        val testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()

        val testExpectedResult = JoinChatRoomResponse(
                kind = Kind.JOIN_ROOM,
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
        ).blockingGet()

        val testInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!,
                handle = testCreatedUserData.handle
        )

        // WHEN
        val testActualResult = chatService.joinRoomByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!,
                request = testInputRequest
        ).blockingGet()

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
        assertTrue { testActualResult.room?.customid == testCreatedChatRoomData.customid }

        // Also, assert that ChatRoomEventCursor is currently stored
        assertTrue {
            testActualResult.eventscursor?.cursor?.takeIf { it.isNotEmpty() } ==
                    chatService.getChatRoomEventUpdateCursor(testCreatedChatRoomData.id!!) }

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
        val testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()

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
        val testCreatedChatRoomData = chatService
                .createRoom(testCreateChatRoomInputRequest)
                .blockingGet()

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testInputJoinRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )

        // WHEN
        val testJoinChatRoomData = chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testInputJoinRequest
        ).blockingGet()

        val testExpectedResult = ListChatRoomParticipantsResponse(
                kind = Kind.CHAT_LIST_PARTICIPANTS,
                participants = listOf(
                        ChatRoomParticipant(
                                kind = Kind.CHAT_PARTICIPANT,
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
        ).blockingGet()

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
    fun `I-ERROR-404) Join Room`() {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testInputRequest = JoinChatRoomRequest(
                userid = "non-existing-user"
        )

        val joinRoom = TestObserver<JoinChatRoomResponse>()

        // WHEN
        chatService.joinRoom(
                chatRoomId = testInputRoomId,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(joinRoom)

        // THEN
        joinRoom
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified roomid '${testInputRoomId}' was not found."
                            && err.code == 404
                }
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
        val testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()

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
            println(
                    "`Exit a Room`() -> testActualResult"
            )

            assertTrue { true }
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
    fun `J-ERROR-404) Exit a Room`() {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID"
        val testInputRequest = JoinChatRoomRequest(
                userid = "non-existing-user"
        )

        val joinRoom = TestObserver<JoinChatRoomResponse>()

        // WHEN
        chatService.joinRoom(
                chatRoomId = testInputRoomId,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(joinRoom)

        // THEN
        joinRoom
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-Room-not-found - Join Room`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified roomid '${testInputRoomId}' was not found."
                            && err.code == 404
                }
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
        val testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()

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

        val testSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testExpectedResult = GetUpdatesResponse(
                kind = Kind.CHAT_LIST,
                /*cursor = "",*/
                more = false,
                itemcount = 1,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.getUpdates(
                chatRoomId = testCreatedChatRoomData.id!!/*,
                cursor = null*/
        ).blockingGet()

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
    fun `K-ERROR-404) Get Updates`() {
        // GIVEN
        val testInputRoomId = "NON-Existing-Room-ID-XXX"
        val getUpdates = TestObserver<GetUpdatesResponse>()

        // WHEN
        chatService.getUpdates(
                chatRoomId = testInputRoomId
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(getUpdates)

        // THEN
        getUpdates
                .awaitDone(1000, TimeUnit.MILLISECONDS)
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Get Updates`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified room was not found."
                            && err.code == 404
                }
    }

    @Test
    fun `K-1) All Event Updates`() {
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

        val testSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testExpectedResult = GetUpdatesResponse(
                kind = Kind.CHAT_LIST,
                /*cursor = "",*/
                more = false,
                itemcount = 1,
                events = listOf(testSendMessageData)
        )

        val allEventUpdates = TestObserver<List<ChatEvent>>()
        val chatRoomId = testCreatedChatRoomData.id!!
        chatService.startListeningToChatUpdates(chatRoomId)

        // WHEN
        chatService.allEventUpdates(
                chatRoomId = chatRoomId,
                frequency = 500
        )
                .toObservable()
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnDispose {
                    chatService.stopListeningToChatUpdates(chatRoomId)

                    // Perform Delete Test Chat Room
                    deleteTestChatRooms(testCreatedChatRoomData.id)
                    // Perform Delete Test User
                    deleteTestUsers(testCreatedUserData.userid)
                }
                .subscribe(allEventUpdates)

        // THEN
        allEventUpdates
                .awaitCount(2)
                .assertValueCount(2)
                .assertValueAt(0) { testActualResult ->
                    println(
                            "`All Event Updates[0]`() -> response = \n" +
                                    json.encodeToString(
                                            ArraySerializer(ChatEvent.serializer()),
                                            testActualResult.toTypedArray()
                                    )
                    )

                    return@assertValueAt testActualResult.size == testExpectedResult.itemcount!!.toInt()
                }
                .assertValueAt(1) { testActualResult ->
                    println(
                            "`All Event Updates[1]`() -> response = \n" +
                                    json.encodeToString(
                                            ArraySerializer(ChatEvent.serializer()),
                                            testActualResult.toTypedArray()
                                    )
                    )

                    return@assertValueAt testActualResult.isEmpty()
                }
    }

    @Test
    fun `L) Message Is Reported`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testInputReportMessageRequest = ReportMessageRequest(
                reporttype = "abuse",
                userid = testCreatedUserData.userid!!
        )

        val reportMessageResponse = chatService.reportMessage(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputReportMessageRequest
        ).blockingGet()

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
    fun `M) Message Is Reacted To`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

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
        ).blockingGet()

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
    fun `N) List Previous Events`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

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
        ).blockingGet()

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
    fun `O) Get Event By ID`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputChatEventId = testSendMessageData.id!!
        val testExpectedResult = testSendMessageData.copy()

        // WHEN
        val testActualResult = chatService.getEventById(
                chatRoomId = testInputChatRoomId,
                eventId = testInputChatEventId
        ).blockingGet()

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
    fun `P) List Events History`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

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
        ).blockingGet()

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
    fun `P-1) Execute Chat Command - Speech`() {
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

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = Kind.CHAT_COMMAND,
                op = "speech",
                room = testCreatedChatRoomData,
                speech = ChatEvent(
                        kind = Kind.CHAT,
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
        ).blockingGet()

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
    fun `P-2) Execute Chat Command - Action`() {
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

        val testInputRequest = ExecuteChatCommandRequest(
                // "/high5 {{handle}}"
                command = "/high5 ${testCreatedUserData.handle!!}",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = Kind.CHAT_COMMAND,
                op = "action",
                room = testCreatedChatRoomData,
                speech = null,
                action = ChatEvent(
                        kind = Kind.CHAT,
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
        ).blockingGet()

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
    fun `P-3) Execute Chat Command - Reply to a Message - Threaded`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!


        val testInputRequest = SendThreadedReplyRequest(
                body = "This is Jessy, replying to your greetings yow!!!",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ChatEvent(
                kind = Kind.CHAT,
                roomid = testCreatedChatRoomData.id,
                body = testInputRequest.body,
                eventtype = "reply",
                userid = testCreatedUserData.userid,
                user = testCreatedUserData,
                replyto = testInitialSendMessage
        )

        // WHEN
        val testActualResult = chatService.sendThreadedReply(
                chatRoomId = testCreatedChatRoomData.id!!,
                replyTo = testInitialSendMessage.id!!,
                request = testInputRequest
        ).blockingGet()

        // THEN
        println(
                "`Execute Chat Command - Reply to a Message - Threaded`() -> testActualResult = \n" +
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
    fun `P-4) Execute Chat Command - Purge User Messages`() {
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
        val testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()
        val testCreatedAdminData = userService
                .createOrUpdateUser(request = testCreateAdminInputRequest)
                .blockingGet()

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
        val testAdminJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedAdminData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testAdminJoinRoomInputRequest
        ).blockingGet()

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Send test message
        chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        ).blockingGet()

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
            ).blockingGet()

            // THEN
            println(
                    "`Execute Chat Command - Purge User Messages`() -> testActualResult = \n" +
                            json.encodeToString(
                                    ExecuteChatCommandResponse.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.message == testExpectedResult.message }
        } catch (err: Throwable) {
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
    fun `P-5) Execute Chat Command - Admin Command`() {
        // TODO:: Admin password is hardcoded as "zola".
    }

    @Test
    fun `P-6) Execute Chat Command - Admin - Delete All Events`() {
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

        val testInputRequest = ExecuteChatCommandRequest(
                command = "*deleteallevents ${TestData.ADMIN_PASSWORD}",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = Kind.CHAT_COMMAND,
                op = "admin",
                room = null,
                speech = null,
                action = null
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        ).blockingGet()

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
    fun `P-7) Send Quoted Reply`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send an initial message to the created chat room
        val testInitialSendMessage = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!


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
        ).blockingGet()

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
    fun `P-8) Execute Chat Command - Announcement`() {
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

        val testInputRequest = ExecuteChatCommandRequest(
                command = "This is a test announcement!",
                userid = testCreatedUserData.userid!!,
                eventtype = EventType.ANNOUNCEMENT
        )
        val testExpectedResult = ExecuteChatCommandResponse(
                kind = Kind.CHAT_COMMAND,
                op = EventType.SPEECH,
                room = testCreatedChatRoomData,
                speech = ChatEvent(
                        kind = Kind.CHAT,
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
        ).blockingGet()

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
    fun `P-ERROR-404-User-NOT-found) Execute Chat Command`() {
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
        val testCreatedChatRoomData = chatService
                .createRoom(testCreateChatRoomInputRequest)
                .blockingGet()

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow error test",
                userid = testInputUserId
        )

        val executeChatCommand = TestObserver<ExecuteChatCommandResponse>()

        // WHEN
        chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnDispose {
                    // Perform Delete Test Chat Room
                    deleteTestChatRooms(testCreatedChatRoomData.id)
                }
                .subscribe(executeChatCommand)

        // THEN
        executeChatCommand
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-User-NOT-found - Execute Chat Command`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified user was not found"
                            && err.code == 404
                }
    }

    @Test
    fun `P-ERROR-412-User-not-yet-joined) Execute Chat Command`() {
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

        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow error test",
                userid = testCreatedUserData.userid!!
        )

        val executeChatCommand = TestObserver<ExecuteChatCommandResponse>()

        // WHEN
        chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnDispose {
                    // Perform Delete Test Chat Room
                    deleteTestChatRooms(testCreatedChatRoomData.id)
                    // Perform Delete Test User
                    deleteTestUsers(testCreatedUserData.userid)
                }
                .subscribe(executeChatCommand)

        // THEN
        executeChatCommand
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-User-not-yet-joined - Execute Chat Command`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "A user cannot execute commands in a room unless the user has joined the room."
                            && err.code == 412
                }
    }

    @Test
    fun `P-ERROR-404-REPLY-NOT-FOUND) Execute Chat Command`() {
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
        val testCreatedUserData = userService
                .createOrUpdateUser(request = testCreateUserInputRequest)
                .blockingGet()

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

        val testReplyToIdNonExisting = "non-existing-ID"
        val testInputRequest = ExecuteChatCommandRequest(
                command = "Yow error test",
                userid = testCreatedUserData.userid!!,
                replyto = testReplyToIdNonExisting
        )

        val executeChatCommand = TestObserver<ExecuteChatCommandResponse>()

        // WHEN
        chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnDispose {
                    // Perform Delete Test Chat Room
                    deleteTestChatRooms(testCreatedChatRoomData.id)
                    // Perform Delete Test User
                    deleteTestUsers(testCreatedUserData.userid)
                }
                .subscribe(executeChatCommand)

        // THEN
        executeChatCommand
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-REPLY-NOT-FOUND - Execute Chat Command`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The message you want to reply to can't be found."
                            && err.code == 404
                            && err.data?.get("kind") == Kind.CHAT_COMMAND
                            && err.data?.get("op") == "speech"
                }
    }

    @Test
    fun `Q) List Messages By User`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!
        val testInputLimit = 10
        val testExpectedResult = ListMessagesByUser(
                kind = Kind.CHAT_LIST,
                events = listOf(testSendMessageData)
        )

        // WHEN
        val testActualResult = chatService.listMessagesByUser(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId,
                limit = testInputLimit
        ).blockingGet()

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
    fun `R) Bounce User - Ban user`() {
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
        ).blockingGet()

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
    fun `S) Search Event History`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testInputUserId = testSendMessageData.userid!!
        val testInputEventTypes = listOf(EventType.SPEECH)
        val testInputRequest = SearchEventHistoryRequest(
                fromuserid = testInputUserId,
                types = testInputEventTypes,
                limit = 10
        )

        try {
            // WHEN
            val testActualResult = chatService.searchEventHistory(
                    request = testInputRequest
            ).blockingGet()

            // THEN
            println(
                    "`Search Event History`() -> testActualResult = \n" +
                            json.encodeToString(
                                    SearchEventHistoryResponse.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.kind == Kind.CHAT_LIST }
            assertTrue {
                testActualResult.events.any { _ev ->
                    _ev.id == testSendMessageData.id
                }
            }
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
    fun `T) Update Chat Message`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputEventId = testSendMessageData.id!!
        val testInputUserId = testSendMessageData.userid!!
        val testInputUpdatedBody = "Updated! Jessy yow!!!"
        val testInputRequest = UpdateChatMessageRequest(
                userid = testInputUserId,
                body = testInputUpdatedBody
        )
        val testExpectedResult = testSendMessageData.copy(
                id = testSendMessageData.id,
                body = testInputUpdatedBody
        )

        // WHEN
        val testActualResult = chatService.updateChatMessage(
                chatRoomId = testInputChatRoomId,
                eventId = testInputEventId,
                request = testInputRequest
        ).blockingGet()

        // THEN
        println(
                "`Update Chat Message`() -> testActualResult = \n" +
                        json.encodeToString(
                                ChatEvent.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.id == testInputEventId }
        assertTrue { testActualResult.body == testInputUpdatedBody }
        assertTrue { testActualResult.roomid == testInputChatRoomId }
        assertTrue { testActualResult.userid == testInputUserId }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `U) Delete Event`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

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
        val testActualResult = chatService.permanentlyDeleteEvent(
                chatRoomId = testInputChatRoomId,
                eventId = testInputEventId,
                userid = testInputUserId
        ).blockingGet()

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
    fun `V) Flag Message Event as Deleted`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

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
        val testActualResult = chatService.flagEventLogicallyDeleted(
                chatRoomId = testInputChatRoomId,
                eventId = testInputEventId,
                userid = testInputUserId,
                deleted = testInputDeleted,
                permanentifnoreplies = testInputPermanentIfNoReplies
        ).blockingGet()

        // THEN
        println(
                "`Flag Message Event as Deleted`() -> testActualResult = \n" +
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
    fun `W) Report a Message`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

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
        ).blockingGet()

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
    fun `W-ERROR-404-EVENT-NOT-FOUND) Report a Message`() {
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

        val testChatIdNonExisting = "non-existing-ID"
        val testInputRequest = ReportMessageRequest(
                reporttype = "abuse",
                userid = testCreatedUserData.userid!!
        )

        val reportMessage = TestObserver<ChatEvent>()

        // WHEN
        chatService.reportMessage(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testChatIdNonExisting,
                request = testInputRequest
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .doOnDispose {
                    // Perform Delete Test Chat Room
                    deleteTestChatRooms(testCreatedChatRoomData.id)
                    // Perform Delete Test User
                    deleteTestUsers(testCreatedUserData.userid)
                }
                .subscribe(reportMessage)

        // THEN
        reportMessage
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404-EVENT-NOT-FOUND - Report a Message`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified event was not found."
                            && err.code == 404
                            && err.data?.get("eventId") == testChatIdNonExisting
                }
    }

    @Test
    fun `X) React to a Message`() {
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

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        )
                .blockingGet()
                .speech!!

        val testInputRequest = ReactToAMessageRequest(
                userid = testCreatedUserData.userid!!,
                reaction = EventReaction.LIKE,
                reacted = true
        )
        val testExpectedResult = testSendMessageData.copy(
                kind = Kind.CHAT,
                roomid = testCreatedChatRoomData.id,
                eventtype = EventType.SPEECH,
                userid = testInputRequest.userid,
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

        // WHEN
        val testActualResult = chatService.reactToEvent(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputRequest
        ).blockingGet()

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

        /* Assert each reaction from event response */
        assertTrue {
            testActualResult.reactions.any { rxn ->
                testExpectedResult.reactions.any { expectedRxn ->
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