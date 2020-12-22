package com.sportstalk.reactive.service

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ReportType
import com.sportstalk.datamodels.users.*
import com.sportstalk.reactive.ServiceFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
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

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.KITKAT])
class UserServiceTest {

    private lateinit var context: Context
    private lateinit var config: ClientConfig
    private lateinit var userService: UserService
    private lateinit var json: Json

    private val rxDisposeBag = CompositeDisposable()

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
            userService.deleteUser(userId = id).blockingGet()
        }
    }

    @Test
    fun `A-ERROR-403) Request is not authorized with a token`() {
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

        val createOrUpdateUser = TestObserver<User>()
        userCaseUserService.createOrUpdateUser(request = testInputRequest)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(createOrUpdateUser)

        createOrUpdateUser
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        return@assertError false
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
    fun `A) Create or Update User`() {
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
        val testActualResult = userService
                .createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        // THEN
        println(
                "`Create or Update User`() -> testActualResult = \n" +
                        json.encodeToString(
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
    fun `A-ERROR-400) Create or Update User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}-1234",
                displayname = "Test 1"
        )

        val createOrUpdateUser = TestObserver<User>()
        userService.createOrUpdateUser(request = testInputRequest)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(createOrUpdateUser)

        createOrUpdateUser
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        return@assertError false
                    }

                    println(
                            "`ERROR-400 - Create or Update User`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The handle requested (\"${testInputRequest.handle!!}\") contains characters that are not allowed.  Use only [abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_]"
                            && err.code == 400
                }
    }

    @Test
    fun `B) Delete User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService.createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        val testExpectedResult = DeleteUserResponse(
                kind = "deleted.appuser",
                user = testCreatedUser
        )

        // WHEN
        val testActualResult = userService.deleteUser(
                userId = testCreatedUser.userid!!
        )
                .blockingGet()

        // THEN
        println(
                "`Delete User`() -> testActualResult = \n" +
                        json.encodeToString(
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
    fun `B-ERROR-404) Delete User`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        val deleteUser = TestObserver<DeleteUserResponse>()

        // WHEN
        userService.deleteUser(userId = testInputUserId)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(deleteUser)

        // THEN
        deleteUser
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Delete User`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specifed user $testInputUserId does not exist."
                            && err.code == 404
                }
    }

    @Test
    fun `C) Get User Details`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService
                .createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.getUserDetails(
                userId = testCreatedUser.userid!!
        )
                .blockingGet()

        // THEN
        println(
                "`Get User Details`() -> testActualResult = \n" +
                        json.encodeToString(
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
    fun `C-ERROR-404) Get User Details`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        val getUserDetails = TestObserver<User>()

        // WHEN
        userService.getUserDetails(userId = testInputUserId)
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(getUserDetails)

        // THEN
        getUserDetails
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Get User Details`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified UserID was not found."
                            && err.code == 404
                }
    }

    @Test
    fun `D) List Users`() {
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
        val testCreatedUser1 = userService
                .createOrUpdateUser(request = testInputRequest1)
                .blockingGet()
        val testCreatedUser2 = userService
                .createOrUpdateUser(request = testInputRequest2)
                .blockingGet()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1, testCreatedUser2)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.listUsers(
                limit = testInputLimit,
                cursor = testCreatedUser1.userid
        )
                .blockingGet()
        val testActualResult2 = userService.listUsers(
                limit = testInputLimit,
                cursor = testCreatedUser2.userid
        )
                .blockingGet()

        // THEN
        println(
                "`List Users`() -> testActualResult1 = " +
                        json.encodeToString(
                                ListUsersResponse.serializer(),
                                testActualResult1
                        )
        )
        println(
                "`List Users`() -> testActualResult2 = " +
                        json.encodeToString(
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
    fun `E) Ban User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService
                .createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = true
        ).blockingGet()

        // THEN
        println(
                "`Ban User`() -> testActualResult = \n" +
                        json.encodeToString(
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
    fun `E-ERROR-404) Ban User`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        val setBanStatus = TestObserver<User>()

        // WHEN
        userService.setBanStatus(
                userId = testInputUserId,
                banned = true
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(setBanStatus)

        // THEN
        setBanStatus
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Ban User`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified user is not found."
                            && err.code == 404

                }
    }

    @Test
    fun `F) Restore User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService
                .createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        // The test user should be BANNED first
        userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = true
        ).blockingGet()

        val testExpectedResult = testCreatedUser.copy()

        // WHEN
        val testActualResult = userService.setBanStatus(
                userId = testCreatedUser.userid!!,
                banned = false
        ).blockingGet()

        // THEN
        println(
                "`Restore User`() -> testActualResult = \n" +
                        json.encodeToString(
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
    fun `F-ERROR-404) Restore User`() {
        // GIVEN
        val testInputUserId = "non-existing-ID-1234"

        val setBanStatus = TestObserver<User>()
        // WHEN
        userService.setBanStatus(
                userId = testInputUserId,
                banned = false
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(setBanStatus)

        // THEN
        setBanStatus
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-404 - Restore User`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "The specified user is not found."
                            && err.code == 404
                }
    }

    @Test
    fun `F1) Search Users - By Handle`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService
                .createOrUpdateUser(request = testInputRequest1)
                .blockingGet()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                handle = testCreatedUser1.handle!!,
                limit = testInputLimit
        ).blockingGet()

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.encodeToString(
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
    fun `F2) Search Users - By Name`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService
                .createOrUpdateUser(request = testInputRequest1)
                .blockingGet()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                name = testCreatedUser1.displayname,
                limit = testInputLimit
        ).blockingGet()

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.encodeToString(
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
    fun `F3) Search Users - By UserId`() {
        // GIVEN
        val testInputRequest1 = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "list_users_first",
                displayname = "Test List Users 1"
        )
        // Should create a test user(s) first
        val testCreatedUser1 = userService
                .createOrUpdateUser(request = testInputRequest1)
                .blockingGet()

        val testExpectedResult = ListUsersResponse(
                kind = "list.users",
                users = listOf(testCreatedUser1)
        )

        val testInputLimit = 10

        // WHEN
        val testActualResult1 = userService.searchUsers(
                userid = testCreatedUser1.userid!!,
                limit = testInputLimit
        ).blockingGet()

        // THEN
        println(
                "`Search Users`() -> testActualResult1 = " +
                        json.encodeToString(
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
    fun `F-ERROR-400) Search Users`() {
        // GIVEN
        val searchUsers = TestObserver<ListUsersResponse>()

        // WHEN
        userService.searchUsers(
                // No search criteria provided
                limit = 100
        )
                .doOnSubscribe { rxDisposeBag.add(it) }
                .subscribe(searchUsers)

        // THEN
        searchUsers
                .assertError {
                    val err = it as? SportsTalkException ?: run {
                        fail()
                    }

                    println(
                            "`ERROR-400 - Search Users`() -> testActualResult = \n" +
                                    json.encodeToString(
                                            SportsTalkException.serializer(),
                                            err
                                    )
                    )

                    return@assertError err.kind == Kind.API
                            && err.message == "Search requires either a userid, handle or name parameter."
                            && err.code == 400
                }
    }

    @Test
    fun `G) Shadow Ban User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService
                .createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        val testExpectedResult = testCreatedUser.copy(
                shadowbanned = true
        )

        // WHEN
        val testActualResult = userService.shadowBanUser(
                userId = testCreatedUser.userid!!,
                shadowban = true
        ).blockingGet()

        // THEN
        println(
                "`Shadow Ban User`() -> testActualResult = \n" +
                        json.encodeToString(
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
    fun `H) Global Purge User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService
                .createOrUpdateUser(request = testInputRequest)
                .blockingGet()

        // WHEN
        try {
            val testActualResult = userService.globalPurge(
                    userId = testInputRequest.userid,
                    banned = true
            ).blockingGet()

            // THEN
            println(
                    "`Global Purge User`() -> testActualResult = \n" +
                            json.encodeToString(
                                    GlobalPurgeResponse.serializer(),
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
    fun `I) Report User`() {
        // GIVEN
        val testInputCreateRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1_${Random.nextInt(100, 999)}",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = userService
                .createOrUpdateUser(request = testInputCreateRequest)
                .blockingGet()

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
            ).blockingGet()

            // THEN
            println(
                    "`Report User`() -> testActualResult = \n" +
                            json.encodeToString(
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

}