package com.sportstalk.api.common;

import com.sportstalk.models.common.SportsTalkConfig;

/**
 * All objects that need an API token or AppID (which is most service objects) should
 * implement this interface.
 */
public interface ISportsTalkConfigurable {
     /**
     * Sets the configuration.
     * @param config
     */
    void setConfig(SportsTalkConfig config);
}
