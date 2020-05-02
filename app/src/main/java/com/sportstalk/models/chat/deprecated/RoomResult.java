package com.sportstalk.models.chat.deprecated;

import com.sportstalk.models.common.Kind;

public class RoomResult extends Room {

    private Kind kind;
    private String ownerId;
    private int inRoom;
    private String whenModified;
    private int maxReports;

    public Kind getKind() {
        return kind;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getInRoom() {
        return inRoom;
    }

    public void setInRoom(int inRoom) {
        this.inRoom = inRoom;
    }

    public String getWhenModified() {
        return whenModified;
    }

    public void setWhenModified(String whenModified) {
        this.whenModified = whenModified;
    }

    public int getMaxReports() {
        return maxReports;
    }

    public void setMaxReports(int maxReports) {
        this.maxReports = maxReports;
    }
}
