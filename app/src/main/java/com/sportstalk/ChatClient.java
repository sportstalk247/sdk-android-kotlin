package com.sportstalk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.sportstalk.api.RoomUserResult;
import com.sportstalk.api.chat.IChatClient;
import com.sportstalk.api.chat.ITalkClient;
import com.sportstalk.api.impl.RestfulEventManager;
import com.sportstalk.api.impl.RestfulRoomManager;
import com.sportstalk.api.impl.RestfulUserManager;
import com.sportstalk.error.SettingsException;
import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.EventHandlerConfig;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportReason;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.rest.HttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class ChatClient  implements IChatClient {

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
    private String endpoint = "https://api.sportstalk247.com/api/v3/5dcb569438a2830dc0a28e22"; //"http://api-origin.sportstalk247.com/api/v3";
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

    private static ChatClient chatClient;

    private RestfulUserManager userManager;

    private Room currentRom;

    private ChatClient(final SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    public static ChatClient create(SportsTalkConfig sportsTalkConfig) {
        if(chatClient == null) chatClient = new ChatClient(sportsTalkConfig);
        return chatClient;
    }

    public void setConfig(final SportsTalkConfig config) {
        sportsTalkConfig = config;
        sportsTalkConfig.setEndpoint(sportsTalkConfig.getEndpoint() + "/" + sportsTalkConfig.getAppId());
        Log.d(TAG, "... endpoint.. " + endpoint);
        if(eventManager == null) eventManager = new RestfulEventManager(sportsTalkConfig, config.getEventHandler());
        if(roomManager == null) roomManager = new RestfulRoomManager(sportsTalkConfig);
        if(userManager == null) userManager = new RestfulUserManager(sportsTalkConfig);

        this.user = sportsTalkConfig.getUser();

        currentRom = new Room();
        sportsTalkConfig.setApiCallback(apiCallback);
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
    public List<Room> listRooms(Map<String, String> data) {
        return roomManager.listRooms();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUsers() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/user/?limit=100&cursor=", new FN().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("listUsers");
        httpClient.execute();
    }

    @Override
    public void setEventHandlers(EventHandlerConfig eventHandlerConfig) {
    }

    public void setRoom(Room room) {
        eventManager.setCurrentRoom(room);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startTalk() {
        eventManager.setCurrentRoom(currentRom);
        eventManager.startTalk();
    }

    public void stopTalk() {
        eventManager.stopTalk();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult report(EventResult eventResult, ReportType reportType) {
        ReportReason reportReason = new ReportReason();
        reportReason.setUserId(this.user.getUserId());
        reportReason.setReportType(reportType);
        return eventManager.reportEvent(eventResult, reportReason);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Room> listRooms() {
        return roomManager.listRooms();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public RoomUserResult joinRoom(RoomResult room) {
        currentRom = room;
        return roomManager.joinRoom(null, room);
    }

    @Override
    public Room getCurrentRoom() {
        return eventManager.getCurrentRoom();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<User> listParticipants(String cursor, int maxResults) {
        return roomManager.listParticipants(currentRom, cursor, maxResults);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendCommand(String command, CommandOptions commandOptions) {
        return eventManager.sendCommand(user, this.currentRom, command, commandOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendReply(String message, String replyTo, CommandOptions commandOptions) {
        return eventManager.sendReply(null, message, replyTo, commandOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendReaction(Reaction reaction, String reactToMessageId, CommandOptions commandOptions) {
        return eventManager.sendReaction(null,null,reaction, reactToMessageId,commandOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendAdvertisement(AdvertisementOptions advertisementOptions) {
        return eventManager.sendAdvertisement(null,null, advertisementOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendGoal(String message, String img, GoalOptions goalOptions) {
        return eventManager.sendGoal(null, null,message, img, goalOptions);
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public RoomUserResult exitRoom() throws SettingsException {
        if(eventManager.getCurrentRoom() != null)
            throw new SettingsException(Messages.CAN_NOT_EXIT_ROOM);
        return this.roomManager.exitRoom(this.user, this.currentRom);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public RoomResult createRoom(Room room) {
        return roomManager.createRoom(room, this.user.getUserId());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ApiResult deleteRoom(Room room) {
        return this.roomManager.deleteRoom(room.getId());
    }

}
