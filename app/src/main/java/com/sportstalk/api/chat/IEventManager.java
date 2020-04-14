package com.sportstalk.api.chat;

import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.EventHandler;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportReason;
import com.sportstalk.models.common.User;
import com.sportstalk.models.chat.Room;

public interface IEventManager {
    public void startTalk();
    public void stopTalk();
    public void setCurrentRoom(Room room);
    public void setEventHandlers(EventHandler eventHandlers);
    public Room getCurrentRoom();
    public void getUpdates();
    public ApiResult sendCommand(User user, Room room, String command, CommandOptions options);
    public ApiResult sendReply(User user, String message, String replyTo, CommandOptions commandOptions );
    public ApiResult sendReaction(User user, Room room, Reaction reaction, String reactionToMessageId, CommandOptions commandOptions);
    public ApiResult sendAdvertisement(User user, Room room, AdvertisementOptions advertisementOptions);
    public ApiResult sendGoal(User user, Room room, String img, String message, GoalOptions goalOptions);
    public EventHandler getEventHandlers();
    public ApiResult reportEvent(EventResult eventResult, ReportReason reportReason);
}
