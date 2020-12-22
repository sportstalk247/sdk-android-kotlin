package com.sportstalk.api

import com.sportstalk.api.service.CommentModerationService
import com.sportstalk.api.service.CommentService
import com.sportstalk.datamodels.comment.Conversation

interface CommentClient: CommentService, CommentModerationService {
    var currentConversation: Conversation?
}