package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.DateUtils
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.service.UserService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.chat.*
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.User
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
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
    }

    @After
    fun cleanUp() {
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

        val testExpectedResult = ApiResponse<ChatRoom>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = ChatRoom(
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
        )

        // WHEN
        val testActualResult = chatService.createRoom(
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Create Room`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ChatRoom.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.appid == testExpectedResult.data?.appid }
        assertTrue { testActualResult.data?.name == testExpectedResult.data?.name }
        assertTrue { testActualResult.data?.customid == testExpectedResult.data?.customid }
        assertTrue { testActualResult.data?.description == testExpectedResult.data?.description }
        assertTrue { testActualResult.data?.moderation == testExpectedResult.data?.moderation }
        assertTrue { testActualResult.data?.enableactions == testExpectedResult.data?.enableactions }
        assertTrue { testActualResult.data?.enableenterandexit == testExpectedResult.data?.enableenterandexit }
        assertTrue { testActualResult.data?.enableprofanityfilter == testExpectedResult.data?.enableprofanityfilter }
        assertTrue { testActualResult.data?.delaymessageseconds == testExpectedResult.data?.delaymessageseconds }
        assertTrue { testActualResult.data?.open == testExpectedResult.data?.open }
        assertTrue { testActualResult.data?.maxreports == testExpectedResult.data?.maxreports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.data?.id)
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
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest).get().data!!

        val testExpectedResult = ApiResponse<ChatRoom>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = testCreatedChatRoomData
        )

        // WHEN
        val testActualResult = chatService.getRoomDetails(
                chatRoomId = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Get Room Details`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ChatRoom.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data == testExpectedResult.data }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.data?.id)
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
        ).get().data!!

        val testExpectedResult = ApiResponse<ChatRoom>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = testCreatedChatRoomData
        )

        // WHEN
        val testActualResult = chatService.getRoomDetailsByCustomId(
                chatRoomCustomId = testCreatedChatRoomData.customid!!
        ).get()

        // THEN
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.id == testExpectedResult.data?.id }
        assertTrue { testActualResult.data?.name == testExpectedResult.data?.name }
        assertTrue { testActualResult.data?.description == testExpectedResult.data?.description }
        assertTrue { testActualResult.data?.customid == testExpectedResult.data?.customid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
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
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest).get().data!!

        val testExpectedResult = ApiResponse<DeleteChatRoomResponse>(
                kind = "api.result",
                message = "Room deleted successfully.",
                code = 200,
                data = DeleteChatRoomResponse(
                        kind = "deleted.room",
                        deletedEventsCount = 0,
                        room = testCreatedChatRoomData
                )
        )

        // WHEN
        val testActualResult = chatService.deleteRoom(
                chatRoomId = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Delete Room`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(DeleteChatRoomResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.room == testExpectedResult.data?.room }
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
        ).get().data!!

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputRequest = UpdateChatRoomRequest(
                name = "${testData.name!!}-updated",
                customid = "${testData.customid}-updated(${System.currentTimeMillis()})",
                description = "${testData.description}-updated",
                enableactions = !testData.enableactions!!,
                enableenterandexit = !testData.enableenterandexit!!,
                maxreports = 30L
        )

        val testExpectedResult = ApiResponse<ChatRoom>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = testCreatedChatRoomData.copy(
                        name = testInputRequest.name,
                        customid = testInputRequest.customid,
                        description = testInputRequest.description,
                        enableactions = testInputRequest.enableactions,
                        enableenterandexit = testInputRequest.enableenterandexit,
                        maxreports = testInputRequest.maxreports
                )
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
                                ApiResponse.serializer(ChatRoom.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.id == testExpectedResult.data?.id }
        assertTrue { testActualResult.data?.name == testExpectedResult.data?.name }
        assertTrue { testActualResult.data?.customid == testExpectedResult.data?.customid }
        assertTrue { testActualResult.data?.description == testExpectedResult.data?.description }
        assertTrue { testActualResult.data?.enableactions == testExpectedResult.data?.enableactions }
        assertTrue { testActualResult.data?.enableenterandexit == testExpectedResult.data?.enableenterandexit }
        assertTrue { testActualResult.data?.maxreports == testExpectedResult.data?.maxreports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.data?.id)
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
        val testCreatedChatRoomData = chatService.createRoom(testInputRequest).get().data!!

        val testExpectedResult = ApiResponse<ListRoomsResponse>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = ListRoomsResponse(
                        kind = "list.chatrooms",
                        rooms = listOf(testCreatedChatRoomData)
                )
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
                                ApiResponse.serializer(ListRoomsResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.rooms!!.containsAll(testExpectedResult.data?.rooms!!) }

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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testExpectedResult = ApiResponse<JoinChatRoomResponse>(
                kind = "api.result",
                message = "Successfully joined as anonymous user",
                code = 200,
                data = JoinChatRoomResponse(
                        kind = "chat.joinroom",
                        user = testCreatedUserData,
                        room = testCreatedChatRoomData.copy(inroom = testCreatedChatRoomData.inroom!! + 1)
                )
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
                                ApiResponse.serializer(JoinChatRoomResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.user?.userid == testExpectedResult.data?.user?.userid }
        assertTrue { testActualResult.data?.user?.handle == testExpectedResult.data?.user?.handle }
        assertTrue { testActualResult.data?.room?.id == testExpectedResult.data?.room?.id }
        assertTrue { testActualResult.data?.room?.name == testExpectedResult.data?.room?.name }
        assertTrue { testActualResult.data?.room?.description == testExpectedResult.data?.room?.description }

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testExpectedResult = ApiResponse<JoinChatRoomResponse>(
                kind = "api.result",
                message = "Successfully joined as anonymous user",
                code = 200,
                data = JoinChatRoomResponse(
                        kind = "chat.joinroom",
                        user = null,
                        room = null
                )
        )

        // WHEN
        val testActualResult = chatService.joinRoom(
                chatRoomIdOrLabel = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Join Room - Anonymous User`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(JoinChatRoomResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.user == null }
        assertTrue { testActualResult.data?.room == testCreatedChatRoomData }
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

        val testExpectedResult = ApiResponse<JoinChatRoomResponse>(
                kind = "api.result",
                message = "Successfully joined as anonymous user",
                code = 200,
                data = JoinChatRoomResponse(
                        kind = "chat.joinroom",
                        user = testCreatedUserData/*,
                        room = null*/
                )
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
        ).get().data!!

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
                                ApiResponse.serializer(JoinChatRoomResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.user?.userid == testExpectedResult.data?.user?.userid }
        assertTrue { testActualResult.data?.user == testExpectedResult.data?.user }
        assertTrue { testActualResult.data?.room?.customid == testCreatedChatRoomData.customid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.data?.room?.id)
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testInputJoinRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )

        // WHEN
        val testJoinChatRoomData = chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testInputJoinRequest
        ).get().data!!

        val testExpectedResult = ApiResponse<ListChatRoomParticipantsResponse>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = ListChatRoomParticipantsResponse(
                        kind = "list.chatparticipants",
                        participants = listOf(
                                ChatRoomParticipant(
                                        kind = "chat.participant",
                                        user = testJoinChatRoomData.user!!
                                )
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
                                ApiResponse.serializer(ListChatRoomParticipantsResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.participants?.first()?.kind == testExpectedResult.data?.participants?.first()?.kind }
        assertTrue { testActualResult.data?.participants?.first()?.user == testExpectedResult.data?.participants?.first()?.user }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testInputJoinChatRoomId = testCreatedChatRoomData.id!!
        val testJoinRoomInputRequest = JoinChatRoomRequest(
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatService.joinRoom(
                chatRoomId = testInputJoinChatRoomId,
                request = testJoinRoomInputRequest
        ).get().data!!

        val testExpectedResult = ApiResponse<Any>(
                kind = "api.result",
                message = "Success",
                code = 200
        )

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!

        // WHEN
        val testActualResult = chatService.exitRoom(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId
        ).get()

        // THEN
        println(
                "`Exit a Room`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ExitChatRoomResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data == null }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        ).get().data?.speech!!

        val testExpectedResult = ApiResponse<GetUpdatesResponse>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = GetUpdatesResponse(
                        kind = "list.chatevents",
                        /*cursor = "",*/
                        more = false,
                        itemcount = 1,
                        room = testCreatedChatRoomData,
                        events = listOf(testSendMessageData)
                )
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
                                ApiResponse.serializer(GetUpdatesResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.itemcount!! >= testExpectedResult.data?.itemcount!! }
        assertTrue { testActualResult.data?.more == testExpectedResult.data?.more }
        assertTrue { testActualResult.data?.room?.id == testExpectedResult.data?.room?.id }
        assertTrue {
            testActualResult.data?.events!!.any { ev ->
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
    fun `L-1) Execute Chat Command - Speech`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        val testExpectedResult = ApiResponse<ExecuteChatCommandResponse>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = ExecuteChatCommandResponse(
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
                                ApiResponse.serializer(ExecuteChatCommandResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.op == testExpectedResult.data?.op }
        assertTrue { testActualResult.data?.room?.id == testExpectedResult.data?.room?.id }
        assertTrue { testActualResult.data?.speech?.kind == testExpectedResult.data?.speech?.kind }
        assertTrue { testActualResult.data?.speech?.roomid == testExpectedResult.data?.speech?.roomid }
        assertTrue { testActualResult.data?.speech?.body == testExpectedResult.data?.speech?.body }
        assertTrue { testActualResult.data?.speech?.eventtype == testExpectedResult.data?.speech?.eventtype }
        assertTrue { testActualResult.data?.speech?.userid == testExpectedResult.data?.speech?.userid }
        assertTrue { testActualResult.data?.speech?.user?.userid == testExpectedResult.data?.speech?.user?.userid }
        assertTrue { testActualResult.data?.action == testExpectedResult.data?.action }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `L-2) Execute Chat Command - Action`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
                // "/high5 {{handle}}"
                command = "/high5 ${testCreatedUserData.handle!!}",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ApiResponse<ExecuteChatCommandResponse>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = ExecuteChatCommandResponse(
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
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Execute Chat Command - Action`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ExecuteChatCommandResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.op == testExpectedResult.data?.op }
        assertTrue { testActualResult.data?.room?.id == testExpectedResult.data?.room?.id }
        assertTrue { testActualResult.data?.speech == null }
        assertTrue { testActualResult.data?.action?.kind == testExpectedResult.data?.action?.kind }
        assertTrue { testActualResult.data?.action?.roomid == testExpectedResult.data?.action?.roomid }
        assertTrue { testActualResult.data?.action?.body == testExpectedResult.data?.action?.body }
        assertTrue { testActualResult.data?.action?.eventtype == testExpectedResult.data?.action?.eventtype }
        assertTrue { testActualResult.data?.action?.userid == testExpectedResult.data?.action?.userid }
        assertTrue { testActualResult.data?.action?.user?.userid == testExpectedResult.data?.action?.user?.userid }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `L-3) Execute Chat Command - Reply to a Message`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        ).get().data?.speech!!


        val testInputRequest = ExecuteChatCommandRequest(
                command = "This is Jessy, replying to your greetings yow!!!",
                userid = testCreatedUserData.userid!!,
                replyto = testInitialSendMessage.id!!
        )
        val testExpectedResult = ApiResponse<ExecuteChatCommandResponse>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = ExecuteChatCommandResponse(
                        kind = "chat.executecommand",
                        op = "speech",
                        room = testCreatedChatRoomData,
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
        )

        // WHEN
        val testActualResult = chatService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Execute Chat Command - Reply to a Message`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ExecuteChatCommandResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.op == testExpectedResult.data?.op }
        assertTrue { testActualResult.data?.room?.id == testExpectedResult.data?.room?.id }
        assertTrue { testActualResult.data?.speech?.roomid == testExpectedResult.data?.speech?.roomid }
        assertTrue { testActualResult.data?.speech?.body == testExpectedResult.data?.speech?.body }
        assertTrue { testActualResult.data?.speech?.eventtype == testExpectedResult.data?.speech?.eventtype }
        assertTrue { testActualResult.data?.speech?.userid == testExpectedResult.data?.speech?.userid }
        assertTrue { testActualResult.data?.speech?.user?.userid == testExpectedResult.data?.speech?.user?.userid }
        assertTrue { testActualResult.data?.speech?.replyto?.id == testExpectedResult.data?.speech?.replyto?.id }
        assertTrue { testActualResult.data?.speech?.replyto?.kind == testExpectedResult.data?.speech?.replyto?.kind }
        assertTrue { testActualResult.data?.speech?.replyto?.body == testExpectedResult.data?.speech?.replyto?.body }
        assertTrue { testActualResult.data?.action == null }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `L-4) Execute Chat Command - Purge User Messages`() {
        // TODO:: Admin password is hardcoded as "zola".
    }

    @Test
    fun `L-5) Execute Chat Command - Admin Command`() {
        // TODO:: Admin password is hardcoded as "zola".
    }

    @Test
    fun `L-6) Execute Chat Command - Admin - Delete All Events`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        val testExpectedResult = ApiResponse<ExecuteChatCommandResponse>(
                kind = "api.result",
                message = "Deleted 0 events.",
                code = 200,
                data = ExecuteChatCommandResponse(
                        kind = "chat.executecommand",
                        op = "admin",
                        room = null,
                        speech = null,
                        action = null
                )
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
                                ApiResponse.serializer(ExecuteChatCommandResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data == testExpectedResult.data }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `O) List Messages By User`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        ).get().data?.speech!!

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!
        val testInputLimit = 10
        val testExpectedResult = ApiResponse<ListMessagesByUser>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = ListMessagesByUser(
                        kind = "list.chatevents",
                        events = listOf(testSendMessageData)
                )
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
                                ApiResponse.serializer(ListMessagesByUser.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue {
            testActualResult.data?.events!!.any { ev ->
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
    fun `P) Remove a Message`() {
        // TODO:: `Removes a message` API is broken at the moment
    }

    @Test
    fun `Q) Report a Message`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        ).get().data?.speech!!

        val testInputRequest = ReportMessageRequest(
                reporttype = "abuse",
                userid = testCreatedUserData.userid!!
        )
        val testExpectedResult = ApiResponse<ChatEvent>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = testSendMessageData.copy(
                        active = false,
                        moderation = "flagged",
                        reports = listOf(
                                ChatEventReport(
                                        userid = testInputRequest.userid,
                                        reason = testInputRequest.reporttype
                                )
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
                                ApiResponse.serializer(ChatEvent.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.id == testExpectedResult.data?.id }
        assertTrue { testActualResult.data?.active == testExpectedResult.data?.active }
        assertTrue { testActualResult.data?.moderation == testExpectedResult.data?.moderation }
        assertTrue { testActualResult.data?.reports == testExpectedResult.data?.reports }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `R) React to a Message`() {
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
        val testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest).get().data!!

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
        val testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest).get().data!!

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
        ).get().data?.speech!!

        val testInputRequest = ReactToAMessageRequest(
                userid = testCreatedUserData.userid!!,
                reaction = "like",
                reacted = true
        )
        val testExpectedResult = ApiResponse<ChatEvent>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = ChatEvent(
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
        )

        // WHEN
        val testActualResult = chatService.reactToAMessage(
                chatRoomId = testCreatedChatRoomData.id!!,
                eventId = testSendMessageData.id!!,
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`React to a Message`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ChatEvent.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.body == testExpectedResult.data?.body }
        assertTrue { testActualResult.data?.eventtype == testExpectedResult.data?.eventtype }
        assertTrue { testActualResult.data?.userid == testExpectedResult.data?.userid }
        assertTrue { testActualResult.data?.replyto?.id == testExpectedResult.data?.replyto?.id }
        /* Assert each reaction from event response */
        assertTrue {
            testActualResult.data?.replyto?.reactions!!.any { rxn ->
                testExpectedResult.data?.replyto?.reactions!!.any { expectedRxn ->
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