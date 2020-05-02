package com.sportstalk.api.chat;

import com.sportstalk.models.chat.deprecated.AdvertisementOptions;
import com.sportstalk.models.chat.deprecated.CommandOptions;
import com.sportstalk.models.chat.deprecated.EventResult;
import com.sportstalk.models.chat.deprecated.GoalOptions;
import com.sportstalk.models.chat.deprecated.Room;
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
