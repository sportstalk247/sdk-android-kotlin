package com.sportstalk.coroutine.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.coroutine.ServiceFactory
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.DateUtils
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
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
import kotlin.test.fail

@Suppress("MainFunctionReturnUnit")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.KITKAT])
class UserServiceTest {

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
            userService.deleteUser(userId = id)
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
    fun `A-ERROR-403) Request is not authorized with a token`() = runBlocking {
        val userCaseUserService = ServiceFactory.User.get(
                config.copy(
                        apiToken = "not-a-valid-auth-api-token"
                )
        )

        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}-1234",
                displayname = "Test 1"
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userCaseUserService.createOrUpdateUser(request = testInputRequest)
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-403 - Request is not authorized with a token`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
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
    fun `A) Create or Update User`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        val testExpectedResult = User(
                kind = Kind.USER,
                userid = testInputRequest.userid,
                handle = testInputRequest.handle,
                handlelowercase = testInputRequest.handle!!.toLowerCase(),
                displayname = testInputRequest.displayname
        )

        // WHEN
        val testActualResult = userService.createOrUpdateUser(request = testInputRequest)

        // THEN
        println(
                "`Create or Update User`() -> testActualResult = \n" +
                        json.stringify/*encodeToString*/(
                                User.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.handle == testExpectedResult.handle }
        assertTrue { testActualResult.handlelowercase == testExpectedResult.handlelowercase }
        assertTrue { testActualResult.displayname == testExpectedResult.displayname }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.userid)
    }

    @Test
    fun `A-ERROR-405) Create or Update User`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}-1234",
                displayname = "Test 1"
        )

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userService.createOrUpdateUser(request = testInputRequest)
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-405 - Create or Update User`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The handle requested (${testInputRequest.handle!!}) contains characters that are not allowed.  Use only [abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_]" }
            assertTrue { err.code == 405 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `B) Delete User`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)

        val testExpectedResult = DeleteUserResponse(
                kind = Kind.DELETED_USER,
                user = testCreatedUser
        )

        // WHEN
        val testActualResult = userService.deleteUser(
                userId = testCreatedUser.userid!!
        )

        // THEN
        println(
                "`Delete User`() -> testActualResult = \n" +
                        json.stringify/*encodeToString*/(
                                DeleteUserResponse.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.user?.userid == testExpectedResult.user?.userid }
        assertTrue { testActualResult.user?.handle == testExpectedResult.user?.handle!! }
        assertTrue { testActualResult.user?.handlelowercase == testExpectedResult.user?.handlelowercase!! }
        assertTrue { testActualResult.user?.displayname == testExpectedResult.user?.displayname }

    }

    @Test
    fun `B-ERROR-404) Delete User`() = runBlocking {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userService.deleteUser(
                        userId = testInputUserId
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Delete User`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specifed user $testInputUserId does not exist." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `C) Get User Details`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.getUserDetails(
                userId = testCreatedUser.userid!!
        )

        // THEN
        println(
                "`Get User Details`() -> testActualResult = \n" +
                        json.stringify/*encodeToString*/(
                                User.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.handle == testExpectedResult.handle!! }
        assertTrue { testActualResult.handlelowercase == testExpectedResult.handlelowercase!! }
        assertTrue { testActualResult.displayname == testExpectedResult.displayname }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.userid)
    }

    @Test
    fun `C-ERROR-404) Get User Details`() = runBlocking {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userService.getUserDetails(
                        userId = testInputUserId
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Get User Details`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified UserID was not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `D) List Users`() = runBlocking {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        val testInputRequest2 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_second",
                displayname = "Test List Users 2"
        )
        // Should create a test user first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1)
        val testCreatedUser2 = userService.createOrUpdateUser(request = testInputRequest2)

        val testExpectedResult = ListUsersResponse(
                kind = Kind.USER_LIST,
                users = listOf(testCreatedUser1, testCreatedUser2)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.listUsers(
                limit = testInputLimit,
                cursor = testCreatedUser1.userid
        )
        val testActualResult2 = userService.listUsers(
                limit = testInputLimit,
                cursor = testCreatedUser2.userid
        )

        // THEN
        println(
                "`List Users`() -> testActualResult1 = " +
                        json.stringify/*encodeToString*/(
                                ListUsersResponse.serializer(),
                                testActualResult1
                        )
        )
        println(
                "`List Users`() -> testActualResult2 = " +
                        json.stringify/*encodeToString*/(
                                ListUsersResponse.serializer(),
                                testActualResult2
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.users.isNotEmpty() }
        assertTrue { testActualResult1.users.any { it.userid == testCreatedUser1.userid } }

        assertTrue { testActualResult2.kind == testExpectedResult.kind }
        assertTrue { testActualResult2.users.isNotEmpty() }
        assertTrue { testActualResult2.users.any { it.userid == testCreatedUser2.userid } }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid, testCreatedUser2.userid)
    }

    @Test
    fun `E) Ban User`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = true
        )

        // THEN
        println(
                "`Ban User`() -> testActualResult = \n" +
                        json.stringify/*encodeToString*/(
                                User.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        // "@handle_test1 was banned"
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.handle == testExpectedResult.handle!! }
        assertTrue { testActualResult.handlelowercase == testExpectedResult.handlelowercase!! }
        assertTrue { testActualResult.displayname == testExpectedResult.displayname }
        assertTrue { testActualResult.banned == true }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.userid)
    }

    @Test
    fun `E-ERROR-404) Ban User`() = runBlocking {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userService.setBanStatus(
                        userId = testInputUserId,
                        banned = true
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Ban User`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified user is not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `F) Restore User`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)

        // The test user should be BANNED first
        userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = true
        )

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = false
        )

        // THEN
        println(
                "`Restore User`() -> testActualResult = \n" +
                        json.stringify/*encodeToString*/(
                                User.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        // "@handle_test1 was restored"
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.handle == testExpectedResult.handle!! }
        assertTrue { testActualResult.handlelowercase == testExpectedResult.handlelowercase!! }
        assertTrue { testActualResult.displayname == testExpectedResult.displayname }
        assertTrue { testActualResult.banned == false }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.userid)
    }

    @Test
    fun `F-ERROR-404) Restore User`() = runBlocking {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userService.setBanStatus(
                        userId = testInputUserId,
                        banned = false
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-404 - Restore User`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified user is not found." }
            assertTrue { err.code == 404 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `F1) Search Users - By Handle`() = runBlocking {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1)

        val testExpectedResult = ListUsersResponse(
                kind = Kind.USER_LIST,
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                handle = testCreatedUser1.handle!!,
                limit = testInputLimit
        )

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.stringify/*encodeToString*/(
                                ListUsersResponse.serializer(),
                                testActualResult1
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.users.isNotEmpty() }
        assertTrue { testActualResult1.users.any { it.handle == testCreatedUser1.handle } }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid)
    }

    @Test
    fun `F2) Search Users - By Name`() = runBlocking {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1)

        val testExpectedResult = ListUsersResponse(
                kind = Kind.USER_LIST,
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                name = testCreatedUser1.displayname,
                limit = testInputLimit
        )

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.stringify/*encodeToString*/(
                                ListUsersResponse.serializer(),
                                testActualResult1
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.users.isNotEmpty() }
        assertTrue { testActualResult1.users.any { it.displayname == testCreatedUser1.displayname } }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid)
    }

    @Test
    fun `F3) Search Users - By UserId`() = runBlocking {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1)

        val testExpectedResult = ListUsersResponse(
                kind = Kind.USER_LIST,
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                userid = testCreatedUser1.userid!!,
                limit = testInputLimit
        )

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.stringify/*encodeToString*/(
                                ListUsersResponse.serializer(),
                                testActualResult1
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.users.isNotEmpty() }
        assertTrue { testActualResult1.users.any { it.userid == testCreatedUser1.userid } }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid)
    }

    @Test
    fun `F-ERROR-400) Search Users`() = runBlocking {
        // GIVEN

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            withContext(Dispatchers.IO) {
                userService.searchUsers(
                        // No search criteria provided
                        limit = 100
                )
            }
        } catch (err: SportsTalkException) {
            println(
                    "`ERROR-400 - Search Users`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "Search requires either a userid, handle or name parameter." }
            assertTrue { err.code == 400 }

            throw err
        }

        return@runBlocking
    }

    @Test
    fun `G) Set Shadow Ban Status`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)

        val testExpectedResult = testCreatedUser.copy(
                shadowbanned = true
        )

        // WHEN
        val testActualResult = userService.setShadowBanStatus(
                userId = testCreatedUser.userid!!,
                shadowban = true
        )

        // THEN
        println(
                "`Set Shadow Ban Status`() -> testActualResult = \n" +
                        json.stringify/*encodeToString*/(
                                User.serializer(),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        // "@handle_test1 was banned"
        assertTrue { testActualResult.userid == testExpectedResult.userid }
        assertTrue { testActualResult.handle == testExpectedResult.handle!! }
        assertTrue { testActualResult.handlelowercase == testExpectedResult.handlelowercase!! }
        assertTrue { testActualResult.displayname == testExpectedResult.displayname }
        assertTrue { testActualResult.shadowbanned == true }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.userid)
    }

    @Test
    fun `H) Globally Purge User Content`() = runBlocking {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)

        // WHEN
        try {
            val testActualResult = userService.globallyPurgeUserContent(
                    userId = testInputRequest.userid,
                    banned = true
            )

            // THEN
            println(
                    "`Globally Purge User Content`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    GloballyPurgeUserContentResponse.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { true }
        } catch (err: SportsTalkException) {
            err.printStackTrace()
            fail(err.message)
        } finally {
            // Perform Delete Test User
            deleteTestUsers(testCreatedUser.userid)
        }
    }


    @Test
    fun `I) Report User`() = runBlocking {
        // GIVEN
        val testInputCreateRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputCreateRequest)

        val testInputRequest = ReportUserRequest(
                userid = testCreatedUser.userid!!,
                reporttype = ReportType.ABUSE
        )
        val testExpectedResult = testCreatedUser.copy(
                reports = listOf(
                        UserReport(
                                userid = testInputRequest.userid,
                                reason = testInputRequest.reporttype
                        )
                )
        )

        try {
            // WHEN
            val testActualResult = userService.reportUser(
                    userId = testInputRequest.userid!!,
                    reporttype = testInputRequest.reporttype!!
            )

            // THEN
            println(
                    "`Report User`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    User.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.kind == testExpectedResult.kind }
            assertTrue { testActualResult.userid == testExpectedResult.userid }
            assertTrue { testActualResult.handle == testExpectedResult.handle!! }
            assertTrue { testActualResult.handlelowercase == testExpectedResult.handlelowercase!! }
            assertTrue { testActualResult.displayname == testExpectedResult.displayname }
            // A User Report will be added
            assertTrue {
                testActualResult.reports.find { _rprt ->
                    _rprt.userid == testInputRequest.userid
                            && _rprt.reason == testInputRequest.reporttype
                } != null
            }
        } catch (err: SportsTalkException) {
            fail(err.message)
        } finally {
            // Perform Delete Test User
            deleteTestUsers(testCreatedUser.userid)
        }
    }

    @Test
    fun `J-1) List User Notifications - Chat Reply`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
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
            testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

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
            testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

            val testInputJoinChatRoomId = testCreatedChatRoomData?.id!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                    userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should join test created chat room
            chatService.joinRoom(
                    chatRoomId = testInputJoinChatRoomId,
                    request = testJoinRoomInputRequest
            )

            val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                    command = "Yow Jessy, how are you doin'?",
                    userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should send an initial message to the created chat room
            val testInitialSendMessage = chatService.executeChatCommand(
                    chatRoomId = testCreatedChatRoomData?.id!!,
                    request = testInitialSendMessageInputRequest
            ).speech!!

            val testInputChatReplyThreadedRequest = SendThreadedReplyRequest(
                    body = "This is Jessy, replying to your greetings yow!!!",
                    userid = testCreatedUserData?.userid!!
            )

            // Perform Chat Reply - Threaded
            val testChatReplyThreaded = chatService.sendThreadedReply(
                    chatRoomId = testCreatedChatRoomData?.id!!,
                    replyTo = testInitialSendMessage.id!!,
                    request = testInputChatReplyThreadedRequest
            )

            // WHEN
            val testActualResult = userService.listUserNotifications(
                    userId = testInputChatReplyThreadedRequest.userid,
                    limit = 10,
                    filterNotificationTypes = listOf(UserNotification.Type.CHAT_REPLY),
                    cursor = null,
                    includeread = false,
                    filterChatRoomId = testCreatedChatRoomData.id
            )

            // THEN
            println(
                    "`List User Notifications - Chat Reply`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    ListUserNotificationsResponse.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.kind == Kind.LIST_USER_NOTIFICATIONS }
            assertTrue {
                testActualResult.notifications.any { notif ->
                    notif.userid == testCreatedUserData?.userid
                            && notif.chatroomid == testCreatedChatRoomData?.id
                            && notif.chateventid == testChatReplyThreaded.id
                            && notif.notificationtype == UserNotification.Type.CHAT_REPLY
                }
            }

        } catch(err: SportsTalkException) {
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
    fun `J-2) List User Notifications - Chat Quote`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
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
            testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

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
            testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

            val testInputJoinChatRoomId = testCreatedChatRoomData?.id!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                    userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should join test created chat room
            chatService.joinRoom(
                    chatRoomId = testInputJoinChatRoomId,
                    request = testJoinRoomInputRequest
            )

            val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                    command = "Yow Jessy, how are you doin'?",
                    userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should send an initial message to the created chat room
            val testInitialSendMessage = chatService.executeChatCommand(
                    chatRoomId = testCreatedChatRoomData?.id!!,
                    request = testInitialSendMessageInputRequest
            ).speech!!

            val testInputChatQuoteRequest = SendQuotedReplyRequest(
                    body = "This is Jessy, quote to your greetings yow!!!",
                    userid = testCreatedUserData?.userid!!
            )

            // Perform Chat Reply - Quote
            val testChatReplyQuote = chatService.sendQuotedReply(
                    chatRoomId = testCreatedChatRoomData?.id!!,
                    replyTo = testInitialSendMessage.id!!,
                    request = testInputChatQuoteRequest
            )

            // WHEN
            val testActualResult = userService.listUserNotifications(
                    userId = testInputChatQuoteRequest.userid,
                    limit = 10,
                    filterNotificationTypes = listOf(UserNotification.Type.CHAT_QUOTE),
                    cursor = null,
                    includeread = false,
                    filterChatRoomId = testCreatedChatRoomData.id
            )

            // THEN
            println(
                    "`List User Notifications - Chat Quote`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    ListUserNotificationsResponse.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.kind == Kind.LIST_USER_NOTIFICATIONS }
            assertTrue {
                testActualResult.notifications.any { notif ->
                    notif.userid == testCreatedUserData?.userid
                            && notif.chatroomid == testCreatedChatRoomData?.id
                            && notif.chateventid == testChatReplyQuote.id
                            && notif.notificationtype == UserNotification.Type.CHAT_QUOTE
                }
            }

        } catch(err: SportsTalkException) {
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
    fun `K) Set User Notification As Read`() = runBlocking {
        // GIVEN
        val testUserData = TestData.users.first()
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
            testCreatedUserData = userService.createOrUpdateUser(request = testCreateUserInputRequest)

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
            testCreatedChatRoomData = chatService.createRoom(testCreateChatRoomInputRequest)

            val testInputJoinChatRoomId = testCreatedChatRoomData?.id!!
            val testJoinRoomInputRequest = JoinChatRoomRequest(
                    userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should join test created chat room
            chatService.joinRoom(
                    chatRoomId = testInputJoinChatRoomId,
                    request = testJoinRoomInputRequest
            )

            val testInitialSendMessageInputRequest = ExecuteChatCommandRequest(
                    command = "Yow Jessy, how are you doin'?",
                    userid = testCreatedUserData?.userid!!
            )
            // Test Created User Should send an initial message to the created chat room
            val testInitialSendMessage = chatService.executeChatCommand(
                    chatRoomId = testCreatedChatRoomData?.id!!,
                    request = testInitialSendMessageInputRequest
            ).speech!!

            val testInputChatReplyThreadedRequest = SendThreadedReplyRequest(
                    body = "This is Jessy, replying to your greetings yow!!!",
                    userid = testCreatedUserData?.userid!!
            )

            // Perform Chat Reply - Threaded
            val testChatReplyThreaded = chatService.sendThreadedReply(
                    chatRoomId = testCreatedChatRoomData?.id!!,
                    replyTo = testInitialSendMessage.id!!,
                    request = testInputChatReplyThreadedRequest
            )

            val listUserNotifications = userService.listUserNotifications(
                    userId = testInputChatReplyThreadedRequest.userid,
                    filterNotificationTypes = listOf(UserNotification.Type.CHAT_REPLY),
                    limit = 10,
                    includeread = false
            )

            val testUserNotification = listUserNotifications.notifications.firstOrNull() ?: fail()

            // WHEN
            val testActualResult = userService.setUserNotificationAsRead(
                    userId = testUserNotification.userid!!,
                    notificationId = testUserNotification.id!!,
                    read = true
            )

            // THEN
            println(
                    "`Set User Notification As Read`() -> testActualResult = \n" +
                            json.stringify/*encodeToString*/(
                                    UserNotification.serializer(),
                                    testActualResult
                            )
            )

            assertTrue { testActualResult.kind == Kind.NOTIFICATION }
            assertTrue { testActualResult.id == testUserNotification.id }
            assertTrue { testActualResult.userid == testUserNotification.userid }
            assertTrue { testActualResult.isread == true }

        } catch(err: SportsTalkException) {
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