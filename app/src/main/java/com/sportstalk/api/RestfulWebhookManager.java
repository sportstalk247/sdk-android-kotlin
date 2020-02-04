package com.sportstalk.api;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.sportstalk.AdvertisementOptions;
import com.sportstalk.SportsTalkConfig;
import com.sportstalk.Utils;
import com.sportstalk.rest.HttpClient;

import java.util.HashMap;
import java.util.Map;

public class RestfulWebhookManager implements IWebhookManager {


    private SportsTalkConfig sportsTalkConfig;
    private Map<String, String> apiHeaders;

    public RestfulWebhookManager(SportsTalkConfig sportsTalkConfig) {
        setConfig(sportsTalkConfig);
    }

    private void setConfig(final SportsTalkConfig sportsTalkConfig) {
        this.sportsTalkConfig = sportsTalkConfig;
        this.apiHeaders = new Utils().getApiHeaders(sportsTalkConfig.getApiKey());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void ListWebhooks() {
        Map<String, String> data = new HashMap<>();
        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "GET", sportsTalkConfig.getEndpoint() + "/webhook", apiHeaders, data, sportsTalkConfig.getApiCallback());
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

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/webhook", apiHeaders, data, sportsTalkConfig.getApiCallback());
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

        HttpClient httpClient = new HttpClient(sportsTalkConfig.getContext(), "POST", sportsTalkConfig.getEndpoint() + "/webhook", apiHeaders, data, sportsTalkConfig.getApiCallback());
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

}
