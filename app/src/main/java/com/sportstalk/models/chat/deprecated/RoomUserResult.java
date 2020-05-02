package com.sportstalk.models.chat.deprecated;

import com.sportstalk.models.common.UserResult;

public class RoomUserResult {
    private UserResult userResult;
    private RoomResult roomResult;

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

}
