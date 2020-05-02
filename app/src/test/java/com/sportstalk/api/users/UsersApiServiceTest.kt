package com.sportstalk.api.users

import android.app.Activity
import android.content.Context
import com.sportstalk.Dependencies
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.users.CreateUpdateUserRequest
import com.sportstalk.models.users.DeleteUserResponse
import com.sportstalk.models.users.User
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import net.bytebuddy.utility.RandomString
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import kotlin.test.assertTrue

@UnstableDefault
@ImplicitReflectionSerializer
@RunWith(RobolectricTestRunner::class)
class UsersApiServiceTest {

    private lateinit var context: Context
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var retrofit: Retrofit
    private lateinit var usersApiService: UsersApiService
    private lateinit var json: Json

    @Before
    fun setup() {
        context = Robolectric.buildActivity(Activity::class.java).get().applicationContext
        val apiUrlEndpoint = Dependencies.ApiEndpoint.getInstance(context)!!
        val authToken = Dependencies.AuthToken.getInstance(context)!!
        val appId = Dependencies.AppId.getInstance(context)!!
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
    }

    @After
    fun cleanUp() {
    }

    @Test
    fun `Create or Update User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1",
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
                        displayname = testInputRequest.displayname
                )
        )

        // WHEN
        val testActualResult = usersApiService.createUpdateUser(request = testInputRequest).get()
        println(
                "`Create or Update User`() -> testActualResult = " +
                        json.stringify(ApiResponse.serializer(User.serializer()), testActualResult)
        )

        // THEN
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.userid == testExpectedResult.data?.userid }
        assertTrue { testActualResult.data?.handle?.contains(testExpectedResult.data?.handle!!) == true }
        assertTrue { testActualResult.data?.handlelowercase?.contains(testExpectedResult.data?.handle!!.toLowerCase()) == true }
        assertTrue { testActualResult.data?.displayname == testExpectedResult.data?.displayname }
    }

    @Test
    fun `Delete User`() {
        // GIVEN
        val testInputRequest = CreateUpdateUserRequest(
                userid = RandomString.make(16),
                handle = "handle_test1",
                displayname = "Test 1"
        )
        // Should create a test user first
        val testCreatedUser = usersApiService.createUpdateUser(request = testInputRequest).get()

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
        val testActualResult = usersApiService.deleteUser(
                userId = testCreatedUser.data?.userid ?: testInputRequest.userid
        ).get()
        println(
                "`Delete User`() -> testActualResult = " +
                        json.stringify(ApiResponse.serializer(DeleteUserResponse.serializer()), testActualResult)
        )

        // THEN
        assertTrue { testActualResult.kind == testExpectedResult.kind }
        assertTrue { testActualResult.message == testExpectedResult.message }
        assertTrue { testActualResult.code == testExpectedResult.code }
        assertTrue { testActualResult.data != null }
        assertTrue { testActualResult.data?.kind == testExpectedResult.data?.kind }
        assertTrue { testActualResult.data?.user?.userid == testExpectedResult.data?.user?.userid }
        assertTrue { testActualResult.data?.user?.handle == testExpectedResult.data?.user?.handle }
        assertTrue { testActualResult.data?.user?.handle?.contains(testExpectedResult.data?.user?.handle!!) == true }
        assertTrue { testActualResult.data?.user?.handlelowercase?.contains(testExpectedResult.data?.user?.handle!!.toLowerCase()) == true }
        assertTrue { testActualResult.data?.user?.displayname == testExpectedResult.data?.user?.displayname }

    }

}