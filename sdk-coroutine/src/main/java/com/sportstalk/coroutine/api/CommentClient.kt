package com.sportstalk.coroutine.api

import com.sportstalk.coroutine.service.CommentModerationService
import com.sportstalk.coroutine.service.CommentService
import com.sportstalk.datamodels.comment.Conversation

interface CommentClient: CommentService, CommentModerationService {
    var currentConversation: Conversation?
}