package com.sportstalk.api.chat;

import com.sportstalk.api.common.ISportsTalkConfigurable;
import com.sportstalk.api.impl.Webhook;

public interface IWebhookManager extends ISportsTalkConfigurable {
    void ListWebhooks();

    void createWebhook(Webhook webhook);

    void updateWebhook(Webhook webhook);

    void deleteWebhook(Webhook webhook);
}