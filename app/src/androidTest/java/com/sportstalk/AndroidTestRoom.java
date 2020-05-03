package com.sportstalk;

import android.content.Context;

import com.sportstalk.api.chat.EventHandler;
import com.sportstalk.error.SettingsException;
import com.sportstalk.error.SportsTalkException;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.Event;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
public class AndroidTestRoom {

    Context context;
    SportsTalkConfig sportsTalkConfig;
    User user;
    ChatClient chatClient;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getContext();

        sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("ZortLH1JUkuEJQ5YgkZjHwx-AZFkPSJkSYnEO1NA6y7A");
        sportsTalkConfig.setContext(context);
        sportsTalkConfig.setAppId("5e9eff5338a28719345eb469");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");

        user = new User();
        user.setUserId("sarah");
        user.setDisplayName("sarah");
        user.setHandle("sarah");
        user.setHandleLowerCase("sarah");
        sportsTalkConfig.setUser(user);

        final EventHandler eventHandler = new EventHandler() {
            @Override
            public void onEventStart(Event event) {
            }

            @Override
            public void onReaction(Event event) {
            }

            @Override
            public void onAdminCommand(Event event) {
            }

            @Override
            public void onReply(Event event) {
            }

            @Override
            public void onPurge(Event event) {
            }

            @Override
            public void onSpeech(Event event) {
                System.out.println(" event ");
            }

            @Override
            public void onChat(Event event) {
            }

            @Override
            public void onNetworkResponse(List<EventResult> list) {
            }

            @Override
            public void onHelp(ApiResult apiResult) {
            }

            @Override
            public void onGoalCommand(EventResult eventResult) {

            }
        };
        sportsTalkConfig.setEventHandler(eventHandler);
        chatClient = ChatClient.create(sportsTalkConfig);
    }

    @Test
    public void whenUserIsNullCreatedRoomShouldNotBeNull() {
        Room room = new Room();
        room.setName("Test Room22");
        room.setSlug("chat-test-room ");
        RoomResult roomResult = chatClient.createRoom(room);
        Assert.assertNotNull(roomResult.getId());
    }

    @Test
    public void whenARoomIsCreatedThenRoomsListShouldNotBeEmpty() {
        Room room = new Room();
        room.setName("Test Room32");
        room.setSlug("chat-test-room32");
        RoomResult roomResult = chatClient.createRoom(room);
        List<Room>list = chatClient.listRooms();
        Assert.assertTrue(list.size()>0);
    }

    @Test
    public void whenARoomIsCreatedThenRoomsListShouldContainThatRoomName() {
        Room room = new Room();
        room.setName("Test Room33");
        room.setSlug("chat-test-room33");
        RoomResult roomResult = chatClient.createRoom(room);
        List<Room>list = chatClient.listRooms();
        boolean status = false;
        for(Room r: list) {
            if("Test Room33".equals(r.getName())) {
                status = true;
                break;
            }
        }
        Assert.assertTrue(status);
    }

    @Test(expected = SportsTalkException.class)
    public void whenAppIdIsNullThenShouldThrowException() {
        sportsTalkConfig.setAppId(null);
        ChatClient chatClient1 = ChatClient.create(sportsTalkConfig);
        Room room = new Room();
        room.setName("Test Room23");
        room.setSlug("chat-test-room ");
        RoomResult roomResult = chatClient1.createRoom(room);
    }

    ///// join room scenario
    @Test(expected = Exception.class)
    public void joinRoomWithoutUserTestShouldFail() {
        Room firstRoom  = null;
        for(Room r : chatClient.listRooms()) {
                firstRoom = r;
                break;
        }
        RoomResult result = new RoomResult();
        result.setId(firstRoom.getId());
        result.setName(firstRoom.getName());
        result.setSlug(firstRoom.getSlug());

        sportsTalkConfig.setUser(null);
        ChatClient chatClient = ChatClient.create(sportsTalkConfig);
        RoomUserResult success = chatClient.joinRoom(result);
    }

    @Test(expected = SportsTalkException.class)
    public void whenUserIsNullSendCommandShouldFail() {
        Room firstRoom  = null;
        for(Room r : chatClient.listRooms()) {
            firstRoom = r;
            break;
        }
        RoomResult result = new RoomResult();
        result.setId(firstRoom.getId());
        result.setName(firstRoom.getName());
        result.setSlug(firstRoom.getSlug());

        sportsTalkConfig.setUser(null);
        ChatClient chatClient = ChatClient.create(sportsTalkConfig);
        RoomUserResult success = chatClient.joinRoom(result);
        ApiResult apiResult = chatClient.sendCommand("hello", null);
    }

    @Test
    public void whenAUserIsSetAndJoinedRoomCanSendCommand() {
        Room firstRoom  = null;

        User user = new User();
        user.setUserId("sarah");

        SportsTalkConfig sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setApiKey("ZortLH1JUkuEJQ5YgkZjHwx-AZFkPSJkSYnEO1NA6y7A");
        sportsTalkConfig.setContext(context);
        sportsTalkConfig.setAppId("5e9eff5338a28719345eb469");
        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");
        sportsTalkConfig.setUser(user);

        ChatClient cc = ChatClient.create(sportsTalkConfig);
        for(Room r : cc.listRooms()) {
            firstRoom = r;
            break;
        }
        RoomResult result = new RoomResult();
        result.setId(firstRoom.getId());
        result.setName(firstRoom.getName());
        result.setSlug(firstRoom.getSlug());
        RoomUserResult success = cc.joinRoom(result);
        ApiResult apiResult = cc.sendCommand("hello", null);
        Assert.assertEquals(0, apiResult.getCode());
    }


    @Test
    public void testExitRoom() {

        User user = new User();
        user.setUserId("sarah");
        user.setDisplayName("sarah");
        user.setHandle("sarah");

        Room room = null;
        for(Room r: chatClient.listRooms()) {
            room = r;
            break;
        }

        RoomResult result = new RoomResult();
        result.setId(room.getId());
        result.setName(room.getName());
        result.setSlug(room.getSlug());

        RoomUserResult roomUserResult = chatClient.joinRoom(result);
        chatClient.setRoom(roomUserResult.getRoomResult());
           try {
            RoomUserResult roomUserResult1 = chatClient.exitRoom();
            Assert.assertNotNull(roomUserResult1);
        } catch (SettingsException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testForUserMessages(){
        Room room = null;
        for(Room r: chatClient.listRooms()) {
            room = r;
            break;
        }

        RoomResult result = new RoomResult();
        result.setId(room.getId());
        result.setName(room.getName());
        result.setSlug(room.getSlug());

        RoomUserResult roomUserResult = chatClient.joinRoom(result);
        chatClient.setRoom(roomUserResult.getRoomResult());

        ApiResult apiResult = chatClient.sendCommand("hello", null);
        List<EventResult>list = chatClient.listUserMessages(user, result, "", 100);

        boolean find = false;
        for(EventResult eventResult : list) {
            if("hello".equals(eventResult.getBody())) {
                find = true;
                break;
            }
        }
        Assert.assertEquals(true, find);
    }

}
