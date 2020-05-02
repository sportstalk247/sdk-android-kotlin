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

/**
 * This is the primary interface for SportsTalk Chat Clients.
 * For most chat implementations, you will be primarily using the IChatClient interface.
 */
public interface IChatClient extends ITalkClient {

     /**
     * Provide callbacks to power your UI when new events are received
     * @param eventHandlerConfig
     */
    void setEventHandlers(EventHandlerConfig eventHandlerConfig);

    /**
     * Begin receiving new events from the Sportstalk Server.
     */
    void startTalk();

     /**
     * End the chat session.  New events will not be retrieved. However, if the underlying chat implementation has an event queue that still has items remaining,
     * those items may continue to be emitted until that queue is drained.  This is implementation specific.
     */
    void stopTalk();

    /**
     * Report a chat event for violating community standards
     * @param eventResult
     * @param reportType
     * @return
     */
    ApiResult report(EventResult eventResult, ReportType reportType);

      /**
     * List available chat rooms.
     * @return
     */
    List<Room> listRooms();

      /**
     * Add a user to a room. Necessary before being able to contribute to the room.
     * You do not need to join a room to read the chat, only to participate.
     * @param room
     * @return
     */
    RoomUserResult joinRoom(RoomResult room);

        /**
     * Get the currently set room, if any.  May return null if no room has been set or the room was deleted.
     * @return
     */
    Room getCurrentRoom();

     /**
     * List participants in a room.  Requires you to have joined a room first.
     * @param cursor
     * @param maxResults
     * @return
     */
    List<User> listParticipants(String cursor, int maxResults);
}
