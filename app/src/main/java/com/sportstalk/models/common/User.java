package com.sportstalk.models.common;

public class User {

    private Kind kind;
    private String userId;
    private String handle;
    private String handleLowerCase;
    private String displayName;
    private String pictureUrl;
    private String profileUrl;

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getHandleLowerCase() {
        return handleLowerCase;
    }

    public void setHandleLowerCase(String handleLowerCase) {
        this.handleLowerCase = handleLowerCase;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isBanned() {
        return IsBanned;
    }

    public void setBanned(boolean banned) {
        IsBanned = banned;
    }

    private boolean IsBanned;
}
