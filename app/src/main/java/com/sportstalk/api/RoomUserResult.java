package com.sportstalk.api;

import com.sportstalk.RoomResult;
import com.sportstalk.UserResult;

public class RoomUserResult {
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
