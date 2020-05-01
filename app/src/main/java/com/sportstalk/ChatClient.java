package com.sportstalk;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import com.sportstalk.api.APICallback;
import com.sportstalk.impl.Utils;
import com.sportstalk.api.chat.EventHandler;
import com.sportstalk.api.chat.IChatClient;
import com.sportstalk.impl.Messages;
import com.sportstalk.impl.rest.RestfulEventManager;
import com.sportstalk.impl.rest.RestfulRoomManager;
import com.sportstalk.impl.rest.RestfulUserManager;
import com.sportstalk.error.SettingsException;
import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.EventHandlerConfig;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportReason;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;
import com.sportstalk.impl.rest.HttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class ChatClient implements IChatClient {

    /**
     * chat client
     **/
    private static ChatClient chatClient;
    /**
     * invalid polling frequency message
     **/
    private final String INVALID_POLLING_FREQUENCY = "Invalid poll _pollFrequency.  Must be between 250ms and 5000ms";
    /**
     * error message in case not joined the room
     **/
    private final String NO_ROOM_SET = "No room set.  You must join a room before you can get updates!";
    /**
     * Android Log
     **/
    private final String TAG = ChatClient.class.getName();
    /**
     * callback handler
     **/
    private APICallback apiCallback;
    /**
     * default endpoint
     **/
    private String endpoint = "https://api.sportstalk247.com/api/v3"; //"http://api-origin.sportstalk247.com/api/v3";
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
    private RestfulRoomManager roomManager;
    /**
     * Restful event manager
     **/
    private RestfulEventManager eventManager;
    /**
     * Restful user manager
     **/
    private RestfulUserManager userManager;

    private Room currentRom;

    private ChatClient(final SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    public static ChatClient create(SportsTalkConfig sportsTalkConfig) {
        if (chatClient == null) chatClient = new ChatClient(sportsTalkConfig);
        return chatClient;
    }

    public void setConfig(final SportsTalkConfig config) {
        sportsTalkConfig = config;
        sportsTalkConfig.setEndpoint(sportsTalkConfig.getEndpoint() + "/" + sportsTalkConfig.getAppId());
        if (eventManager == null)
            eventManager = new RestfulEventManager(sportsTalkConfig, config.getEventHandler());
        if (roomManager == null) roomManager = new RestfulRoomManager(sportsTalkConfig);
        if (userManager == null) userManager = new RestfulUserManager(sportsTalkConfig);

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
     *
     * @param pollFrequency
     */
    public void setUpdateFrequency(long pollFrequency) {
        this.pollFrequency = pollFrequency;
    }


    /**
     * lists all users
     */
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listUsers() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(context, "GET", this.endpoint + "/user/?limit=100&cursor=", new Utils().getApiHeaders(apiKey), data, apiCallback);
        httpClient.setAction("listUsers");
        httpClient.execute();
    }

    @Override
    public void setEventHandlers(EventHandlerConfig eventHandlerConfig) {
    }

    public void setRoom(Room room) {
        eventManager.setCurrentRoom(room);
    }

    /**
     * starts talk which will start polling
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startTalk() {
        eventManager.setCurrentRoom(currentRom);
        eventManager.startTalk();
    }

    /**
     * stops the talk
     */
    public void stopTalk() {
        eventManager.stopTalk();
    }

    /**
     * report an event
     *
     * @param eventResult
     * @param reportType
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult report(EventResult eventResult, ReportType reportType) {
        ReportReason reportReason = new ReportReason();
        reportReason.setUserId(this.user.getUserId());
        reportReason.setReportType(reportType);
        return eventManager.reportEvent(eventResult, reportReason);
    }

    /**
     * returns list rooms
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Room> listRooms() {
        return roomManager.listRooms();
    }

    /**
     * join room
     *
     * @param room
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public RoomUserResult joinRoom(RoomResult room) {
        currentRom = room;
        return roomManager.joinRoom(this.user, room);
    }

    /**
     * gets current room
     *
     * @return
     */
    @Override
    public Room getCurrentRoom() {
        return eventManager.getCurrentRoom();
    }

    /**
     * list participants of a current room
     *
     * @param cursor
     * @param maxResults
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<User> listParticipants(String cursor, int maxResults) {
        return roomManager.listParticipants(currentRom, cursor, maxResults);
    }

    /**
     * sends command
     *
     * @param command
     * @param commandOptions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendCommand(String command, CommandOptions commandOptions) {
        return eventManager.sendCommand(user, this.currentRom, command, commandOptions);
    }

    /**
     * sends a reply to a command
     *
     * @param message
     * @param replyTo
     * @param commandOptions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendReply(String message, String replyTo, CommandOptions commandOptions) {
        return eventManager.sendReply(null, message, replyTo, commandOptions);
    }

    /**
     * sends a reaction
     *
     * @param reaction
     * @param reactToMessageId
     * @param commandOptions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendReaction(Reaction reaction, String reactToMessageId, CommandOptions commandOptions) {
        return eventManager.sendReaction(null, null, reaction, reactToMessageId, commandOptions);
    }

    /**
     * send advertisement
     *
     * @param advertisementOptions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendAdvertisement(AdvertisementOptions advertisementOptions) {
        return eventManager.sendAdvertisement(null, null, advertisementOptions);
    }

    /**
     * send goal
     *
     * @param message
     * @param img
     * @param goalOptions
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult sendGoal(String message, String img, GoalOptions goalOptions) {
        return eventManager.sendGoal(this.user, null, message, img, goalOptions);
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * exit room
     *
     * @return
     * @throws SettingsException
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public RoomUserResult exitRoom() throws SettingsException {
        if (eventManager.getCurrentRoom() != null)
            throw new SettingsException(Messages.CAN_NOT_EXIT_ROOM);
        return this.roomManager.exitRoom(this.user, this.currentRom);
    }

    /**
     * create a room
     *
     * @param room
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public RoomResult createRoom(Room room) {
        return roomManager.createRoom(room, this.user.getUserId());
    }

    /**
     * deletes a room
     *
     * @param room
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public ApiResult deleteRoom(Room room) {
        return this.roomManager.deleteRoom(room.getId());
    }

    /**
     * list users messages
     *
     * @param user
     * @param room
     * @param cursor
     * @param limit
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public EventResult listUserMessages(User user, Room room, String cursor, int limit) {
        return this.roomManager.listUserMessages(user, room, cursor, limit);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public UserResult createOrUpdateUser(User user, boolean status) {
        return this.userManager.createOrUpdateUser(user);
    }
}
