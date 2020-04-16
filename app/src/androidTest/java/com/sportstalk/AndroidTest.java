package com.sportstalk;

import android.content.Context;

import com.sportstalk.api.RoomUserResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AndroidTest {

    Context context;
    SportsTalkConfig sportsTalkConfig;
    User user;
    ChatClient chatClient;

    RoomResult roomResult;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();

        sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("QZF6YKDKSUCeL03tdA2l2gx4ckSvC7LkGsgmix-pBZLA");
        sportsTalkConfig.setContext(context);
        sportsTalkConfig.setAppId("5e92a5ce38a28d0b6453687a");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");

        user = new User();
        user.setUserId("sarah");
        user.setDisplayName("sarah");
        user.setHandle("sarah");
        user.setHandleLowerCase("sarah");
        sportsTalkConfig.setUser(user);
        chatClient = ChatClient.create(sportsTalkConfig);
    }

    @Test
    public void createRoomTest() {

        Room room = new Room();
        room.setName("Test Room");
        room.setSlug("chat-test-room");

        roomResult = chatClient.createRoom(room);
        Assert.assertNotNull(roomResult.getId());
    }

    @Test
    public void joinRoomTest() {
        System.out.println(" room id " + roomResult.getId());
        RoomUserResult roomUserResult = chatClient.joinRoom(roomResult);
        Assert.assertNotNull(roomUserResult);
    }

}
