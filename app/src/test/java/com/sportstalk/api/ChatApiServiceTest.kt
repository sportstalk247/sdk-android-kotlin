package com.sportstalk.api

import android.app.Activity
import android.content.Context
import com.sportstalk.Dependencies
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.User
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.serializer
import net.bytebuddy.utility.RandomString
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import kotlin.test.assertTrue

@UnstableDefault
@ImplicitReflectionSerializer
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
class ChatApiServiceTest {

    private lateinit var context: Context
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit
    private lateinit var usersApiService: UsersApiService
    private lateinit var chatApiService: ChatApiService
    private lateinit var json: Json
    private lateinit var appId: String

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get().applicationContext
        val apiUrlEndpoint = Dependencies.ApiEndpoint.getInstance(context)!!
        val authToken = Dependencies.AuthToken.getInstance(context)!!
        appId = Dependencies.AppId.getInstance(context)!!
        okHttpClient = Dependencies._OkHttpClient.getInstance(authToken)
        json = Json(
                JsonBuilder()
                        .apply {
                            prettyPrint = false
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                        .buildConfiguration()
        )
        retrofit = Dependencies._Retrofit.getInstance(apiUrlEndpoint, okHttpClient, json)
        usersApiService = Dependencies.ApiServices.Users.getInstance(
                appId = appId,
                retrofit = retrofit
        )
        chatApiService = Dependencies.ApiServices.Chat.getInstance(
                appId = appId,
                retrofit = retrofit
        )
    }

    @After
    fun cleanUp() {
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private fun deleteTestUsers(vararg userIds: String?) {
        for(id in userIds) {
            id ?: continue
            usersApiService.deleteUser(userId = id).get()
        }
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private fun deleteTestChatRooms(vararg chatRoomIds: String?) {
        for(id in chatRoomIds) {
            id ?: continue
            chatApiService.deleteRoom(chatRoomId = id).get()
        }
    }

    @Test
    fun `1) Create Room`() {
        // GIVEN
        val testExpectedData = TestData.chatRooms(appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testExpectedData.name!!,
                slug = testExpectedData.slug,
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
                        slug = testExpectedData.slug,
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
        val testActualResult = chatApiService.createRoom(
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Create Room`() -> testActualResult = " +
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
        assertTrue { testActualResult.data?.slug == testExpectedResult.data?.slug }
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
    fun `2) Get Room Details`() {
        // GIVEN
        val testData = TestData.chatRooms(appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testData.name!!,
                slug = testData.slug,
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
        val testCreatedChatRoomData = chatApiService.createRoom(testInputRequest).get().data!!

        val testExpectedResult = ApiResponse<ChatRoom>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = testCreatedChatRoomData
        )

        // WHEN
        val testActualResult = chatApiService.getRoomDetails(
                chatRoomId = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Get Room Details`() -> testActualResult = " +
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
    fun `3) Delete Room`() {
        // GIVEN
        val testData = TestData.chatRooms(appId).first()
        val testInputRequest = CreateChatRoomRequest(
                name = testData.name!!,
                slug = testData.slug,
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
        val testCreatedChatRoomData = chatApiService.createRoom(testInputRequest).get().data!!

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
        val testActualResult = chatApiService.deleteRoom(
                chatRoomId = testCreatedChatRoomData.id!!
        ).get()

        // THEN
        println(
                "`Delete Room`() -> testActualResult = " +
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
    fun `4) Update Room`() {
        // GIVEN
        val testData = TestData.chatRooms(appId).first()
        // Should create a test chat room first
        val testCreatedChatRoomData = chatApiService.createRoom(
                request = CreateChatRoomRequest(
                        name = testData.name!!,
                        slug = testData.slug,
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

        val testInputRequest = UpdateChatRoomRequest(
                roomid = testCreatedChatRoomData.id!!,
                name = "${testData.name!!}-updated",
                slug = "${testData.slug}-updated(${System.currentTimeMillis()})",
                description = "${testData.description}-updated",
                enableactions = !testData.enableactions!!,
                enableenterandexit = !testData.enableenterandexit!!,
                throttle = 1_000L
        )

        val testExpectedResult = ApiResponse<ChatRoom>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = testCreatedChatRoomData.copy(
                        id = testInputRequest.roomid,
                        name = testInputRequest.name,
                        slug = testInputRequest.slug,
                        description = testInputRequest.description,
                        enableactions = testInputRequest.enableactions,
                        enableenterandexit = testInputRequest.enableenterandexit
                )
        )

        // WHEN
        val testActualResult = chatApiService.updateRoom(
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Update Room`() -> testActualResult = " +
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
        assertTrue { testActualResult.data?.slug == testExpectedResult.data?.slug }
        assertTrue { testActualResult.data?.description == testExpectedResult.data?.description }
        assertTrue { testActualResult.data?.enableactions == testExpectedResult.data?.enableactions }
        assertTrue { testActualResult.data?.enableenterandexit == testExpectedResult.data?.enableenterandexit }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testActualResult.data?.id)
    }

    @Test
    fun `5) Join Room - Authenticated User`() {
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
        val testCreatedUserData = usersApiService.createUpdateUser(request = testCreateUserInputRequest).get().data!!

        val testChatRoomData = TestData.chatRooms(appId).first()
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
                name = testChatRoomData.name!!,
                slug = testChatRoomData.slug,
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
        val testCreatedChatRoomData = chatApiService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testExpectedResult = ApiResponse<JoinChatRoomResponse>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = JoinChatRoomResponse(
                        kind = "chat.joinroom",
                        user = testCreatedUserData,
                        room = testCreatedChatRoomData.copy(inroom = testCreatedChatRoomData.inroom!! + 1)
                )
        )

        val testInputRequest = JoinChatRoomRequest(
                roomid = testCreatedChatRoomData.id!!,
                userid = testCreatedUserData.userid!!
        )

        // WHEN
        val testActualResult = chatApiService.joinRoom(
                request = testInputRequest
        ).get()

        // THEN
        println(
                "`Join Room - Authenticated User`() -> testActualResult = " +
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
        assertTrue { testActualResult.data?.room == testExpectedResult.data?.room }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
    }

    @Test
    fun `6) Join Room - Anonymous User`() {
        // GIVEN
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

        val testInputChatRoomIdOrLabel = "random_join_chat_room_label"

        // WHEN
        val testActualResult = chatApiService.joinRoom(
                chatRoomIdOrLabel = testInputChatRoomIdOrLabel
        ).get()

        // THEN
        println(
                "`Join Room - Anonymous User`() -> testActualResult = " +
                        json.stringify(
                                ApiResponse.serializer(JoinChatRoomResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.user == null }
        assertTrue { testActualResult.data?.room == null }
    }

    @Test
    fun `7) List Room Participants`() {
        // GIVEN
        val testUserData = TestData.users.first()
        val testCreateUserInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "${testUserData.handle}-${System.currentTimeMillis()}",
                displayname = testUserData.displayname,
                pictureurl = testUserData.pictureurl,
                profileurl = testUserData.profileurl
        )
        // Should create a test user first
        val testCreatedUserData = usersApiService.createUpdateUser(request = testCreateUserInputRequest).get().data!!

        val testChatRoomData = TestData.chatRooms(appId).first()
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
                name = testChatRoomData.name!!,
                slug = testChatRoomData.slug,
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
        val testCreatedChatRoomData = chatApiService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testInputJoinRequest = JoinChatRoomRequest(
                roomid = testCreatedChatRoomData.id!!,
                userid = testCreatedUserData.userid!!
        )

        // WHEN
        val testJoinChatRoomData = chatApiService.joinRoom(request = testInputJoinRequest).get().data!!

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
        val testActualResult = chatApiService.listRoomParticipants(
                chatRoomId = testInputChatRoomId,
                limit = testInputLimit
        ).get()

        // THEN
        println(
                "`List Room Participants`() -> testActualResult = " +
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
    fun `8) Exit a Room`() {
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
        val testCreatedUserData = usersApiService.createUpdateUser(request = testCreateUserInputRequest).get().data!!

        val testChatRoomData = TestData.chatRooms(appId).first()
        val testCreateChatRoomInputRequest = CreateChatRoomRequest(
                name = testChatRoomData.name!!,
                slug = testChatRoomData.slug,
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
        val testCreatedChatRoomData = chatApiService.createRoom(testCreateChatRoomInputRequest).get().data!!

        val testJoinRoomInputRequest = JoinChatRoomRequest(
                roomid = testCreatedChatRoomData.id!!,
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should join test created chat room
        chatApiService.joinRoom(request = testJoinRoomInputRequest).get().data!!

        val testExpectedResult = ApiResponse<Any>(
                kind = "api.result",
                message = "Success",
                code = 200
        )

        val testInputChatRoomId = testCreatedChatRoomData.id!!
        val testInputUserId = testCreatedUserData.userid!!

        // WHEN
        val testActualResult = chatApiService.exitRoom(
                chatRoomId = testInputChatRoomId,
                userId = testInputUserId
        ).get()

        // THEN
        println(
                "`Exit a Room`() -> testActualResult = " +
                        json.stringify(
                                ApiResponse.serializer(ExitChatRoomResponse.serializer() ),
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

    object TestData {
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
                if(_chatRooms != null) _chatRooms!!
                else listOf(
                        ChatRoom(
                                kind = "chat.room",
                                id = RandomString.make(16),
                                appid = appId,
                                ownerid = null,
                                name = "Test Chat Room 1",
                                description = "This is a test chat room 1.",
                                iframeurl = null,
                                slug = "test-room-1",
                                enableactions = true,
                                enableenterandexit = true,
                                open = true,
                                inroom = 1,
                                added = System.currentTimeMillis(),
                                whenmodified = System.currentTimeMillis(),
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
                                iframeurl = null,
                                slug = "test-room-2",
                                enableactions = false,
                                enableenterandexit = false,
                                open = false,
                                inroom = 1,
                                added = System.currentTimeMillis(),
                                whenmodified = System.currentTimeMillis(),
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
                                iframeurl = null,
                                slug = "test-room-3",
                                enableactions = true,
                                enableenterandexit = true,
                                open = false,
                                inroom = 1,
                                added = System.currentTimeMillis(),
                                whenmodified = System.currentTimeMillis(),
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
                                iframeurl = null,
                                slug = "test-room-4",
                                enableactions = false,
                                enableenterandexit = false,
                                open = true,
                                inroom = 1,
                                added = System.currentTimeMillis(),
                                whenmodified = System.currentTimeMillis(),
                                moderation = "post",
                                maxreports = 0L,
                                enableprofanityfilter = true,
                                delaymessageseconds = 0L
                        )
                )
    }

}