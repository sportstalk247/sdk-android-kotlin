package com.sportstalk.api;

import com.sportstalk.AdvertisementOptions;

public interface IWebhookManager {
    public void ListWebhooks();
    public void createWebhook(AdvertisementOptions.Webhook webhook);
    public void updateWebhook(AdvertisementOptions.Webhook webhook);
    public void deleteWebhook(AdvertisementOptions.Webhook webhook);
}
