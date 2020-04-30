package com.sportstalk.impl;

import java.util.HashMap;
import java.util.Map;

import static com.sportstalk.impl.Constants.CONTENT_TYPE;
import static com.sportstalk.impl.Constants.X_API_TOKEN;

public class Utils {
    private final String MIME_JSON = "application/json";

    public Map<String, String> getApiHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MIME_JSON);
        headers.put("Accept", MIME_JSON);
        if (null != apiKey && !apiKey.isEmpty()) headers.put(X_API_TOKEN, apiKey);
        return headers;
    }
}
