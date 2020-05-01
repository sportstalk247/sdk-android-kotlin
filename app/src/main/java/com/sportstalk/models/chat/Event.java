package com.sportstalk.models.chat;

import com.sportstalk.models.common.Kind;
import com.sportstalk.models.common.User;

import java.util.List;

/**
 * Generic Class that wraps chat events.
 * @param <T>
 */
public class Event<T> {
    private Kind kind;
    private String id;
    private String roomId;
    private int added;
    private String body;
    private String userId;
    private EventType eventType;
    private User user;
    private String customType;
    private String customId;
    private T customPayload;
    private T replyTo;
    private List<Event> reactions;

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public T getCustomPayload() {
        return customPayload;
    }

    public void setCustomPayload(T customPayload) {
        this.customPayload = customPayload;
    }

    public T getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(T replyTo) {
        this.replyTo = replyTo;
    }

    public List<Event> getReactions() {
        return reactions;
    }

    public void setReactions(List<Event> reactions) {
        this.reactions = reactions;
    }
}
