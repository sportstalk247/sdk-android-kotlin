package com.sportstalk.api.chat;

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

    /** this method is used to send chat message */
    ApiResult sendCommand(User user, Room room, String command, CommandOptions options);

    /** this method is used to send a reply to a message */
    ApiResult sendReply(User user, String message, String replyTo, CommandOptions commandOptions);

    /** this method is used to send a reaction */
    ApiResult sendReaction(User user, Room room, Reaction reaction, String reactionToMessageId, CommandOptions commandOptions);

    /** this is method is used to send an ad link to the users*/
    ApiResult sendAdvertisement(User user, Room room, AdvertisementOptions advertisementOptions);

    /** this methdd is used to send a goal image */
    ApiResult sendGoal(User user, Room room, String img, String message, GoalOptions goalOptions);

    EventHandler getEventHandlers();

    void setEventHandlers(EventHandler eventHandlers);

    /* this method is used to report event */
    ApiResult reportEvent(EventResult eventResult, ReportReason reportReason);
}
