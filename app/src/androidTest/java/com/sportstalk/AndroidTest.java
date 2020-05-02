package com.sportstalk;

import org.junit.FixMethodOrder;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AndroidTest {
//
//    Context context;
//    SportsTalkConfig sportsTalkConfig;
//    User user;
//    ChatClient chatClient;
//
//    RoomResult roomResult;
//
//    @Before
//    public void setup() {
//        context = InstrumentationRegistry.getInstrumentation().getContext();
//
//        sportsTalkConfig = new SportsTalkConfig();
//        sportsTalkConfig.setApiKey("ZortLH1JUkuEJQ5YgkZjHwx-AZFkPSJkSYnEO1NA6y7A");
//        sportsTalkConfig.setContext(context);
//        sportsTalkConfig.setAppId("5e9eff5338a28719345eb469");
//        sportsTalkConfig.setEndpoint("https://api.sportstalk247.com/api/v3");
//
//        user = new User();
//        user.setUserId("sarah");
//        user.setDisplayName("sarah");
//        user.setHandle("sarah");
//        user.setHandleLowerCase("sarah");
//        sportsTalkConfig.setUser(user);
//
//        final EventHandler eventHandler = new EventHandler() {
//            @Override
//            public void onEventStart(Event event) {
//
//            }
//
//            @Override
//            public void onReaction(Event event) {
//
//            }
//
//            @Override
//            public void onAdminCommand(Event event) {
//
//            }
//
//            @Override
//            public void onReply(Event event) {
//
//            }
//
//            @Override
//            public void onPurge(Event event) {
//
//            }
//
//            @Override
//            public void onSpeech(Event event) {
//                    System.out.println(" event ");
//            }
//
//            @Override
//            public void onChat(Event event) {
//
//            }
//
//            @Override
//            public void onNetworkResponse(List<EventResult> list) {
//
//            }
//
//            @Override
//            public void onHelp(ApiResult apiResult) {
//
//            }
//
//            @Override
//            public void onGoalCommand(EventResult eventResult) {
//
//            }
//        };
//        sportsTalkConfig.setEventHandler(eventHandler);
//        chatClient = ChatClient.create(sportsTalkConfig);
//    }
//
//    @Test
//    public void createRoomTest() {
//
//        Room room = new Room();
//        room.setName("Test Room2");
//        room.setSlug("chat-test-room " + System.currentTimeMillis() );
//        //roomResult = chatClient.createRoom(room);
//        //Assert.assertNotNull(roomResult.getId());
//        RoomResult r = new RoomResult();
//        r.setId("5eab249f38a29418485d0c96");
//
//        RoomUserResult roomUserResult = chatClient.joinRoom(r);
//        chatClient.setRoom(roomUserResult.getRoomResult());
//        chatClient.startChat();
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testSendCommand() {
//        RoomResult r = new RoomResult();
//        r.setId("5eab249f38a29418485d0c96");
//        r.setOwnerId("sarah");
//        RoomUserResult roomUserResult = chatClient.joinRoom(r);
//        Assert.assertNotNull(roomUserResult);
//        final CommandOptions commandOptions = new CommandOptions();
//        ApiResult result = chatClient.sendCommand("hello h a r u", commandOptions);
//        Assert.assertNotNull(result.getErrors());
//    }

}
