package com.sportstalk.models.common;

import com.sportstalk.models.chat.EventResult;
import com.sportstalk.models.chat.WebhookType;
import com.sportstalk.models.common.Kind;

import java.util.List;

/**
 * For most customers, webhooks should primarily be managed in the Sportstalk dashboard.
 * However, you can manage them programmatically as well.
 */
public class Webhook {
    private String id;
    private Kind kind;
    private String label;
    private String url;
    private boolean enabled;
    private WebhookType type;
    private List<EventResult> events;

    /**
     * The Sportstalk ID of the webhook
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Get the kind, a server marker for the object type.
     * @return
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Get the label for a webhook. The label is a short human readable description of what the webhook is for.
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label of a webhook.
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Get the webhook URL
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Set the webhook url.  Changing this or other values will not update them on the server.
     * To update settings on the server, use the WebhookService
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * True if the webhook is active.
     * To update settings on the server, use the WebhookService
     * @return true if webhook is active
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * True if the webhook is active.
     * To update settings on the server, use the WebhookService
     * @return true if webhook is active
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether or not this is a pre or post moderation hook.
     * @return true if webhook is active
     */
    public WebhookType getType() {
        return type;
    }

    /**
     * Sets whether or not this is a pre or post moderation hook.
     * To update settings on the server, use the WebhookService
     * @return true if webhook is active
     */
    public void setType(WebhookType type) {
        this.type = type;
    }

    /**
     * Get the events that trigger the hook
     * @return event list
     */
    public List<EventResult> getEvents() {
        return events;
    }

    /**
     * Sets the event types that will trigger the hook.  Pre and post hooks have different allowed types.
     * If you set invalid types for the hook, then updating the hook will result in an error from the server.
     * To update settings on the server, use the WebhookService.
     */
    public void setEvents(List<EventResult> events) {
        this.events = events;
    }
}
