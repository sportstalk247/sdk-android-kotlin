package com.sportstalk.api.chat;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.impl.Webhook;

public interface IWebhookManager extends ISportsTalkConfigurable {
    public void ListWebhooks();
    public void createWebhook(Webhook webhook);
    public void updateWebhook(Webhook webhook);
    public void deleteWebhook(Webhook webhook);
}