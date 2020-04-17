package com.sportstalk.api.chat;

import com.sportstalk.api.RoomUserResult;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.User;

import java.util.List;

public interface IRoomService {
    public List<Room> listRooms();
    public ApiResult deleteRoom(String id);
    public RoomResult createRoom(Room room, String userId);
    public List<User> listParticipants(Room room, String cursor, int maxResults);
    public RoomUserResult joinRoom(User user, RoomResult room);
    public RoomUserResult exitRoom(User user, Room room);
    public EventResult listUserMessages(User user, Room room, String cursor, int limit);
}
