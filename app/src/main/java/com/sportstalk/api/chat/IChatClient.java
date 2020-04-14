package com.sportstalk.api.chat;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.RoomResult;
import com.sportstalk.models.common.User;
import com.sportstalk.models.chat.EventHandlerConfig;
import com.sportstalk.models.common.ReportType;
import com.sportstalk.models.chat.Room;
import com.sportstalk.api.RoomUserResult;

import java.util.List;

public interface IChatClient extends ITalkClient {

    public void setEventHandlers(EventHandlerConfig eventHandlerConfig);
    public void startTalk();
    public void stopTalk();
    public ApiResult report(EventResult eventResult, ReportType reportType);
    public List<Room> listRooms();
    public RoomUserResult joinRoom(RoomResult room);
    public Room getCurrentRoom();
    public List<User> listParticipants(String cursor, int maxResults);
}
