package com.sportstalk.impl.rest;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.sportstalk.api.APICallback;
import com.sportstalk.models.chat.Event;
import com.sportstalk.api.chat.EventHandler;
import com.sportstalk.impl.Utils;
import com.sportstalk.api.chat.IEventManager;
import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.EventType;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportReason;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import androidx.annotation.RequiresApi;

public class RestfulEventManager implements IEventManager {
    CountDownLatch countDownLatch = null;
    JSONObject jsonObject = null;
    private SportsTalkConfig sportsTalkConfig;
    private boolean polling;
    private Map<String, String> apiHeaders;
    private Room currentRoom;
    private String updatesApi;
    private EventHandler eventHandler = null;
    private String roomApi;
    private String commandApi;
    private User user;
    private long lastCursor;
    private String lastMessageId;
    private String firstMessageId;
    private long firstMessageTime;
    private int pollFrequency = 800;
    private Timer timer;

    public RestfulEventManager(SportsTalkConfig sportsTalkConfig, EventHandler eventHandler) {
        setConfig(sportsTalkConfig);
    }

    /**
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
     *
     * @param user
     */
    public void setUser(User user) {
        this.sportsTalkConfig.setUser(user);
        this.user = this.sportsTalkConfig.getUser();
    }

    /**
     * sets polling frequency
     *
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
        if (timer != null) {
            timer.cancel();
            timer = null;

        }
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
            }
        };

        final HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", updatesApi, apiHeaders, data, apiCallback);
        httpClient.setAction("update");

        timer = new Timer();
        TimerTask task = new TimerTask() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                //send volley request here
                if (updatesApi != null) {
                    jsonObject = (JSONObject) httpClient.execute().getData();
                    handlePollUpdates(jsonObject);
                }
            }
        };
        timer.schedule(task, 0, pollFrequency);
    }

    /**
     * @param data
     */
    private void handlePollUpdates(JSONObject data) {
        Log.d(RestfulEventManager.class.getName(), " data " + data );
        try {
            JSONArray array = data.has("data") ? data.getJSONObject("data").getJSONArray("events") : null;
            int len = array == null ? 0 : array.length();
            if (len == 0) {
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
                event.setRoomId(jsonObject.getString("roomid"));
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
                event.setCustomPayload(jsonObject.getString("custompayload"));
                if ("chat.event".equals(eventKind)) {
                    if (eventType.equals("Purge")) {
                        eventHandler.onPurge(event);
                    } else if (eventType.equals("Reaction")) {
                        eventHandler.onReaction(event);
                    } else if (eventType.equals("reply")) {
                        JSONObject replyObject = jsonObject.getJSONObject("replyto");
                        event.setReplyTo(replyObject);
                        eventHandler.onReply(event);
                    } else if (eventType.equalsIgnoreCase("Speech")) {
                        eventHandler.onSpeech(event);
                    } else if (eventType.equals("api.result")) {
                        eventHandler.onEventStart(event);
                    } else {
                        eventHandler.onChat(event);
                    }
                } else {
                    eventHandler.onEventStart(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Room getCurrentRoom() {
        return this.currentRoom;
    }

    @Override
    public void setCurrentRoom(Room room) {
        this.lastCursor = 0;
        this.lastMessageId = null;
        this.firstMessageId = null;
        this.firstMessageTime = 0L;
        this.currentRoom = room;

        this.roomApi = this.sportsTalkConfig.getEndpoint() + "/chat/rooms/" + this.currentRoom.getId();
        this.commandApi = this.roomApi + "/command";
        this.updatesApi = this.roomApi + "/updates";
    }

    @Override
    public void getUpdates() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendCommand(User user, Room room, String command, CommandOptions options) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.sportsTalkConfig.getEndpoint()).append("/chat/rooms/").append(room.getId()).append("/command");
        Map<String, String> data = new HashMap<>();
        data.put("command", command);
        data.put("userid", this.user.getUserId());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendCommand");
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendReply(User user, String message, String replyTo, CommandOptions commandOptions) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/chat/rooms/").append(currentRoom.getId()).append("/command");
        Map<String, String> data = new HashMap<>();
        data.put("command", message);
        data.put("userid", this.user.getUserId());
        data.put("replyto", commandOptions.getReplyTo());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendReply");
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendReaction(User user, Room room, Reaction reaction, String reactionToMessageId, CommandOptions commandOptions) {
        Map<String, String> data = new HashMap<>();
        data.put("userid", this.user.getUserId());
        data.put("reaction", reaction.name());
        data.put("reacted", "true");
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/chat/rooms/" + currentRoom.getId() + "/react/" + reactionToMessageId, new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendReaction");
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendAdvertisement(User user, Room room, AdvertisementOptions advertisementOptions) {

        Map<String, String> data = new HashMap<>();
        data.put("command", "advertisement");
        data.put("customtype", "advertisement");
        data.put("userid", this.user.getUserId());
        data.put("command", "advertisement");

        Map<String, String> custom = new HashMap<>();
        custom.put("img", advertisementOptions.getImg());
        custom.put("link", advertisementOptions.getLink());
        custom.put("id", advertisementOptions.getId());

        data.put("custompayload", new JSONObject(custom).toString());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.commandApi, new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendAdvertisement");
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendGoal(User user, Room room, String message, String img, GoalOptions goalOptions) {
        Map<String, String> data = new HashMap<>();
        data.put("command", message);
        data.put("customtype", "goal");
        data.put("userid", user.getUserId());

        Map<String, String> custom = new HashMap<>();
        data.put("img", img);
        data.put("link", "");

        String s = String.format("{\"img\":\"%s\",\"link\":\"%s\"}", img, goalOptions.getLink());
        data.put("custompayload", s);
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", commandApi, new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("sendGoal");
        return httpClient.execute();
    }

    @Override
    public EventHandler getEventHandlers() {
        return null;
    }

    @Override
    public void setEventHandlers(EventHandler eventHandlers) {
        this.eventHandler = eventHandlers;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult reportEvent(EventResult eventResult, ReportReason reportReason) {
        Map<String, String> data = new HashMap<>();
        data.put("reporttype", ReportType.Abuse.name());
        data.put("userid", this.user.getUserId());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/chat/rooms/" + currentRoom.getId() + "/events/" + eventResult.getId() + "/report/" + reportReason.getUserId(), new Utils().getApiHeaders(sportsTalkConfig.getApiKey()), data, sportsTalkConfig.getApiCallback());
        return httpClient.execute();
    }

}
