package com.sportstalk;

import com.sportstalk.models.common.ApiResult;

import org.json.JSONObject;

/**
 * This interface provides support for callback when a response received
 * from the REST endpoint.
 */
public interface APICallback {
    void execute(ApiResult<JSONObject> jsonObject, String action);

    void error(ApiResult<JSONObject> jsonObject, String action);
}
