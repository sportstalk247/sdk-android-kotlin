package com.sportstalk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
     * Completable future object
     **/
    private CompletableFuture completableFuture = null;
    /**
     * call back used to fetch poll data
     **/
    private EventHandler eventHandler;


    public SportsTalkClient(final String apiKey) {
        this.apiKey = apiKey;
    }

    public SportsTalkClient(final SportsTalkConfig sportsTalkConfig) {
        this.appId            = sportsTalkConfig.getAppId();
        this.apiKey           = sportsTalkConfig.getApiKey();
        this.userId           = sportsTalkConfig.getUserId();
        this.endpoint         = sportsTalkConfig.getEndpoint() == null ? this.endpoint : sportsTalkConfig.getEndpoint();
        this.context          = sportsTalkConfig.getContext();
        this.user             = sportsTalkConfig.getUser();
        this.eventHandler     = sportsTalkConfig.getEventHandler();
        this.apiCallback      = sportsTalkConfig.getApiCallback();
    }

    /**
     * sets end point
     **/
    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
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
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/room", new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("listRooms");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUsers() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/user/?limit=100&cursor=", new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("listUsers");
        httpClient.execute();
    }

    /**
     * starts polling. The default polling frequency is 800ms
     */
    private void startPollUpdate() throws SportsTalkSettingsException {
        if(updatesAPI == null || roomAPI == null) throw new SportsTalkSettingsException("");
        if(pollFrequency <250 || pollFrequency>5000) throw new SportsTalkSettingsException(INVALID_POLLING_FREQUENCY);
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                CompletableFuture completableFuture = getUpdates();
                try {
                    final ApiResult<JSONObject> apiResult1 = (ApiResult) completableFuture.get();
                    if (apiResult1 != null)
                        handlePoll(apiResult1.getData());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ses.scheduleAtFixedRate(task, 0L, pollFrequency, TimeUnit.MILLISECONDS);
    }

    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture getUpdates() {
        completableFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                APICallback apiCallback = new APICallback() {
                    @Override
                    public void execute(ApiResult<JSONObject> apiResult, String action) {
                        completableFuture.complete(apiResult);
                    }

                    @Override
                    public void error(ApiResult<JSONObject> apiResult, String action) {
                        completableFuture.complete(apiResult);
                    }
                };

                Map<String, String> data = new HashMap<>();
                if (updatesAPI != null) {
                    HttpClient httpClient = new HttpClient(context, "GET", updatesAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
                    httpClient.setAction("update");
                    httpClient.execute();
                }
            }
        });
        return completableFuture;
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
                    }else {
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
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/room/" + roomId + "/participants?cursor=" + cursor + "&maxresults=" + maxResults, new FN().getApiHeaders(apiKey), null, apiCallback);
        httpClient.setAction("listParticipants");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void joinRoom(String roomId, Map<String, String> data) {
        APICallback apiCallback1 = new APICallback() {
            @Override
            public void execute(ApiResult<JSONObject> jsonObject, String action) {
                try {
                    roomIdentifier = jsonObject.getData().getJSONObject("data").getJSONObject("room").getString("id");
                    roomAPI    = endpoint + "/room/" + roomIdentifier;
                    commandAPI = roomAPI + "/command";
                    updatesAPI = roomAPI + "/updates";

                    // starts polling
                    Thread.sleep(500);
                    startPollUpdate();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch(SportsTalkSettingsException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                    Log.e(TAG, ie.getMessage());
                }

                apiCallback.execute(jsonObject, action);
            }

            @Override
            public void error(ApiResult<JSONObject> jsonObject, String action) {
                apiCallback.error(jsonObject, action);
            }
        };

        StringBuilder sb = new StringBuilder();
        sb.append(this.endpoint).append("/room/").append(roomId).append("/join");
        HttpClient httpClient = new HttpClient(context, "POST", sb.toString(), new FN().getApiHeaders(apiKey), data, apiCallback1);
        httpClient.setAction("joinRoom");
        httpClient.execute();
    }

    public String getCurrentRoom() {
        return this.roomIdentifier;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendCommand(final String command, final CommandOptions commandOption, String roomId) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.endpoint).append("/room/").append(roomId).append("/command");
        Map<String, String> data = new HashMap<>();
        data.put("command", command);
        data.put("userid", user.getUserId());
        data.put("customtype", "");
        data.put("customid", "");
        data.put("custompayload", "");

        HttpClient httpClient = new HttpClient(context, "POST", sb.toString(), new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("sendCommand");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendReply(final String command, final CommandOptions commandOption, int roomId, Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.endpoint).append("/room/").append(roomId).append("/command");
        data.put("command", command);
        HttpClient httpClient = new HttpClient(context, "POST", sb.toString(), new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("sendReply");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendReaction(String message, Reaction reaction, String reactToMessageId, CommandOptions commandOptions) {
        Map<String, String> data = new HashMap<>();

        data.put("userid",    userId);
        data.put("reaction",  reaction.name());
        data.put("reacted", "true");

        HttpClient httpClient = new HttpClient(context, "POST", endpoint + "/room/" + roomIdentifier + "/react/" + reactToMessageId, new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("sendReaction");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendAdvertisement(AdvertisementOptions advertisement) {
        Map<String, String> data = new HashMap<>();
        data.put("command",    "advertisement");
        data.put("customtype", "advertisement");
        data.put("userid",        user.getUserId());
        data.put("command",    "advertisement");

        Map<String, String> custom = new HashMap<>();
        custom.put("img", advertisement.getImg());
        custom.put("link",advertisement.getLink());
        custom.put("id",  advertisement.getId());

        data.put("custompayload", new JSONObject(custom).toString());

        HttpClient httpClient = new HttpClient(context, "POST", commandAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("sendAdvertisement");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendGoal(String message, String img, GoalOptions goalOptions) {
        Map<String, String> data = new HashMap<>();
        data.put("command",     message);
        data.put("customtype","goal");
        data.put("userid",       user.getUserId());

        Map<String, String> custom = new HashMap<>();
        data.put("img",  img);
        data.put("link", "");

        data.put("custompayload", new JSONObject(custom).toString());

        HttpClient httpClient = new HttpClient(context, "POST", commandAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("sendGoal");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setBanStatus(String userId, boolean isBanned) {
        Map<String, String> data = new HashMap<>();
        data.put("banned", Boolean.toString(isBanned));
        HttpClient httpClient = new HttpClient(context, "POST", this.endpoint + "/user/" + userId + "/ban", new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("banStatus");
        httpClient.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createOrUpdateUser() {
        Map<String, String> data = new HashMap<>();
        data.put("userid",      user.getUserId());
        data.put("handle",      user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("profileurl",  user.getPictureUrl());
        data.put("pictureurl",  user.getPictureUrl());

        HttpClient httpClient = new HttpClient(context, "POST", this.endpoint + "/user/" + user.getUserId(), new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("user");
        httpClient.execute();
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
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/user/?limit=" + limit + "&cursor=" + cursor, new FN().getApiHeaders(apiKey), null, apiCallback);
        httpClient.setAction("listUserMessages");
        httpClient.execute();
    }
}
