package com.sportstalk;

import java.util.HashMap;
import java.util.Map;

import static com.sportstalk.Constants.CONTENT_TYPE;
import static com.sportstalk.Constants.X_API_TOKEN;

public class Utils {
    private final String FORM_ENCODED = "application/json";

    public Map<String, String> getApiHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, FORM_ENCODED);
        headers.put("Accept", FORM_ENCODED);
        if (null != apiKey && !apiKey.isEmpty()) headers.put(X_API_TOKEN, apiKey);
        return headers;
    }
}
