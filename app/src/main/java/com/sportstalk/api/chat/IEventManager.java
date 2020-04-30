package com.sportstalk.api.chat;

import com.sportstalk.EventHandler;
import com.sportstalk.models.chat.AdvertisementOptions;
import com.sportstalk.models.chat.CommandOptions;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.GoalOptions;
import com.sportstalk.models.chat.Room;
import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.Reaction;
import com.sportstalk.models.common.ReportReason;
import com.sportstalk.models.common.User;

public interface IEventManager {
    void startTalk();

    void stopTalk();

    Room getCurrentRoom();

    void setCurrentRoom(Room room);

    void getUpdates();

    ApiResult sendCommand(User user, Room room, String command, CommandOptions options);

    ApiResult sendReply(User user, String message, String replyTo, CommandOptions commandOptions);

    ApiResult sendReaction(User user, Room room, Reaction reaction, String reactionToMessageId, CommandOptions commandOptions);

    ApiResult sendAdvertisement(User user, Room room, AdvertisementOptions advertisementOptions);

    ApiResult sendGoal(User user, Room room, String img, String message, GoalOptions goalOptions);

    EventHandler getEventHandlers();

    void setEventHandlers(EventHandler eventHandlers);

    ApiResult reportEvent(EventResult eventResult, ReportReason reportReason);
}
