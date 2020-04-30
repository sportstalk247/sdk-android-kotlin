package com.sportstalk.api.chat;

import com.sportstalk.models.chat.Event;
import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.common.ApiResult;

import java.util.List;

/**
 * This is a callback is used for appropriate events such as pollStart,
 * reaction, admin command, purge etc
 */
public interface EventHandler {
    void onEventStart(Event event);

    void onReaction(Event event);

    void onAdminCommand(Event event);

    void onReply(Event event);

    void onPurge(Event event);

    void onSpeech(Event event);

    void onChat(Event event);

    void onNetworkResponse(List<EventResult> list);

    void onHelp(ApiResult apiResult);

    void onGoalCommand(EventResult eventResult);
}