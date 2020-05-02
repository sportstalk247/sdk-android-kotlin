package com.sportstalk.api

import android.app.Activity
import android.content.Context
import com.sportstalk.Dependencies
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.ChatRoom
import com.sportstalk.models.chat.CreateRoomRequest
import com.sportstalk.models.users.User
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
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
        // TODO::
    }

    @Test
    fun `1) Create Room`() {
        // GIVEN
        val testExpectedData = TestData.chatRooms(appId).first()
        val testInputRequest = CreateRoomRequest(
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

        // Perform Delete Test User
        deleteTestChatRooms(testActualResult.data?.id)
    }

    @Test
    fun `2) Get Room Details`() {
        // GIVEN
        val testData = TestData.chatRooms(appId).first()
        val testInputRequest = CreateRoomRequest(
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

        // Perform Delete Test User
        deleteTestChatRooms(testActualResult.data?.id)
    }


    object TestData {
        val users = listOf(
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test1",
                        displayname = "Test 1"
                ),
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test2",
                        displayname = "Test 2"
                ),
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test3",
                        displayname = "Test 3"
                ),
                User(
                        kind = "app.user",
                        userid = RandomString.make(16),
                        handle = "handle_test3",
                        displayname = "Test 3"
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