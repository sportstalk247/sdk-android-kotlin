package com.sportstalk.api.chat;

import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.User;

import java.util.List;

/*
  This class is responsible for managing room activities
 */
public interface IRoomManager {
    /** list all rooms **/
    List<Room> listRooms();

    /** delete a room **/
    ApiResult deleteRoom(String id);

    /** create a room */
    RoomResult createRoom(Room room, String userId);

    /** list all users joined in a room */
    List<User> listParticipants(Room room, String cursor, int maxResults);

    /** join a room */
    RoomUserResult joinRoom(User user, RoomResult room);

    /** exit from a room */
    RoomUserResult exitRoom(User user, Room room);

    /** list all user messages **/
    EventResult listUserMessages(User user, Room room, String cursor, int limit);
}
