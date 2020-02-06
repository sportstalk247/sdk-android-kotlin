package com.sportstalk.api;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.sportstalk.APICallback;
import com.sportstalk.AdvertisementOptions;
import com.sportstalk.ApiResult;
import com.sportstalk.CommandOptions;
import com.sportstalk.Event;
import com.sportstalk.EventHandler;
import com.sportstalk.EventType;
import com.sportstalk.GoalOptions;
import com.sportstalk.Kind;
import com.sportstalk.Reaction;
import com.sportstalk.SportsTalkConfig;
import com.sportstalk.User;
import com.sportstalk.Utils;
import com.sportstalk.rest.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RestfulEventManager implements IEventManager {
    private SportsTalkConfig sportsTalkConfig;
    private boolean polling;
    private Map<String, String> apiHeaders;
    private Room currentRoom;
    private String updatesApi;
    private EventHandler eventHandler = null;
    private String roomApi;
    private String commandApi;
    private User user;

    private long   lastCursor;
    private String lastMessageId;
    private String firstMessageId;
    private long   firstMessageTime;

    private int pollFrequency = 800;

    public RestfulEventManager(SportsTalkConfig sportsTalkConfig, EventHandler eventHandler) {
        setConfig(sportsTalkConfig);
    }


    /**
     *
     * @param sportsTalkConfig
     */
    private void setConfig(SportsTalkConfig sportsTalkConfig) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.user = sportsTalkConfig.getUser();
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
        this.eventHandler = sportsTalkConfig.getEventHandler();
    }


    /**
     * sets user
     * @param user
     */
    public void setUser(User user) {
        this.sportsTalkConfig.setUser(user);
        this.user = this.sportsTalkConfig.getUser();
    }

    /**
     * sets polling frequency
     * @param pollFrequency
     */
    public void setPollingFrequency(int pollFrequency) {
        this.pollFrequency = pollFrequency;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void startTalk() {
        startPollUpdate();
    }

    @Override
    public void stopTalk() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startPollUpdate() {
        Map<String, String> data = new HashMap<>();
        APICallback apiCallback = new APICallback() {
            @Override
            public void execute(ApiResult<JSONObject> apiResult, String action) {
                handlePollUpdates(apiResult.getData());
            }

            @Override
            public void error(ApiResult<JSONObject> apiResult, String action) {
                System.out.println("... ant error??? " );
            }
        };

        final HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", updatesApi, apiHeaders, data, apiCallback);
        httpClient.setAction("update");

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run () {
                //send volley request here
                if (updatesApi != null) {
                    httpClient.execute();
                }
            }
        };
        timer.schedule(task, 0, pollFrequency);
    }

    /**
     *
     * @param data
     */
    private void handlePollUpdates(JSONObject data) {

        try {
            JSONArray array = data.getJSONArray("data");
            int len = array == null ? 0 : array.length();
            if(len == 0) {
                Event event = new Event();
                eventHandler.onEventStart(event);
                return;
            }


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
                JSONObject userJsonObject = jsonObject.getJSONObject("user");
                User user = new User();
                user.setHandle(userJsonObject.getString("handle"));
                user.setDisplayName(userJsonObject.getString("displayname"));
                user.setPictureUrl(userJsonObject.getString("pictureurl"));
                user.setProfileUrl(userJsonObject.getString("profileurl"));
                event.setUser(user);
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
                    }else if (eventType.equals("api.result")) {
                        eventHandler.onEventStart(event);
                    }
                    else {
                        eventHandler.onChat(event);
                    }
                }else{
                    eventHandler.onEventStart(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentRoom(Room room) {
        this.lastCursor = 0;
        this.lastMessageId = null;
        this.firstMessageId = null;
        this.firstMessageTime = 0L;
        this.currentRoom = room;
        this.roomApi = this.sportsTalkConfig.getEndpoint() + "/room/"+ this.currentRoom.getId();
        this.commandApi = this.roomApi + "/command";
        this.updatesApi = this.roomApi + "/updates";
    }

    @Override
    public void setEventHandlers(EventHandler eventHandlers) {
        this.eventHandler = eventHandlers;
    }

    @Override
    public Room getCurrentRoom() {
        return this.currentRoom;
    }

    @Override
    public void getUpdates() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sendCommand(User user, Room room, String command, CommandOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/room/").append(room.getId()).append("/command");
        Map<String, String> data = new HashMap<>();
        data.put("command", command);
        data.put("userid", user.getUserId());
        data.put("customtype", "");
        data.put("customid", "");
        data.put("custompayload", "");

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendCommand");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sendReply(User user, String message, String replyTo, CommandOptions commandOptions) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/room/").append(currentRoom.getId()).append("/command");
        Map<String, String>data = new HashMap<>();
        data.put("command", message);
        data.put("userid",  user.getUserId());
        data.put("replyto", commandOptions.getReplyTo());
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendReply");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sendReaction(User user, Room room, Reaction reaction, String reactionToMessageId, CommandOptions commandOptions) {
        Map<String, String> data = new HashMap<>();
        data.put("userid",    user.getUserId());
        data.put("reaction",  reaction.name());
        data.put("reacted", "true");
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/room/" + room.getId() + "/react/" + reactionToMessageId, new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendReaction");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sendAdvertisement(User user, Room room, AdvertisementOptions advertisementOptions) {

        Map<String, String> data = new HashMap<>();
        data.put("command",    "advertisement");
        data.put("customtype", "advertisement");
        data.put("userid",        user.getUserId());
        data.put("command",    "advertisement");

        Map<String, String> custom = new HashMap<>();
        custom.put("img",  advertisementOptions.getImg());
        custom.put("link", advertisementOptions.getLink());
        custom.put("id",   advertisementOptions.getId());

        data.put("custompayload", new JSONObject(custom).toString());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.commandApi, new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendAdvertisement");
        httpClient.execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void sendGoal(User user, Room room, String img, String message, GoalOptions goalOptions) {
        Map<String, String> data = new HashMap<>();
        data.put("command",     message);
        data.put("customtype","goal");
        data.put("userid",       user.getUserId());

        Map<String, String> custom = new HashMap<>();
        data.put("img",  img);
        data.put("link", "");

        data.put("custompayload", new JSONObject(custom).toString());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", commandApi, new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendGoal");
        httpClient.execute();
    }

    @Override
    public EventHandler getEventHandlers() {
        return null;
    }

}
