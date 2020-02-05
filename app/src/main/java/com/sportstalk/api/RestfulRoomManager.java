package com.sportstalk.api;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sportstalk.AdvertisementOptions;
import com.sportstalk.RoomResult;
import com.sportstalk.SportsTalkConfig;
import com.sportstalk.User;
import com.sportstalk.Utils;
import com.sportstalk.rest.HttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RestfulRoomManager implements IRoomManager {

    private String TAG = RestfulRoomManager.class.getName();

    private SportsTalkConfig sportsTalkConfig;

    private Map<String, String> apiHeaders;

    private List<Room> knownRooms;

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

    private boolean isPushEnabled;

    private CompletableFuture<Room> roomFuture;

    private Room userCreatedRoom;

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
    public void listRooms() {
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/room", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("listRooms");
        httpClient.execute();
    }

    public List<Room> getKnownRooms() {
        return knownRooms;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void deleteRoom(String id) {
        Map<String,String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sportsTalkConfig.getEndpoint() + "/room/"+id, apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("deleteRoom");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void createRoom(final Room room, final String userId) {

                Map<String,String>data = new HashMap<>();
                data.put("slug", room.getSlug());
                data.put("userid", userId);
                data.put("name", room.getName());
                data.put("description", room.getDescription());
                data.put("moderation", "post");
                data.put("enableactions", room.isEnableEnterandExit() == true ? "true":"false");

                HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/room", apiHeaders, data, sportsTalkConfig.getApiCallback());
                httpClient.setAction("createRoom");
                httpClient.execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void listParticipants(Room room, String cursor, int maxResults) {
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/room/" + room.getId()+"/participants?cursor=" + cursor + "&maxresults=" + maxResults, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("listParticipants");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void joinRoom(User user, RoomResult room) {
        StringBuilder sb = new StringBuilder();
        sb.append(sportsTalkConfig.getEndpoint()).append("/room/").append(room.getId()).append("/join");
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sb.toString(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("joinRoom");
        httpClient.execute();
    }

}
