package com.sportstalk.impl.common.rest;

import java.util.HashMap;
import java.util.Map;

import static com.sportstalk.impl.Constants.CONTENT_TYPE;
import static com.sportstalk.impl.Constants.X_API_TOKEN;

public class Utils {
    private static final String MIME_JSON = "application/json";

    public static Map<String, String> getApiHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MIME_JSON);
        headers.put("Accept", MIME_JSON);
        if (null != apiKey && !apiKey.isEmpty()) headers.put(X_API_TOKEN, apiKey);
        return headers;
    }
}
