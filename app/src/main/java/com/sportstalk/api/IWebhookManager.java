package com.sportstalk.api;

import com.sportstalk.AdvertisementOptions;

public interface IWebhookManager {
    public void ListWebhooks();
    public void createWebhook(Webhook webhook);
    public void updateWebhook(Webhook webhook);
    public void deleteWebhook(Webhook webhook);
}
