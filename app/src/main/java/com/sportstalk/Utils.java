package com.sportstalk;

import java.util.HashMap;
import java.util.Map;

import static com.sportstalk.Constants.CONTENT_TYPE;
import static com.sportstalk.Constants.X_API_TOKEN;

public class Utils {
    private final String FORM_ENCODED = "application/json";
    public Map<String, String> getApiHeaders(String apiKey) {
        Map<String, String>headers = new HashMap<>();
        headers.put(CONTENT_TYPE, FORM_ENCODED);
        headers.put("Accept", FORM_ENCODED);
   //     headers.put("User-Agent", "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:74.0) Gecko/20100101 Firefox/74.0");
        if(null != apiKey && !apiKey.isEmpty()) headers.put(X_API_TOKEN, apiKey);
        return headers;
    }
}
