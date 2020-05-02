package com.sportstalk.impl.rest;

import android.os.Build;

import com.sportstalk.impl.common.rest.Utils;
import com.sportstalk.api.chat.IRoomManager;
import com.sportstalk.models.chat.EventResult;
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
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Room> listRooms() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/chat/rooms", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("listRooms");
        ApiResult apiResult = httpClient.execute();
        try {
            List<Room> list = new ArrayList<>();
            JSONArray array = ((JSONObject) apiResult.getData()).getJSONObject("data").getJSONArray("rooms");
            for (int i = 0; i < array.length(); i++) {
                RoomResult room = new RoomResult();
                room.setKind(Kind.chat);
                room.setId(array.getJSONObject(i).getString("id"));
                room.setOwnerId(array.getJSONObject(i).getString("ownerid"));
                room.setName(array.getJSONObject(i).getString("name"));
                room.setDescription(array.getJSONObject(i).getString("description"));
                room.setSlug(array.getJSONObject(i).getString("slug"));
                room.setEnableEnterandExit(array.getJSONObject(i).getBoolean("enableenterandexit"));
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
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sportsTalkConfig.getEndpoint() + "/room/" + id, apiHeaders, data, sportsTalkConfig.getApiCallback());
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

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/chat/rooms", apiHeaders, data, sportsTalkConfig.getApiCallback());
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
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/room/" + room.getId() + "/participants?cursor=" + cursor + "&maxresults=" + maxResults, apiHeaders, data, sportsTalkConfig.getEventHandler());
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
        sb.append(sportsTalkConfig.getEndpoint()).append("/chat/rooms/").append(room.getId()).append("/join");
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
        if(jsonObject == null) return null;
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
        sb.append(sportsTalkConfig.getEndpoint()).append("/room/").append(room.getId()).append("/join");
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
    public EventResult listUserMessages(User user, Room room, String cursor, int limit) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/chat/rooms/").append(room.getId()).append("/messagesbyuser/").append(user.getUserId());
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        JSONObject jsonObject = (JSONObject) httpClient.execute().getData();
        EventResult eventResult = new EventResult();
        eventResult.setBody(jsonObject.toString());
        return eventResult;
    }

}
