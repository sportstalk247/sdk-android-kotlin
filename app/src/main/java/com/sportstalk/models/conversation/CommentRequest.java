package com.sportstalk.models.conversation;
public class CommentRequest {

    private String cursor;
    private boolean includeChildren;
    private CommentSortMethod sortMethod;
    private CommentSortDirection direction;

    public String getCursor() {
        return cursor;
    }

    /**
     * Set the cursor (commentid).  Comments after this cursor will be retrieved.
     * @param cursor
     */
    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    /**
     * If true will retrieve child comments
     * @return
     */
    public boolean isIncludeChildren() {
        return includeChildren;
    }

    /**
     * If true will retrieve child comments
     * @return
     */
    public void setIncludeChildren(boolean includeChildren) {
        this.includeChildren = includeChildren;
    }

    /**
     * Determines how comments will be sorted by the server
     * @return
     */
    public CommentSortMethod getSortMethod() {
        return sortMethod;
    }

    /**
     * Determines how comments will be sorted by the server
     * @return
     */
    public void setSortMethod(CommentSortMethod sortMethod) {
        this.sortMethod = sortMethod;
    }

    /**
     * Determines the direction of the sort on the server
     * @return
     */
    public CommentSortDirection getDirection() {
        return direction;
    }

    /**
     * Determines the direction of the sort on the server
     * @return
     */
    public void setDirection(CommentSortDirection direction) {
        this.direction = direction;
    }


}
