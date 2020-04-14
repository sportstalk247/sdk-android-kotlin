package com.sportstalk.models.chat;

public class Room {

    private String id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModeration() {
        return moderation;
    }

    public void setModeration(String moderation) {
        this.moderation = moderation;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isEnableAddress() {
        return enableAddress;
    }

    public void setEnableAddress(boolean enableAddress) {
        this.enableAddress = enableAddress;
    }

    public boolean isRoomIsOpen() {
        return roomIsOpen;
    }

    public void setRoomIsOpen(boolean roomIsOpen) {
        this.roomIsOpen = roomIsOpen;
    }

    public boolean isEnableEnterandExit() {
        return enableEnterandExit;
    }

    public void setEnableEnterandExit(boolean enableEnterandExit) {
        this.enableEnterandExit = enableEnterandExit;
    }

    private String description;
    private String moderation;
    private String slug;
    private boolean enableAddress;
    private boolean roomIsOpen;
    private boolean enableEnterandExit;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
