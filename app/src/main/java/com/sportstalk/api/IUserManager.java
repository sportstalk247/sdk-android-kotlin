package com.sportstalk.api;

import com.sportstalk.EventResult;
import com.sportstalk.User;

import java.util.List;

public interface IUserManager {
    public void listUserMessages(User user, String cursor, int limit);
    public void setBanStatus(User user, boolean isBanned);
    public void createOrUpdateUser(User user);
}
