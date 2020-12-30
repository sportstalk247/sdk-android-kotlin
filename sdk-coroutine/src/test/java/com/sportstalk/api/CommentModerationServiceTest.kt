package com.sportstalk.api

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.sportstalk.coroutine.ServiceFactory
import com.sportstalk.coroutine.service.CommentModerationService
import com.sportstalk.coroutine.service.CommentService
import com.sportstalk.coroutine.service.UserService
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class CommentModerationServiceTest {

    private lateinit var context: Context
    private lateinit var config: com.sportstalk.datamodels.ClientConfig
    private lateinit var userService: UserService
    private lateinit var commentService: CommentService
    private lateinit var commentModerationService: CommentModerationService
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

        config = com.sportstalk.datamodels.ClientConfig(
                appId = appInfo?.metaData?.getString("sportstalk.api.app_id")!!,
                apiToken = appInfo.metaData?.getString("sportstalk.api.auth_token")!!,
                endpoint = appInfo.metaData?.getString("sportstalk.api.url.endpoint")!!
        )
        json = ServiceFactory.RestApi.json
        userService = ServiceFactory.User.get(config)
    }

    @After
    fun cleanUp() {
    }

    // TODO::

}