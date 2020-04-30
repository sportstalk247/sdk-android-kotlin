package com.sportstalk.models.common;

import android.content.Context;

import com.sportstalk.APICallback;
import com.sportstalk.api.chat.EventHandler;

public class SportsTalkConfig {

    private String appId;
    private String apiKey;
    private String userId;
    private String endpoint;
    private Context context;
    private EventHandler eventHandler;
    private boolean isPushEnabled;
    private APICallback apiCallback;
    private User user;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public boolean isPushEnabled() {
        return isPushEnabled;
    }

    public void setPushEnabled(boolean pushEnabled) {
        isPushEnabled = pushEnabled;
    }

    public APICallback getApiCallback() {
        return apiCallback;
    }

    public void setApiCallback(APICallback apiCallback) {
        this.apiCallback = apiCallback;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(final Context context) {
        this.context = context;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

}
