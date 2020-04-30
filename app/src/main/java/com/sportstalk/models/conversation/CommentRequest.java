package com.sportstalk.models.conversation;

public class CommentRequest {

    private String cursor;
    private boolean includeChildren;
    private CommentSortMethod sortMethod;
    private CommentSortDirection direction;

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    public boolean isIncludeChildren() {
        return includeChildren;
    }

    public void setIncludeChildren(boolean includeChildren) {
        this.includeChildren = includeChildren;
    }

    public CommentSortMethod getSortMethod() {
        return sortMethod;
    }

    public void setSortMethod(CommentSortMethod sortMethod) {
        this.sortMethod = sortMethod;
    }

    public CommentSortDirection getDirection() {
        return direction;
    }

    public void setDirection(CommentSortDirection direction) {
        this.direction = direction;
    }


}
