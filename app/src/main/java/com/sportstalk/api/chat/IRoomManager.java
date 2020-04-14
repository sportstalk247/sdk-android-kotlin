package com.sportstalk.api.chat;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.api.RoomUserResult;

import java.util.List;

public interface IRoomManager {
    public List<Room> listRooms();
    public ApiResult deleteRoom(String id);
    public RoomResult createRoom(Room room, String userId);
    public List<User> listParticipants(Room room, String cursor, int maxResults);
    public RoomUserResult joinRoom(User user, RoomResult room);
    public RoomUserResult exitRoom(User user, Room room);
}
