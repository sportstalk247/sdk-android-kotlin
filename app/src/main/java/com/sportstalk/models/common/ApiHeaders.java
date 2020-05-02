package com.sportstalk.models.common;

public class ApiHeaders {

    private String ContentType;
    private String xApiToken;

    public String getContentType() {
        return ContentType;
    }

    public void setContentType(String contentType) {
        ContentType = contentType;
    }

    public String getxApiToken() {
        return xApiToken;
    }

    public void setxApiToken(String xApiToken) {
        this.xApiToken = xApiToken;
    }
}
