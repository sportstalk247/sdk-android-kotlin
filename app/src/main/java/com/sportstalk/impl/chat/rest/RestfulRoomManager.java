package com.sportstalk.impl.chat.rest;

import android.os.Build;

import com.sportstalk.impl.common.rest.Utils;
import com.sportstalk.api.chat.IRoomManager;
import com.sportstalk.impl.common.rest.HttpClient;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.EventType;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.SportsTalkConfig;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class RestfulRoomManager implements IRoomManager {

    private String TAG = RestfulRoomManager.class.getName();

    private SportsTalkConfig sportsTalkConfig;

    private Map<String, String> apiHeaders;

    private List<Room> knownRooms;

    public RestfulRoomManager(final SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    private void setConfig(SportsTalkConfig sportsTalkConfig) {
        this.sportsTalkConfig = sportsTalkConfig;
        knownRooms = new ArrayList<>();
        this.apiHeaders = Utils.getApiHeaders(sportsTalkConfig.getApiKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Room> listRooms() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/" + sportsTalkConfig.getAppId() + "/chat/rooms", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("listRooms");
        ApiResult apiResult = httpClient.execute();
        try {
            List<Room> list = new ArrayList<>();
            JSONArray array = ((JSONObject) apiResult.getData()).getJSONArray("data");
            for (int i = 0; i < array.length(); i++) {
                RoomResult room = new RoomResult();
                room.setKind(Kind.chat);
                room.setId(array.getJSONObject(i).getString("id"));
                room.setOwnerId(array.getJSONObject(i).getString("ownerid"));
                room.setName(array.getJSONObject(i).getString("name"));
                room.setDescription(array.getJSONObject(i).getString("description"));
                room.setSlug(array.getJSONObject(i).getString("slug"));
                room.setEnableEnterandExit(array.getJSONObject(i).getBoolean("enableEnterAndExit"));
                room.setRoomIsOpen(array.getJSONObject(i).getBoolean("open"));
                room.setInRoom(array.getJSONObject(i).getInt("inroom"));
                room.setWhenModified(array.getJSONObject(i).getString("whenmodified"));

                list.add(room);
            }

            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Room> getKnownRooms() {
        return knownRooms;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public ApiResult deleteRoom(String id) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sportsTalkConfig.getEndpoint() + "/"+sportsTalkConfig.getAppId()+"/room/" + id, apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("deleteRoom");
        return httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public RoomResult createRoom(final Room room, final String userId) {

        Map<String, String> data = new HashMap<>();
        data.put("slug", room.getSlug());
        data.put("userid", userId);
        data.put("name", room.getName());
        data.put("description", room.getDescription());
        data.put("moderation", "post");
        data.put("enableactions", room.isEnableEnterandExit() == true ? "true" : "false");

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/"+sportsTalkConfig.getAppId()+ "/chat/rooms", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("createRoom");
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        try {
            JSONObject dataObject = jsonObject.getJSONObject("data");
            return createRoomObject(dataObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<User> listParticipants(Room room, String cursor, int maxResults) {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/" + sportsTalkConfig.getAppId() + "/room/" + room.getId() + "/participants?cursor=" + cursor + "&maxresults=" + maxResults, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("listParticipants");
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();

        List<User> list = new ArrayList<>();
        try {
            JSONObject object = jsonObject.getJSONObject("data");
            JSONArray array = object.getJSONArray("participants");
            int size = array == null ? 0 : array.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonParticipant = array.getJSONObject(i);
                UserResult userResult = new UserResult();
                userResult.setKind(Kind.user);
                userResult.setUserId(jsonParticipant.getString("userid"));
                userResult.setHandle(jsonParticipant.getString("handle"));
                userResult.setHandleLowerCase(jsonParticipant.getString("handlelowercase"));
                userResult.setDisplayName(jsonParticipant.getString("displayname"));
                userResult.setPictureUrl(jsonParticipant.getString("pictureurl"));
                userResult.setProfileUrl(jsonParticipant.getString("profileurl"));
                userResult.setBanned(jsonParticipant.getBoolean("banned"));

                list.add(userResult);
            }
        } catch (JSONException ex) {
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public RoomUserResult joinRoom(User user, RoomResult room) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/").append(sportsTalkConfig.getAppId()).append("/chat/rooms/").append(room.getId()).append("/join");
        Map<String, String> data = new HashMap<>();
        data.put("userid", user.getUserId());
        data.put("handle", user.getHandle());
        data.put("displayname", user.getDisplayName());

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("joinRoom");
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        RoomUserResult roomUserResult = new RoomUserResult();
        roomUserResult.setRoomResult(createRoomResult(jsonObject));
        return roomUserResult;
    }

    /**
     * @param jsonObject
     * @return
     */
    private RoomResult createRoomResult(JSONObject jsonObject) {
        RoomResult roomResult = new RoomResult();
        try {
            JSONObject object = jsonObject.getJSONObject("data");
            JSONObject roomObject = object.getJSONObject("room");

            roomResult.setId(roomObject.getString("id"));
            roomResult.setKind(Kind.room);
            roomResult.setOwnerId(roomObject.getString("ownerid"));
            roomResult.setDescription(roomObject.getString("description"));
            roomResult.setName(roomObject.getString("name"));
            roomResult.setRoomIsOpen(roomObject.getBoolean("open"));
            roomResult.setEnableEnterandExit(roomObject.getBoolean("enableEnterAndExit"));
            roomResult.setInRoom(roomObject.getInt("inroom"));
            roomResult.setWhenModified(roomObject.getString("whenmodified"));
            roomResult.setModeration(roomObject.getString("moderation"));
            roomResult.setMaxReports(roomObject.getInt("maxreports"));
        } catch (JSONException ex) {
        }
        return roomResult;
    }

    private RoomResult createRoomObject(JSONObject roomObject) {

        RoomResult roomResult = new RoomResult();
        try {

            roomResult.setId(roomObject.getString("id"));
            roomResult.setKind(Kind.room);
            roomResult.setOwnerId(roomObject.getString("ownerid"));
            roomResult.setDescription(roomObject.getString("description"));
            roomResult.setName(roomObject.getString("name"));
            roomResult.setRoomIsOpen(roomObject.getBoolean("open"));
            roomResult.setEnableEnterandExit(roomObject.getBoolean("enableEnterAndExit"));
            roomResult.setInRoom(roomObject.getInt("inroom"));
            roomResult.setWhenModified(roomObject.getString("whenmodified"));
            roomResult.setModeration(roomObject.getString("moderation"));
            roomResult.setMaxReports(roomObject.getInt("maxreports"));
        } catch (JSONException ex) {
        }
        return roomResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public RoomUserResult exitRoom(User user, Room room) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/").append(sportsTalkConfig.getAppId()).append("/room/").append(room.getId()).append("/join");
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("exitRoom");
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        RoomUserResult roomUserResult = new RoomUserResult();
        roomUserResult.setRoomResult(createRoomResult(jsonObject));
        return roomUserResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<EventResult> listUserMessages(User user, Room room, String cursor, int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/chat/rooms/").append(room.getId()).append("/messagesbyuser/").append(user.getUserId()).append("/?limit=").append(limit).append("&cursor=").append(cursor);
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        List<EventResult>list = new ArrayList<>();

        try {
            JSONArray eventArray = jsonObject.getJSONObject("data").getJSONArray("events");
            int size = eventArray == null ? 0 : eventArray.length();
            for(int i = 0; i<size; i++) {
                EventResult eventResult = new EventResult();
                JSONObject eventObject = eventArray.getJSONObject(i);
                eventResult.setKind(Kind.chat);
                eventResult.setId(eventObject.getString("id"));
                eventResult.setRoomId(eventObject.getString("roomid"));
                eventResult.setBody(eventObject.getString("body"));
                eventResult.setAdded(eventObject.getInt("added"));
                eventResult.setEventType(EventType.valueOf(eventObject.getString("eventtype")));
                eventResult.setUserId(eventObject.getString("userid"));

                JSONObject userObject = eventObject.getJSONObject("user");
                User evtUser = new User();
                evtUser.setUserId(userObject.getString("userid"));
                evtUser.setHandle(userObject.getString("handle"));
                evtUser.setDisplayName(userObject.getString("displayname"));
                evtUser.setProfileUrl(userObject.getString("profileurl"));
                evtUser.setPictureUrl(userObject.getString("pictureurl"));
                evtUser.setKind(Kind.user);
                eventResult.setUser(evtUser);

                list.add(eventResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
