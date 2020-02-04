package com.sportstalk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sportstalk.api.Room;
import com.sportstalk.rest.HttpClient;
import com.sportstalk.api.RestfulEventManager;
import com.sportstalk.api.RestfulRoomManager;
import com.sportstalk.api.RestfulUserManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SportsTalkClient {

    /** invalid polling frequency message **/
    private final String INVALID_POLLING_FREQUENCY = "Invalid poll _pollFrequency.  Must be between 250ms and 5000ms";
    /** error message in case not joined the room **/
    private final String NO_ROOM_SET = "No room set.  You must join a room before you can get updates!";
    /**
     * Android Log
     **/
    private final String TAG = SportsTalkClient.class.getName();
    /**
     * callback handler
     **/
    private APICallback apiCallback;
    /**
     * default endpoint
     **/
    private String endpoint = "http://api-origin.sportstalk247.com/api/v3";
    /**
     * api key
     **/
    private String apiKey;
    /**
     * application id
     **/
    private String appId;
    /**
     * default polling interval in milliseconds
     **/
    private long pollFrequency = 800;
    /**
     * user id
     **/
    private String userId;
    /**
     * room id
     **/
    private String roomIdentifier;
    /**
     * endpoint for room related activities
     **/
    private String roomAPI;
    /**
     * endpoint for command related activities
     **/
    private String commandAPI;
    /**
     * endpoint for update related activities
     **/
    private String updatesAPI;
    /**
     * application context
     **/
    private Context context;
    /**
     * user object
     **/
    private User user;
    /**
     * sports talk configuration file
     **/
    private SportsTalkConfig sportsTalkConfig;

    /**
     * call back used to fetch poll data
     **/
    private EventHandler eventHandler;

    private boolean isPushEnabled;

    private RestfulRoomManager roomManager;

    private RestfulEventManager eventManager;

    private static SportsTalkClient sportsTalkClient;

    private RestfulUserManager userManager;

    private Room currentRom;

    public SportsTalkClient(final String apiKey) {
        this.apiKey = apiKey;
    }

    private SportsTalkClient(final SportsTalkConfig sportsTalkConfig) {
        this.appId            = sportsTalkConfig.getAppId();
        this.apiKey           = sportsTalkConfig.getApiKey();
        this.userId           = sportsTalkConfig.getUserId();
        this.endpoint         = sportsTalkConfig.getEndpoint() == null ? this.endpoint : sportsTalkConfig.getEndpoint();
        this.context          = sportsTalkConfig.getContext();
        this.user             = sportsTalkConfig.getUser();
        this.eventHandler     = sportsTalkConfig.getEventHandler();
        this.isPushEnabled    = sportsTalkConfig.isPushEnabled();
        sportsTalkConfig.setEndpoint(this.endpoint);
        registerApiCallback();
        sportsTalkConfig.setApiCallback(apiCallback);
        setConfig(sportsTalkConfig);
    }

    private void registerApiCallback() {
        apiCallback = new APICallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void execute(ApiResult<JSONObject> apiResult, String action) {
                if("createRoom".equals(action)) {
                    currentRom = new Room();
                    try {
                        String id = apiResult.getData().getJSONObject("data").getString("id");
                        currentRom.setId(id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    java.util.List<EventResult>list = new ArrayList<>();
                    EventResult eventResult = new EventResult();
                    eventResult.setCustomPayload(currentRom);
                    list.add(eventResult);
                    sportsTalkConfig.getEventHandler().onNetworkResponse(list);
                }else if("joinRoom".equals(action)) {
                    roomAPI    = endpoint + "/room/" + roomIdentifier;
                    commandAPI = roomAPI + "/command";
                    updatesAPI = roomAPI + "/updates";
                    startTalk();
                }
             }

            @Override
            public void error(ApiResult<JSONObject> jsonObject, String action) {
                System.out.println("*** any error *** " + jsonObject.getData());
            }
        };
    }
    /**
     * sets end point
     **/
    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * a Factory  for creating SportsTalkClient
     * @param sportsTalkConfig
     */
    public static SportsTalkClient create(SportsTalkConfig sportsTalkConfig) {
        if(sportsTalkClient == null)
        sportsTalkClient = new SportsTalkClient(sportsTalkConfig);
        return sportsTalkClient;
    }

    public void setConfig(final SportsTalkConfig config) {
        sportsTalkConfig = config;
        if(eventManager == null) eventManager = new RestfulEventManager(config, config.getEventHandler());
        if(roomManager == null) roomManager = new RestfulRoomManager(config);
        if(userManager == null) userManager = new RestfulUserManager(config);

        registerApiCallback();
        sportsTalkConfig.setApiCallback(apiCallback);
    }

    /**
     * sets polling frequency
     * @param pollFrequency
     */
    public void setUpdateFrequency(long pollFrequency) {
        this.pollFrequency = pollFrequency;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listRooms(Map<String, String> data) {
        roomManager.listRooms();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUsers() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/user/?limit=100&cursor=", new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("listUsers");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startTalk() {
        eventManager.setCurrentRoom(currentRom);
        eventManager.startTalk();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startPollUpdate() {
        Map<String, String> data = new HashMap<>();
        APICallback apiCallback = new APICallback() {
            @Override
            public void execute(ApiResult<JSONObject> apiResult, String action) {
                handlePoll(apiResult.getData());
            }

            @Override
            public void error(ApiResult<JSONObject> apiResult, String action) {
            }
        };

        final HttpClient httpClient = new HttpClient(context, "GET", updatesAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("update");

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run () {
                //send volley request here
                if (updatesAPI != null) {
                    httpClient.execute();
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    /**
     * handle the data returned from the polling
     * @param data
     */
    private void handlePoll(JSONObject data) {
        try {
            JSONArray array = data.getJSONArray("data");
            int len = array == null ? 0 : array.length();
            for (int i = 0; i < len; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String eventType = jsonObject.getString("eventtype");
                String eventKind = jsonObject.getString("kind");
                Event event = new Event();
                event.setAdded(Integer.parseInt(jsonObject.getString("added")));
                event.setId(jsonObject.getString("id"));
                event.setRoomId(jsonObject.getString("roomId"));
                event.setBody(jsonObject.getString("body"));
                event.setUserId(jsonObject.getString("userid"));
                event.setEventType(EventType.Purge);
                event.setKind(Kind.chat);

                if("chat.event".equals(eventKind)) {
                    if (eventType.equals("Purge")) {
                        eventHandler.onPurge(event);
                    } else if (eventType.equals("Reaction")) {
                        eventHandler.onReaction(event);
                    } else if (eventType.equals("Reply")) {
                        eventHandler.onReaction(event);
                    } else if (eventType.equals("Speech")) {
                        eventHandler.onSpeech(event);
                    } else if (eventType.equals("api.result")) {
                        eventHandler.onEventStart(event);
                    }
                    else {
                        eventHandler.onChat(event);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listParticipants(int roomId, String cursor, int maxResults) {
        roomManager.listParticipants(currentRom, cursor, maxResults);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void joinRoom(RoomResult roomResult, String roomId, Map<String, String> data) {
        roomManager.joinRoom(user, roomResult);
        roomIdentifier = currentRom.getId();
        roomAPI    = endpoint + "/room/" + roomIdentifier;
        commandAPI = roomAPI + "/command";
        updatesAPI = roomAPI + "/updates";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createRoom() {
        Room room = new Room();
        room.setSlug("test");
        roomManager.createRoom(room, user.getUserId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void deleteRoom(String roomId){
        roomManager.deleteRoom(roomId);
    }

    public String getCurrentRoom() {
        return this.roomIdentifier;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendCommand(final String command, final CommandOptions commandOption, String roomId) {
        eventManager.sendCommand(user, currentRom,command, commandOption);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendReply(final String message, String replyTo, final CommandOptions commandOption, String roomId, Map<String, String> data) {
        eventManager.sendReply(user,message, replyTo, commandOption);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendReaction(String message, Reaction reaction, String reactToMessageId, CommandOptions commandOptions) {
        eventManager.sendReaction(user, currentRom,reaction, reactToMessageId, commandOptions);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendAdvertisement(AdvertisementOptions advertisement) {
        eventManager.sendAdvertisement(user, currentRom, advertisement);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendGoal(String message, String img, GoalOptions goalOptions) {
        eventManager.sendGoal(user, currentRom, message,img, goalOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setBanStatus(String userId, boolean isBanned) {
        userManager.setBanStatus(user, isBanned);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createOrUpdateUser() {
        userManager.createOrUpdateUser(user);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeMessage(Event event) {
        String id = event.getId();
        HttpClient httpClient = new HttpClient(context, "POST", this.roomAPI + "/remove/" + id, new FN().getApiHeaders(apiKey), null, apiCallback);
        httpClient.setAction("removeMessage");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void reportMessage(Event event) {
        String id = event.getId();
        HttpClient httpClient = new HttpClient(context, "POST", this.roomAPI + "/report/" + id, new FN().getApiHeaders(apiKey), null, apiCallback);
        httpClient.setAction("reportMessage");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void approveMessage(Event event) {
        String id = event.getId();
        HttpClient httpClient = new HttpClient(context, "POST", this.roomAPI + "/report/" + id, new FN().getApiHeaders(apiKey), null, apiCallback);
        httpClient.setAction("approveMessage");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUserMessages(int limit, String cursor) {
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/user/"+user.getUserId()+"/?limit=" + limit + "&cursor=" + cursor, new FN().getApiHeaders(apiKey), null, apiCallback);
        httpClient.setAction("listUserMessages");
        httpClient.execute();
    }
}
