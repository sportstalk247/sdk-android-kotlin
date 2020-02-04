package com.sportstalk.api;

import com.sportstalk.AdvertisementOptions;
import com.sportstalk.RoomResult;
import com.sportstalk.User;

public interface IRoomManager {
    public void listRooms();
    public void deleteRoom(String id);
    public void createRoom(Room room, String userId);
    public void listParticipants(Room room, String cursor, int maxResults);
    public void joinRoom(User user, RoomResult room);
}
