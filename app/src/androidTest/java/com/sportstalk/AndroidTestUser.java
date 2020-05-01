package com.sportstalk;

import android.content.Context;

import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
public class AndroidTestUser {

    Context context;
    SportsTalkConfig sportsTalkConfig;
    User user;
    ChatClient chatClient;
    CommentingClient conversationClient;


    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("ZortLH1JUkuEJQ5YgkZjHwx-AZFkPSJkSYnEO1NA6y7A");
        sportsTalkConfig.setContext(context);
        sportsTalkConfig.setAppId("5e9eff5338a28719345eb469");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");
        chatClient = ChatClient.create(sportsTalkConfig);
        conversationClient = CommentingClient.create(sportsTalkConfig, null, null, null);
    }

    @Test
    public void test() {
        user = new User();
        user.setUserId("sarah");
        user.setDisplayName("sarah");
        user.setHandle("sarah");
        user.setHandleLowerCase("sarah");
        UserResult userResult = chatClient.createOrUpdateUser(user, true);
        Assert.assertNotNull(userResult);
    }

    @Test
    public void createSecondUserAndCreatedUserIsNotNull() {
        user = new User();
        user.setUserId("sarah2");
        user.setDisplayName("sarah2");
        user.setHandle("sarah2");
        user.setHandleLowerCase("sarah2");
        UserResult userResult = chatClient.createOrUpdateUser(user, true);
        Assert.assertNotNull(userResult);
    }

}
