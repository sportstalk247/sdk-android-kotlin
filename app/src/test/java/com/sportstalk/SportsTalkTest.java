package com.sportstalk;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class SportsTalkTest {


    EventHandler eventHandler;
    SportsTalkConfig sportsTalkConfig;
    MainActivity mainActivity;
    Event myEvent;

    @Before
    public void setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity.class).create().get();

        eventHandler = new EventHandler() {
            @Override
            public void onEventStart(Event event) {
                myEvent = event;
            }

            @Override
            public void onReaction(Event event) {

            }

            @Override
            public void onAdminCommand(Event event) {

            }

            @Override
            public void onPurge(Event event) {

            }

            @Override
            public void onSpeech(Event event) {

            }

            @Override
            public void onChat(Event event) {
                myEvent = event;
            }

            @Override
            public void onNetworkResponse(List<EventResult> list) {
                myEvent = list.get(0);
            }

            @Override
            public void onHelp(ApiResult apiResult) {
                System.out.println("chat event....");
            }

            @Override
            public void onGoalCommand(EventResult eventResult) {
                System.out.println("6. chat event....");
            }
        };

        sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setEventHandler(eventHandler);
        //sportsTalkConfig.setContext(mainActivity.getApplicationContext());
    }

    @Test
    public void testForJoinRoom(){
        SportsTalkClient sportsTalkClient = SportsTalkClient.create(sportsTalkConfig);
        RoomResult result = new RoomResult();
        result.setId("5dd9d5a038a28326ccfe5743");

        User user = new User();
        user.setUserId("001864a867604101b29672e904da688a");
        user.setDisplayName("Aldo");

        Map<String, String> data = new HashMap<>();
        data.put("userId",      user.getUserId());
        data.put("handle",      user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("puctureurl",  user.getPictureUrl());
        data.put("profileurl",  user.getProfileUrl());
        sportsTalkClient.joinRoom(result, "5dd9d5a038a28326ccfe5743", data);
        Assert.assertNotNull(sportsTalkClient.getCurrentRoom());
    }

    @Test
    public void testWhenRoomIdIsNullThenMyEventIsNull() {
        sportsTalkConfig = new SportsTalkConfig();
        sportsTalkConfig.setEventHandler(eventHandler);
        SportsTalkClient sportsTalkClient = SportsTalkClient.create(sportsTalkConfig);
        RoomResult result = new RoomResult();
        //result.setId("5dd9d5a038a28326ccfe5743");

        User user = new User();
        user.setUserId("001864a867604101b29672e904da688a");
        user.setDisplayName("Aldo");

        Map<String, String> data = new HashMap<>();
        data.put("userId",      user.getUserId());
        data.put("handle",      user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("puctureurl",  user.getPictureUrl());
        data.put("profileurl",  user.getProfileUrl());
        sportsTalkClient.joinRoom(result, null, data);
        Assert.assertNull(myEvent);

    }

    @Test
    public void testWhenCreateRoom() {

        SportsTalkClient sportsTalkClient = SportsTalkClient.create(sportsTalkConfig);
        sportsTalkClient.createRoom();
        Assert.assertNotEquals("5dd9d5a038a28326ccfe5743" ,sportsTalkClient.getCurrentRoom());
   }

}
