package com.sportstalk.impl.common.rest;

import android.os.Build;

import com.sportstalk.api.chat.IWebhookManager;
import com.sportstalk.models.Webhook;
import com.sportstalk.models.common.SportsTalkConfig;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;

public class RestfulWebhookManager implements IWebhookManager {

    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulWebhookManager(SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void ListWebhooks() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/webhook/hooks", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("listWebhooks");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void createWebhook(Webhook webhook) {

        Map<String, String> data = new HashMap<>();
        data.put("label", "");
        data.put("url", "");
        data.put("enabled", "false");
        data.put("type", "postpublish");

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/webhook/hooks", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("createWebhooks");
        httpClient.execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void updateWebhook(Webhook webhook) {
        Map<String, String> data = new HashMap<>();
        data.put("label", "");
        data.put("url", "");
        data.put("enabled", "false");
        data.put("type", "postpublish");
        data.put("events", "\"speech\", \"custom\", \"reply\", \"reaction\", \"action\", \"enter\", \"exit\", \"roomopened\", \"roomclosed\", \"purge\"");

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "PUT", sportsTalkConfig.getEndpoint() + "/webhook/hooks", apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("updateWebhooks");
        httpClient.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void deleteWebhook(Webhook webhook) {
        Map<String, String> data = new HashMap<>();

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "DELETE", sportsTalkConfig.getEndpoint() + "/webhook/" + webhook.getId(), apiHeaders, data, sportsTalkConfig.getApiCallback());
        httpClient.setAction("deleteWebhooks");
        httpClient.execute();
    }

    @Override
    public void setConfig(final SportsTalkConfig config) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = Utils.getApiHeaders(sportsTalkConfig.getApiKey());
    }
}
