package com.sportstalk.api;

import com.sportstalk.AdvertisementOptions;
import com.sportstalk.CommandOptions;
import com.sportstalk.EventHandler;
import com.sportstalk.GoalOptions;
import com.sportstalk.Reaction;
import com.sportstalk.User;

public interface IEventManager {
    public void startTalk();
    public void stopTalk();
    public void setCurrentRoom(Room room);
    public void setEventHandlers(EventHandler eventHandlers);
    public Room getCurrentRoom();
    public void getUpdates();
    public void sendCommand(User user, Room room, String command, CommandOptions options);
    public void sendReply(User user, String message, String replyTo, CommandOptions commandOptions );
    public void sendReaction(User user, Room room, Reaction reaction, String reactionToMessageId, CommandOptions commandOptions);
    public void sendAdvertisement(User user, Room room, AdvertisementOptions advertisementOptions);
    public void sendGoal(User user, Room room, String img, String message, GoalOptions goalOptions);
    public EventHandler getEventHandlers();
}
