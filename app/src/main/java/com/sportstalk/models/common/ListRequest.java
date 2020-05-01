package com.sportstalk.models.common;

/**
 * A class that describes common listing behavior used in different Sportstalk APIs
 */
public class ListRequest {

    private String cursor;
    private int limit;

    /**
     * The cursor is a start or end point used for searching.  Depending on the rest of your request, results will be items coming before or after this point.
     * @return cursor
     */
    public String getCursor() {
        return cursor;
    }

    /**
     * The cursor is a start or end point used for searching.  Depending on the rest of your request, results will be items coming before or after this point.
     */
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    /**
     * Get the max number of results.  Most APIs have a default limit of 200 which cannot be exceeded.  View the REST api documentation in postman for details
     * @return the max number of responses
     */
    public int getLimit() {
        return limit;
    }
    /**
     * Set the max number of results.  Most APIs have a default limit of 200 which cannot be exceeded.  View the REST api documentation in postman for details
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
