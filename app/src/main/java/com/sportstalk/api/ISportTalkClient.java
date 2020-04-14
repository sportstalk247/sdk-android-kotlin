package com.sportstalk.api;

import com.sportstalk.EventHandler;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.UserResult;
import com.sportstalk.models.chat.Room;

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
