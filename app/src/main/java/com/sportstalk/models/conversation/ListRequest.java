package com.sportstalk.models.conversation;

public class ListRequest {

    private String cursor;
    private int limit;

    /** gets cursor name **/
    public String getCursor() {
        return cursor;
    }

    /** sets cursor **/
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    /** get limit **/
    public int getLimit() {
        return limit;
    }

    /** sets limit **/
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
