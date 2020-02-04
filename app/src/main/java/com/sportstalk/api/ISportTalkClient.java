package com.sportstalk.api;

import com.sportstalk.AdvertisementOptions;
import com.sportstalk.EventHandler;
import com.sportstalk.RoomResult;
import com.sportstalk.UserResult;

import java.util.List;

public interface ISportTalkClient {
    public void setEventHandler(EventHandler eventHandler);
    public void startTalk();
    public void stopTalk();
    public List<RoomResult> listRooms();
    public RoomUserResult joinRoom(RoomResult room);
    public Room getCurrentRoom();
    public List<UserResult> listParticipants(String cursor, int maxResults);
}
