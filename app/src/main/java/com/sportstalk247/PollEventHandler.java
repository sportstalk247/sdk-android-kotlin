package com.sportstalk247;

/**
 * This is a callback is used for appropriate events such as pollStart,
 * reaction, admin command, purge etc
 */
public interface PollEventHandler {

    public void onPollStart(Event event);

    public void onReaction(Event event);

    public void onAdminCommand(Event event);

    public void onPurge(Event event);

}
