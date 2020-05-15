package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.os.Build
import com.sportstalk.Dependencies
import com.sportstalk.SportsTalkManager
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import com.sportstalk.models.chat.moderation.ApproveMessageRequest
import com.sportstalk.models.chat.moderation.ListMessagesNeedingModerationResponse
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
import kotlin.test.assertTrue

@UnstableDefault
@ImplicitReflectionSerializer
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ChatModerationApiServiceTest {

    private lateinit var context: Context
    private lateinit var json: Json
    private lateinit var usersApiService: UsersApiService
    private lateinit var chatApiService: ChatApiService
    private lateinit var chatModerationApiService: ChatModerationApiService
    private lateinit var appId: String

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get().applicationContext
        val sportsTalkManager = SportsTalkManager.init(context)
        json = Dependencies._Json.getInstance()
        appId = Dependencies.AppId.getInstance(context)!!
        usersApiService = sportsTalkManager.usersApiService
        chatApiService = sportsTalkManager.chatApiService
        chatModerationApiService = sportsTalkManager.chatModerationApiService
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
            usersApiService.deleteUser(userId = id).get()
        }
    }

    /**
     * Helper function to clean up Test Users from the Backend Server
     */
    private fun deleteTestChatRooms(vararg chatRoomIds: String?) {
        for (id in chatRoomIds) {
            id ?: continue
            chatApiService.deleteRoom(chatRoomId = id).get()
        }
    }

    @Test
    fun `A) Approve Message`() {
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
                // Moderation MUST BE SET to "pre"
                .copy(moderation = "pre")
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
        chatApiService.joinRoom(request = testJoinRoomInputRequest).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatApiService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().data?.speech!!

        val testInputRequest = ApproveMessageRequest(
                approve = true
        )
        val testExpectedResult = ApiResponse<ChatEvent>(
                kind = "api.result",
                /*message = "",*/
                code = 200,
                data = testSendMessageData.copy(
                        moderation = "approved"
                )
        )

        // WHEN
        val testActualResult = chatModerationApiService.approveMessage(
                eventId = testSendMessageData.id!!,
                approve = testInputRequest.approve
        ).get()

        // THEN
        println(
                "`Approve Message`() -> testActualResult = \n" +
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
        assertTrue { testActualResult.data?.moderation == testExpectedResult.data?.moderation }

        // Perform Delete Test Chat Room
        deleteTestChatRooms(testCreatedChatRoomData.id)
        // Perform Delete Test User
        deleteTestUsers(testCreatedUserData.userid)
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
        val testCreatedUserData = usersApiService.createUpdateUser(request = testCreateUserInputRequest).get().data!!

        val testChatRoomData = TestData.chatRooms(appId).first()
                // Moderation MUST BE SET to "pre"
                .copy(moderation = "pre")
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
        chatApiService.joinRoom(request = testJoinRoomInputRequest).get()

        val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                command = "Yow Jessy, how are you doin'?",
                userid = testCreatedUserData.userid!!
        )
        // Test Created User Should send a message to the created chat room
        val testSendMessageData = chatApiService.executeChatCommand(
                chatRoomId = testCreatedChatRoomData.id!!,
                request = testInitialSendMessageInputRequest
        ).get().data?.speech!!

        val testInputRequest = ApproveMessageRequest(
                approve = true
        )
        val testExpectedResult = ApiResponse<ListMessagesNeedingModerationResponse>(
                kind = "api.result",
                message = "",
                code = 200,
                data = ListMessagesNeedingModerationResponse(
                        kind = "list.events",
                        events = listOf(testSendMessageData)
                )
        )

        // WHEN
        val testActualResult = chatModerationApiService.listMessagesNeedingModeration().get()

        // THEN
        println(
                "`List Messages Needing Moderation`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(ListMessagesNeedingModerationResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.events!!.contains(testSendMessageData) }

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