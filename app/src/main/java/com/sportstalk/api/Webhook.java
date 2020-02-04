package com.sportstalk.api;

import com.sportstalk.EventResult;
import com.sportstalk.Kind;
import com.sportstalk.WebhookType;

import java.util.List;

public class Webhook {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public WebhookType getType() {
        return type;
    }

    public void setType(WebhookType type) {
        this.type = type;
    }

    public List<EventResult> getEvents() {
        return events;
    }

    public void setEvents(List<EventResult> events) {
        this.events = events;
    }

    private Kind kind;
    private String label;
    private String url;
    private boolean enabled;
    private WebhookType type;
    private List<EventResult> events;
}
