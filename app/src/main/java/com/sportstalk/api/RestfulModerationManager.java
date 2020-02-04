package com.sportstalk.api;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sportstalk.SportsTalkConfig;
import com.sportstalk.Utils;
import com.sportstalk.rest.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class RestfulModerationManager implements IModerationManager {

    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulModerationManager(SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    private void setConfig(final SportsTalkConfig sportsTalkConfig) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getModerationQueueEvents() {
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", this.sportsTalkConfig.getEndpoint() + "/moderation/queue", apiHeaders, null, sportsTalkConfig.getEventHandler());
        httpClient.setAction("moderationEvent");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void removeEvent(String eventId) {
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint() + "/moderation/applydecisiontoevent/" + eventId, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("removeEvent");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void approveEvent(String eventId) {
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint() + "/moderation/applydecisiontoevent/" + eventId, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("removeEvent");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void reportEvent(String eventId) {
        Map<String, String>data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", this.sportsTalkConfig.getEndpoint() + "/moderation/applydecisiontoevent/" + eventId, apiHeaders, data, sportsTalkConfig.getEventHandler());
        httpClient.setAction("removeEvent");
        httpClient.execute();
    }

}
