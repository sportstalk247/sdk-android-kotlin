package com.sportstalk.api.conversation;

import com.sportstalk.models.common.ApiResult;
import com.sportstalk.models.conversation.Comment;

import java.util.List;

/**
 * This is the interface for moderating comments.
 * When comments are flagged for review, they are put in the moderation queue.  You can retrieve this queue using
 * `converationModerationManager.getModerationQueue()`
 * For each comment in the queue, you can approve it with `approveComment(comment)` or reject with `rejectComment(comment)`
 */
public interface IConversationModerationService {

    /**
     * Retrieves the moderation queue
     * @return
     */
    List<Comment> getModerationQueue();

    /**
     * Rejects a comment. It will not show in the conversation
     * @param comment
     * @return
     */
    ApiResult rejectComment(Comment comment);

    /**
     * Approves a comment.
     * @param comment
     * @return
     */
    ApiResult approveComment(Comment comment);
}
