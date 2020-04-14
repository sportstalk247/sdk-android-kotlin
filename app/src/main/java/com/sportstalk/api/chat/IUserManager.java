package com.sportstalk.api.chat;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

public interface IUserManager {
    public ApiResult listUserMessages(User user, String cursor, int limit);
    public UserResult setBanStatus(User user, boolean isBanned);
    public UserResult createOrUpdateUser(User user);
}
