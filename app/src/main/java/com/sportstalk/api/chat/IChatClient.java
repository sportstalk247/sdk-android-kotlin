package com.sportstalk.api.chat;

import com.sportstalk.models.chat.EventHandlerConfig;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.chat.RoomUserResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.common.User;

import java.util.List;

public interface IChatClient extends ITalkClient {

    void setEventHandlers(EventHandlerConfig eventHandlerConfig);

    void startTalk();

    void stopTalk();

    ApiResult report(EventResult eventResult, ReportType reportType);

    List<Room> listRooms();

    RoomUserResult joinRoom(RoomResult room);

    Room getCurrentRoom();

    List<User> listParticipants(String cursor, int maxResults);
}
