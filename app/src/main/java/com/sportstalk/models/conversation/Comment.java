package com.sportstalk.models.conversation;

import com.sportstalk.models.common.User;

import java.util.List;

/**
 * Comment Model.  Holds data from the server describing a comment in a conversation
 */
public class Comment extends User {
    private String id;
    private String body;
    private String replyTo;

    private int voteScore;
    private int likeCount;
    private List<String> tags;

    /**
     * Gets the vote score.  Usually zero but may go up or down as users vote.
     * @return
     */
    public int getVoteScore() {
        return voteScore;
    }

    /**
     * Sets a votescore on a comment. This does NOT affect the true score on the server.
     * Setting this will have no effect except on the local client display.
     * @param voteScore
     */
    public void setVoteScore(int voteScore) {
        this.voteScore = voteScore;
    }

    /**
     * Get the number of likes
     * @return
     */
    public int getLikeCount() {
        return likeCount;
    }


    /**
     * Set the number of likes on a comment.  This does NOT affect the score on the server.
     * Setting this will have no effect except on the local client display.
     * @param likeCount
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Get the list of hashtags that are in the comment.
     * @return
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Set the hashtags on the comment.  Will not write to the server.  Truly updating the hashtags requires editing the comment.
     * However, there may be reasons to adjust this on the client side, in which case you should use this method purely to power
     * a custom display.
     * @param tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Get the comment ID
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Set the comment id.  Under most circumstances you should NOT do this, as it could break functionality.
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the comment body.
     * @return
     */
    public String getBody() {
        return body;
    }
    /**
     * Set the comment body.
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }
    /**
     * Get the reply to.  May be (and often is) null or an empty string.
     * @return
     */
    public String getReplyTo() {
        return replyTo;
    }
    /**
     * Set the replyto parameter. Will not update the server unless use use the update method on the CommentingClient or CommentService.
     * @param replyTo
     */
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
