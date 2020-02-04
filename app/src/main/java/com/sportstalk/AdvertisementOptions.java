package com.sportstalk;

import java.util.List;

public class AdvertisementOptions {
    private String img;
    private String link;
    private String id;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class Room {
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

    public static class RoomUserResult {
        public UserResult getUserResult() {
            return userResult;
        }

        public void setUserResult(UserResult userResult) {
            this.userResult = userResult;
        }

        public RoomResult getRoomResult() {
            return roomResult;
        }

        public void setRoomResult(RoomResult roomResult) {
            this.roomResult = roomResult;
        }

        private UserResult userResult;
        private RoomResult roomResult;
    }

    public static class Webhook {

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
}
