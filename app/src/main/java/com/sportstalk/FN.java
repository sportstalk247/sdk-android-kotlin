package com.sportstalk;

import java.util.HashMap;
import java.util.Map;

public class FN {
    private final String FORM_ENCODED = "application/json";

    public Map<String, String> getApiHeaders(String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", FORM_ENCODED);
        headers.put("Content-Type", FORM_ENCODED);
        if (null != apiKey && !apiKey.isEmpty()) headers.put("x-api-token", apiKey);
        return headers;
    }
}
