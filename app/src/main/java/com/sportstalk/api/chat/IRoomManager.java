package com.sportstalk.api.chat;

import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.User;

import java.util.List;

public interface IRoomManager {
    List<Room> listRooms();

    ApiResult deleteRoom(String id);

    RoomResult createRoom(Room room, String userId);

    List<User> listParticipants(Room room, String cursor, int maxResults);

    RoomUserResult joinRoom(User user, RoomResult room);

    RoomUserResult exitRoom(User user, Room room);

    EventResult listUserMessages(User user, Room room, String cursor, int limit);
}
