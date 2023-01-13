package com.sportstalk.reactive.rx2.api

import com.sportstalk.reactive.rx2.service.CommentModerationService
import com.sportstalk.reactive.rx2.service.CommentService

interface CommentClient: CommentService, CommentModerationService