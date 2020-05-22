package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.UserService
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.Kind
import com.sportstalk.models.SportsTalkException
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.ListUsersResponse
import com.sportstalk.models.users.User
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
import java.util.concurrent.ExecutionException
import kotlin.random.Random
import kotlin.test.assertTrue


@UnstableDefault
@ImplicitReflectionSerializer
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class UserServiceTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var userService: UserService
    private lateinit var json: Json

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
            userService.deleteUser(userId = id).get()
        }
    }

    @Test
    fun `0-ERROR-403) Request is not authorized with a token`() {
        val userCaseUserService = ServiceFactory.RestApi.User.get(
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
            userCaseUserService.createOrUpdateUser(request = testInputRequest).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
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
    }

    @Test
    fun `1) Create or Update User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        val testExpectedResult = User(
                kind = "app.user",
                userid = testInputRequest.userid,
                handle = testInputRequest.handle,
                handlelowercase = testInputRequest.handle!!.toLowerCase(),
                displayname = testInputRequest.displayname
        )

        // WHEN
        val testActualResult = userService.createOrUpdateUser(request = testInputRequest).get()

        // THEN
        println(
                "`Create or Update User`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `1-ERROR-400) Create or Update User`() {
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
            userService.createOrUpdateUser(request = testInputRequest).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
            println(
                    "`ERROR-400 - Create or Update User`() -> testActualResult = \n" +
                            json.stringify(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The handle requested (\"${testInputRequest.handle!!}\") contains characters that are not allowed.  Use only [abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_]" }
            assertTrue { err.code == 400 }

            throw err
        }
    }

    @Test
    fun `2) Delete User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest).get()

        val testExpectedResult = DeleteUserResponse(
                kind = "deleted.appuser",
                user = testCreatedUser
        )

        // WHEN
        val testActualResult = userService.deleteUser(
                userId = testCreatedUser.userid!!
        ).get()

        // THEN
        println(
                "`Delete User`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `2-ERROR-404) Delete User`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            userService.deleteUser(
                    userId = testInputUserId
            ).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
            println(
                    "`ERROR-404 - Delete User`() -> testActualResult = \n" +
                            json.stringify(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specifed user $testInputUserId does not exist." }
            assertTrue { err.code == 404 }

            throw err
        }
    }

    @Test
    fun `3) Get User Details`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest).get()

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.getUserDetails(
                userId = testCreatedUser.userid!!
        ).get()

        // THEN
        println(
                "`Get User Details`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `3-ERROR-404) Get User Details`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            userService.getUserDetails(
                    userId = testInputUserId
            ).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
            println(
                    "`ERROR-404 - Get User Details`() -> testActualResult = \n" +
                            json.stringify(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified UserID was not found." }
            assertTrue { err.code == 404 }

            throw err
        }
    }

    @Test
    fun `4) List Users`() {
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
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get()
        val testCreatedUser2 = userService.createOrUpdateUser(request = testInputRequest2).get()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1, testCreatedUser2)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.listUsers(
                limit = testInputLimit,
                cursor = testCreatedUser1.userid
        ).get()
        val testActualResult2 = userService.listUsers(
                limit = testInputLimit,
                cursor = testCreatedUser2.userid
        ).get()

        // THEN
        println(
                "`List Users`() -> testActualResult1 = " +
                        json.stringify(
                                ListUsersResponse.serializer(),
                                testActualResult1
                        )
        )
        println(
                "`List Users`() -> testActualResult2 = " +
                        json.stringify(
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
    fun `5) Ban User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest).get()

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = true
        ).get()

        // THEN
        println(
                "`Ban User`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `5-ERROR-404) Ban User`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            userService.setBanStatus(
                    userId = testInputUserId,
                    banned = true
            ).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
            println(
                    "`ERROR-404 - Ban User`() -> testActualResult = \n" +
                            json.stringify(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified user is not found." }
            assertTrue { err.code == 404 }

            throw err
        }
    }

    @Test
    fun `6) Restore User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest).get()

        // The test user should be BANNED first
        userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = true
        ).get()

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = false
        ).get()

        // THEN
        println(
                "`Restore User`() -> testActualResult = \n" +
                        json.stringify(
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
    fun `6-ERROR-404) Restore User`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            userService.setBanStatus(
                    userId = testInputUserId,
                    banned = false
            ).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
            println(
                    "`ERROR-404 - Restore User`() -> testActualResult = \n" +
                            json.stringify(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "The specified user is not found." }
            assertTrue { err.code == 404 }

            throw err
        }
    }

    @Test
    fun `7A) Search Users - By Handle`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                handle = testCreatedUser1.handle!!,
                limit = testInputLimit
        ).get()

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.stringify(
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
    fun `7B) Search Users - By Name`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                name = testCreatedUser1.displayname,
                limit = testInputLimit
        ).get()

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.stringify(
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
    fun `7C) Search Users - By UserId`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                userid = testCreatedUser1.userid!!,
                limit = testInputLimit
        ).get()

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.stringify(
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
    fun `7-ERROR-400) Search Users`() {
        // GIVEN

        // EXPECT
        thrown.expect(SportsTalkException::class.java)

        // WHEN
        try {
            userService.searchUsers(
                    // No search criteria provided
                    limit = 100
            ).get()
        } catch (ex: ExecutionException) {
            val err =  ex.cause as SportsTalkException
            println(
                    "`ERROR-400 - Search Users`() -> testActualResult = \n" +
                            json.stringify(
                                    SportsTalkException.serializer(),
                                    err
                            )
            )
            assertTrue { err.kind == Kind.API }
            assertTrue { err.message == "Search requires either a userid, handle or name parameter." }
            assertTrue { err.code == 400 }

            throw err
        }
    }

}