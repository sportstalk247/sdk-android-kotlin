package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.UserService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.ListUsersResponse
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
class UserServiceTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var userService: UserService
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
    fun `1) Create or Update User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        val testExpectedResult = ApiResponse<User>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = User(
                        kind = "app.user",
                        userid = testInputRequest.userid,
                        handle = testInputRequest.handle,
                        handlelowercase = testInputRequest.handle!!.toLowerCase(),
                        displayname = testInputRequest.displayname
                )
        )

        // WHEN
        val testActualResult = userService.createOrUpdateUser(request = testInputRequest).get()

        // THEN
        println(
                "`Create or Update User`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(User.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.userid == testExpectedResult.data?.userid }
        assertTrue { testActualResult.data?.handle == testExpectedResult.data?.handle }
        assertTrue { testActualResult.data?.handlelowercase == testExpectedResult.data?.handlelowercase }
        assertTrue { testActualResult.data?.displayname == testExpectedResult.data?.displayname }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.data?.userid)
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

        val testExpectedResult = ApiResponse<DeleteUserResponse>(
                kind = "api.result",
                message = "User deleted successfully.",
                code = 200,
                data = DeleteUserResponse(
                        kind = "deleted.appuser",
                        user = testCreatedUser.data
                )
        )

        // WHEN
        val testActualResult = userService.deleteUser(
                userId = testCreatedUser.data?.userid ?: testInputRequest.userid
        ).get()

        // THEN
        println(
                "`Delete User`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(DeleteUserResponse.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.user?.userid == testExpectedResult.data?.user?.userid }
        assertTrue { testActualResult.data?.user?.handle == testExpectedResult.data?.user?.handle!! }
        assertTrue { testActualResult.data?.user?.handlelowercase == testExpectedResult.data?.user?.handlelowercase!! }
        assertTrue { testActualResult.data?.user?.displayname == testExpectedResult.data?.user?.displayname }

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

        val testExpectedResult = ApiResponse<User>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = testCreatedUser.data
        )

        // WHEN
        val testActualResult = userService.getUserDetails(
                userId = testCreatedUser.data?.userid!!
        ).get()

        // THEN
        println(
                "`Get User Details`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(User.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.userid == testExpectedResult.data?.userid }
        assertTrue { testActualResult.data?.handle == testExpectedResult.data?.handle!! }
        assertTrue { testActualResult.data?.handlelowercase == testExpectedResult.data?.handlelowercase!! }
        assertTrue { testActualResult.data?.displayname == testExpectedResult.data?.displayname }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.data?.userid)
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
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get().data!!
        val testCreatedUser2 = userService.createOrUpdateUser(request = testInputRequest2).get().data!!

        val testExpectedResult = ApiResponse<ListUsersResponse>(
                kind = "api.result",
                message = "Success",
                code = 200,
                data = ListUsersResponse(
                        kind = "list.users",
                        users = listOf(testCreatedUser1, testCreatedUser2)
                )
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
                                ApiResponse.serializer(ListUsersResponse.serializer()),
                                testActualResult1
                        )
        )
        println(
                "`List Users`() -> testActualResult2 = " +
                        json.stringify(
                                ApiResponse.serializer(ListUsersResponse.serializer()),
                                testActualResult2
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.message == testExpectedResult.message }
        assertTrue { testActualResult1.code == testExpectedResult.code }
        assertTrue { testActualResult1.data != null }
        assertTrue { testActualResult1.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult1.data?.users?.isNotEmpty() == true }
        assertTrue { testActualResult1.data?.users?.any { it.userid == testCreatedUser1.userid } == true }

        assertTrue { testActualResult2.kind == testExpectedResult.kind }
        assertTrue { testActualResult2.message == testExpectedResult.message }
        assertTrue { testActualResult2.code == testExpectedResult.code }
        assertTrue { testActualResult2.data != null }
        assertTrue { testActualResult2.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult2.data?.users?.isNotEmpty() == true }
        assertTrue { testActualResult2.data?.users?.any { it.userid == testCreatedUser2.userid } == true }

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

        val testExpectedResult = ApiResponse<User>(
                kind = "api.result",
                /*message = "@handle_test1 was banned",*/
                code = 200,
                data = testCreatedUser.data
        )

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.data?.userid!!,
                banned = true
        ).get()

        // THEN
        println(
                "`Ban User`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(User.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        // "@handle_test1 was banned"
        assertTrue { testActualResult.message?.contains(testCreatedUser.data?.handle!!) == true }
        assertTrue { testActualResult.message?.contains("was banned") == true }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.userid == testExpectedResult.data?.userid }
        assertTrue { testActualResult.data?.handle == testExpectedResult.data?.handle!! }
        assertTrue { testActualResult.data?.handlelowercase == testExpectedResult.data?.handlelowercase!! }
        assertTrue { testActualResult.data?.displayname == testExpectedResult.data?.displayname }
        assertTrue { testActualResult.data?.banned == true }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.data?.userid)
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
                userId = testCreatedUser.data?.userid!!,
                banned = true
        ).get()

        val testExpectedResult = ApiResponse<User>(
                kind = "api.result",
                /*message = "@handle_test1 was banned",*/
                code = 200,
                data = testCreatedUser.data
        )

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.data?.userid!!,
                banned = false
        ).get()

        // THEN
        println(
                "`Restore User`() -> testActualResult = \n" +
                        json.stringify(
                                ApiResponse.serializer(User.serializer()),
                                testActualResult
                        )
        )

        assertTrue { testActualResult.kind == testExpectedResult.kind }
        // "@handle_test1 was restored"
        assertTrue { testActualResult.message?.contains(testCreatedUser.data?.handle!!) == true }
        assertTrue { testActualResult.message?.contains("was restored") == true }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.userid == testExpectedResult.data?.userid }
        assertTrue { testActualResult.data?.handle == testExpectedResult.data?.handle!! }
        assertTrue { testActualResult.data?.handlelowercase == testExpectedResult.data?.handlelowercase!! }
        assertTrue { testActualResult.data?.displayname == testExpectedResult.data?.displayname }
        assertTrue { testActualResult.data?.banned == false }

        // Perform Delete Test User
        deleteTestUsers(testActualResult.data?.userid)
    }

    @Test
    fun `7) Search Users - By Handle`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get().data!!

        val testExpectedResult = ApiResponse<ListUsersResponse>(
                kind = "api.result",
                code = 200,
                data = ListUsersResponse(
                        kind = "list.users",
                        users = listOf(testCreatedUser1)
                )
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
                                ApiResponse.serializer(ListUsersResponse.serializer()),
                                testActualResult1
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.code == testExpectedResult.code }
        assertTrue { testActualResult1.data != null }
        assertTrue { testActualResult1.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult1.data?.users?.isNotEmpty() == true }
        assertTrue { testActualResult1.data?.users?.any { it.handle == testCreatedUser1.handle } == true }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid)
    }

    @Test
    fun `8) Search Users - By Name`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get().data!!

        val testExpectedResult = ApiResponse<ListUsersResponse>(
                kind = "api.result",
                code = 200,
                data = ListUsersResponse(
                        kind = "list.users",
                        users = listOf(testCreatedUser1)
                )
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
                                ApiResponse.serializer(ListUsersResponse.serializer()),
                                testActualResult1
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.code == testExpectedResult.code }
        assertTrue { testActualResult1.data != null }
        assertTrue { testActualResult1.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult1.data?.users?.isNotEmpty() == true }
        assertTrue { testActualResult1.data?.users?.any { it.displayname == testCreatedUser1.displayname } == true }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid)
    }

    @Test
    fun `9) Search Users - By UserId`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService.createOrUpdateUser(request = testInputRequest1).get().data!!

        val testExpectedResult = ApiResponse<ListUsersResponse>(
                kind = "api.result",
                code = 200,
                data = ListUsersResponse(
                        kind = "list.users",
                        users = listOf(testCreatedUser1)
                )
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
                                ApiResponse.serializer(ListUsersResponse.serializer()),
                                testActualResult1
                        )
        )

        assertTrue { testActualResult1.kind == testExpectedResult.kind }
        assertTrue { testActualResult1.code == testExpectedResult.code }
        assertTrue { testActualResult1.data != null }
        assertTrue { testActualResult1.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult1.data?.users?.isNotEmpty() == true }
        assertTrue { testActualResult1.data?.users?.any { it.userid == testCreatedUser1.userid } == true }

        // Perform Delete Test User
        deleteTestUsers(testCreatedUser1.userid)
    }

}