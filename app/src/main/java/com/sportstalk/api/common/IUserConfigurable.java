package com.sportstalk.api.common;

import com.sportstalk.models.common.User;

/**
 * Any service that requires a persistent user state or 'current user' should implement this interface.
 */
public interface IUserConfigurable {
    /**
     * Sets the current user.
     * @param user
     */
    void setUser(User user);
}
