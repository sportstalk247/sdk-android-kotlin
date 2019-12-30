package com.sportstalk247;

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
    private PollEventHandler pollEventHandler;

    public SportsTalkClient(final String apiKey) {
        this.apiKey = apiKey;
    }

    public SportsTalkClient(final SportsTalkConfig sportsTalkConfig) {
        this.appId = sportsTalkConfig.getAppId();
        this.apiKey = sportsTalkConfig.getApiKey();
        this.userId = sportsTalkConfig.getUserId();
        this.endpoint = sportsTalkConfig.getEndpoint() == null ? this.endpoint : sportsTalkConfig.getEndpoint();
        this.context = sportsTalkConfig.getContext();
        this.user = sportsTalkConfig.getUser();
        this.pollEventHandler = sportsTalkConfig.getPollEventHandler();
    }

    /**
     * sets end point
     **/
    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listRooms(Map<String, String> data, APICallback apiCallback) {
        Axios axios = new Axios(context, "GET", this.endpoint + "/room", new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.setAction("listRooms");
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUsers(Context context, APICallback apiCallback) {
        Map<String, String> data = new HashMap<>();
        Axios axios = new Axios(context, "GET", this.endpoint + "/user/?limit=100&cursor=", new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.execute();
    }

    public void startPollUpdate() {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                CompletableFuture completableFuture = getUpdates();
                try {
                    final ApiResult<JSONObject> apiResult1 = (ApiResult) completableFuture.get();
                    Log.d(TAG, ".....got response from update calling handlepoll.......");
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
                    Axios axios = new Axios(context, "GET", updatesAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
                    axios.setAction("update");
                    axios.execute();
                }
            }
        });
        return completableFuture;
    }

    private void handlePoll(JSONObject data) {
        try {
            JSONArray array = data.getJSONArray("data");
            int len = array == null ? 0 : array.length();
            for (int i = 0; i < len; i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String eventType = jsonObject.getString("eventtype");
                Event event = new Event();
                event.setAdded(Integer.parseInt(jsonObject.getString("added")));
                event.setId(jsonObject.getString("id"));
                event.setRoomId(jsonObject.getString("roomId"));
                event.setBody(jsonObject.getString("body"));
                event.setUserId(jsonObject.getString("userid"));
                event.setEventType(EventType.Purge);
                if (eventType.equals("Purge")) {
                    pollEventHandler.onPurge(event);
                } else if (eventType.equals("Reaction")) {
                    pollEventHandler.onReaction(event);
                } else if (eventType.equals("Reply")) {
                    pollEventHandler.onReaction(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listParticipants(Context context, APICallback apiCallback, int roomId, String cursor, int maxResults) {
        Axios axios = new Axios(context, "GET", this.endpoint + "/room/" + roomId + "/participants?cursor=" + cursor + "&maxresults=" + maxResults, new FN().getApiHeaders(apiKey), null, apiCallback);
        axios.setAction("listParticipants");
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void joinRoom(final APICallback apiCallback, String roomId, Map<String, String> data) {
        APICallback apiCallback1 = new APICallback() {
            @Override
            public void execute(ApiResult<JSONObject> jsonObject, String action) {
                try {
                    roomIdentifier = jsonObject.getData().getJSONObject("data").getJSONObject("room").getString("id");
                    roomAPI = endpoint + "/room/" + roomIdentifier;
                    commandAPI = roomAPI + "/command";
                    updatesAPI = roomAPI + "/updates";

                } catch (JSONException e) {
                    e.printStackTrace();
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
        Axios axios = new Axios(context, "POST", sb.toString(), new FN().getApiHeaders(apiKey), data, apiCallback1);
        axios.setAction("joinRoom");
        axios.execute();
    }

    public String getCurrentRoom() {
        return this.roomIdentifier;
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendCommand(final String command, final CommandOptions commandOption, String roomId, APICallback apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.endpoint).append("/room/").append(roomId).append("/command");
        Map<String, String> data = new HashMap<>();
        data.put("command", command);
        data.put("userid", user.getUserId());
        data.put("customtype", "");
        data.put("customid", "");
        data.put("custompayload", "");

        Axios axios = new Axios(context, "POST", sb.toString(), new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.setAction("command");
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendReply(Context context, final String command, final CommandOptions commandOption, int roomId, Map<String, String> data, APICallback apiCallback) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.endpoint).append("/room/").append(roomId).append("/command");
        data.put("command", command);
        Axios axios = new Axios(context, "POST", sb.toString(), new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendReaction(String message, Reaction reaction, String reactToMessageId, CommandOptions commandOptions) {
        Map<String, String> data = new HashMap<>();

        data.put("userid", userId);
        data.put("reaction", reaction.name());
        data.put("reacted", "true");

        Axios axios = new Axios(context, "POST", endpoint + "/room/" + roomIdentifier + "/react/" + reactToMessageId, new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendAdvertisement(AdvertisementOptions advertisement) {
        Map<String, String> data = new HashMap<>();
        data.put("command", "advertisement");
        data.put("customtype", "advertisement");
        data.put("userid", user.getUserId());
        data.put("command", "advertisement");

        Map<String, String> custom = new HashMap<>();
        custom.put("img", advertisement.getImg());
        custom.put("link", advertisement.getLink());
        custom.put("id", advertisement.getId());

        data.put("custompayload", new JSONObject(custom).toString());

        Axios axios = new Axios(context, "POST", commandAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendGoal(String message, String img, GoalOptions goalOptions) {
        Map<String, String> data = new HashMap<>();
        data.put("command", message);
        data.put("customtype", "goal");
        data.put("userid", user.getUserId());

        Map<String, String> custom = new HashMap<>();
        data.put("img", img);
        data.put("link", "");

        data.put("custompayload", new JSONObject(custom).toString());

        Axios axios = new Axios(context, "POST", commandAPI, new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setBanStatus(String userId, boolean isBanned) {
        Map<String, String> data = new HashMap<>();
        data.put("banned", Boolean.toString(isBanned));
        Axios axios = new Axios(context, "POST", this.endpoint + "/user/" + userId + "/ban", new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createOrUpdateUser(APICallback apiCallback) {
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("handle", user.getHandle());
        data.put("displayname", user.getDisplayName());
        data.put("profileurl", user.getPictureUrl());
        data.put("pictureurl", user.getPictureUrl());

        Axios axios = new Axios(context, "POST", this.endpoint + "/user/" + user.getUserId(), new FN().getApiHeaders(apiKey), data, apiCallback);
        axios.setAction("user");
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeMessage(Event event) {
        String id = event.getId();
        Axios axios = new Axios(context, "POST", this.roomAPI + "/remove/" + id, new FN().getApiHeaders(apiKey), null, apiCallback);
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void reportMessage(Event event) {
        String id = event.getId();
        Axios axios = new Axios(context, "POST", this.roomAPI + "/report/" + id, new FN().getApiHeaders(apiKey), null, apiCallback);
        axios.execute();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void approveMessage(Event event) {
        String id = event.getId();
        Axios axios = new Axios(context, "POST", this.roomAPI + "/report/" + id, new FN().getApiHeaders(apiKey), null, apiCallback);
        axios.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUserMessages(int limit, String cursor) {
        Axios axios = new Axios(context, "GET", this.roomAPI + "/user/?limit=" + limit + "&cursor=" + cursor, new FN().getApiHeaders(apiKey), null, apiCallback);
        axios.execute();
    }
}
