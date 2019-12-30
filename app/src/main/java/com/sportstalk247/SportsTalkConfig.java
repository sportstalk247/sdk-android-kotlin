package com.sportstalk247;

import android.content.Context;

public class SportsTalkConfig {

    private String appId;

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

    private String apiKey;
    private String userId;
    private String endpoint;
    private Context context;
    private PollEventHandler pollEventHandler;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    public Context getContext() {
        return context;
    }

    public void setContext(final Context context) {
        this.context = context;
    }

    public void setPollEventHandler(PollEventHandler pollEventHandler) {
        this.pollEventHandler = pollEventHandler;
    }

    public PollEventHandler getPollEventHandler() {
        return pollEventHandler;
    }

}
