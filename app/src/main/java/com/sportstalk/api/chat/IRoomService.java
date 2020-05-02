package com.sportstalk.api.chat;

import com.sportstalk.models.chat.deprecated.EventResult;
import com.sportstalk.models.chat.deprecated.Room;
import com.sportstalk.models.chat.deprecated.RoomResult;
import com.sportstalk.models.chat.deprecated.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.User;

import java.util.List;

public interface IRoomService {
    List<Room> listRooms();

    ApiResult deleteRoom(String id);

    RoomResult createRoom(Room room, String userId);

    List<User> listParticipants(Room room, String cursor, int maxResults);

    RoomUserResult joinRoom(User user, RoomResult room);

    RoomUserResult exitRoom(User user, Room room);

    EventResult listUserMessages(User user, Room room, String cursor, int limit);
}
