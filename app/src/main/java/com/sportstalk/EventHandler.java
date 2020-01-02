package com.sportstalk;

/**
 * This is a callback is used for appropriate events such as pollStart,
 * reaction, admin command, purge etc
 */
public interface EventHandler {
    public void onEventStart(Event event);
    public void onReaction(Event event);
    public void onAdminCommand(Event event);
    public void onPurge(Event event);
    public void onSpeech(Event event);
    public void onChat(Event event);
}