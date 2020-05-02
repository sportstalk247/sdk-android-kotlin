package com.sportstalk.api.chat;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.common.SearchType;
import com.sportstalk.models.common.User;
import com.sportstalk.models.common.UserResult;

import java.util.List;

public interface IUserManager {
    ApiResult listUserMessages(User user, String cursor, int limit);

    UserResult setBanStatus(User user, boolean isBanned);

    UserResult createOrUpdateUser(User user);

    List<User> listUsers(int limit, String cursor);

    UserResult deleteUser(User user);

    List<User> searchUsers(SearchType searchType, int limit);


}
