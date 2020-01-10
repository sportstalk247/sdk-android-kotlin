package com.sportstalk;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * This interface provides support for callback when a response received
 * from the REST endpoint.
 */
public interface APICallback{
    public void execute(ApiResult<JSONObject> jsonObject, String action);
    public void error(ApiResult<JSONObject> jsonObject, String action);
}
