package com.sportstalk.api.chat;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

public interface IUserManager {
    ApiResult listUserMessages(User user, String cursor, int limit);

    UserResult setBanStatus(User user, boolean isBanned);

    UserResult createOrUpdateUser(User user);
}
