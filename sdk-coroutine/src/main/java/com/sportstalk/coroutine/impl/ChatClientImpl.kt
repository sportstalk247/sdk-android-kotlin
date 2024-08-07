package com.sportstalk.coroutine.impl

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.ServiceFactory
import com.sportstalk.coroutine.api.ChatClient
import com.sportstalk.coroutine.service.ChatModerationService
import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.chat.moderation.*
import com.sportstalk.datamodels.users.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        config: ClientConfig
) : ChatClient {

    private val chatService: ChatService = ServiceFactory.Chat.get(config)
    private val moderationService: ChatModerationService = ServiceFactory.ChatModeration.get(config)

    // Current User state tracking
    private var _currentUser: User? = null
    override var currentUser: User?
        get() = _currentUser
        set(value) {
            _currentUser = value
        }

    // Room state tracking
    private var _currentRoom: ChatRoom? = null
    override var currentRoom: ChatRoom?
        get() = _currentRoom
        set(value) {
            _currentRoom = value
        }

    // Default image banner URL
    private var _defaultImageBannerUrl: String? = null
    override var defaultImageBannerUrl: String?
        get() = _defaultImageBannerUrl
        set(value) {
            _defaultImageBannerUrl = value
        }

    // Throttle message body for execute chat command
    private var _lastExecuteCommandMessage: String? = null
    // Throttle timestamp for execute chat command
    private var _lastExecuteCommandTimestamp: Long = 0L

    /**
     * Only used if event smoothing is enabled.
     * Keeps a list of messages we already rendered so we can ignore them in getUpdates
     */
    override var preRenderedMessages: MutableSet<String> = chatService.preRenderedMessages

    private var _chatEventsEmitter = MutableSharedFlow<List<ChatEvent>>(replay = 1, extraBufferCapacity = 1)
    override var chatEventsEmitter: Flow<List<ChatEvent>>
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        get() = _chatEventsEmitter.asSharedFlow()
        set(_) {}

    override fun roomSubscriptions(): Set<String> =
            chatService.roomSubscriptions()

    override fun getChatRoomEventUpdateCursor(forRoomId: String): String? =
            chatService.getChatRoomEventUpdateCursor(forRoomId)

    override fun setChatRoomEventUpdateCursor(forRoomId: String, cursor: String) {
        chatService.setChatRoomEventUpdateCursor(forRoomId, cursor)
    }

    override fun clearChatRoomEventUpdateCursor(fromRoomId: String) {
        chatService.clearChatRoomEventUpdateCursor(fromRoomId)
    }

    override fun startListeningToChatUpdates(forRoomId: String) =
            chatService.startListeningToChatUpdates(forRoomId)

    override fun stopListeningToChatUpdates(forRoomId: String) =
            chatService.stopListeningToChatUpdates(forRoomId)

    override suspend fun createRoom(request: CreateChatRoomRequest): ChatRoom =
            chatService.createRoom(request = request)

    override suspend fun getRoomDetails(chatRoomId: String): ChatRoom =
            chatService.getRoomDetails(chatRoomId = chatRoomId)

    override suspend fun getRoomDetailsExtendedBatch(entityTypes: List<RoomDetailEntityType>, roomIds: List<String>, customIds: List<String>): GetRoomDetailsExtendedBatchResponse =
            chatService.getRoomDetailsExtendedBatch(entityTypes, roomIds, customIds)

    override suspend fun getRoomDetailsByCustomId(chatRoomCustomId: String): ChatRoom =
            chatService.getRoomDetailsByCustomId(chatRoomCustomId = chatRoomCustomId)

    override suspend fun deleteRoom(chatRoomId: String): DeleteChatRoomResponse =
            chatService.deleteRoom(chatRoomId = chatRoomId)

    override suspend fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): ChatRoom =
            chatService.updateRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override suspend fun touchSession(chatRoomId: String, userId: String): ChatSubscription =
            chatService.touchSession(chatRoomId, userId)

    override suspend fun listRooms(limit: Int?, cursor: String?): ListRoomsResponse =
            chatService.listRooms(
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse {
        val response = chatService.joinRoom(
                chatRoomId = chatRoomId,
                request = request
        ).also { resp ->
            // Set current user
            _currentUser = resp.user
            // Set current chat room
            _currentRoom = resp.room
            // Reset execute command throttle
            _lastExecuteCommandMessage = null
            _lastExecuteCommandTimestamp = 0L

            // Internally store chatroom event cursor
            val cursor = resp.eventscursor?.cursor ?: ""
            setChatRoomEventUpdateCursor(
                    forRoomId = chatRoomId,
                    cursor = cursor
            )

            // Clear Pre-rendered events
            preRenderedMessages.clear()
        }
        val filteredEvents = (response.eventscursor?.events ?: listOf())
                // Filter out shadowban events for shadowbanned user
                .filterNot { ev ->
                    ev.shadowban == true && ev.userid != currentUser?.userid
                }

        return response.copy(
                eventscursor = response.eventscursor?.copy(
                        events = filteredEvents
                )
        )
    }

    override suspend fun joinRoom(chatRoomIdOrLabel: String): JoinChatRoomResponse {
        val response = chatService.joinRoom(
                chatRoomIdOrLabel = chatRoomIdOrLabel
        )
                .also { resp ->
                    // Set current user
                    _currentUser = resp.user
                    // Set current chat room
                    _currentRoom = resp.room
                    // Reset execute command throttle
                    _lastExecuteCommandMessage = null
                    _lastExecuteCommandTimestamp = 0L

                    // Internally store chatroom event cursor
                    val cursor = resp.eventscursor?.cursor ?: ""
                    setChatRoomEventUpdateCursor(
                            forRoomId = chatRoomIdOrLabel,
                            cursor = cursor
                    )

                    // Clear Pre-rendered events
                    preRenderedMessages.clear()
                }

        val filteredEvents = (response.eventscursor?.events ?: listOf())
                // Filter out shadowban events for shadowbanned user
                .filterNot { ev ->
                    ev.shadowban == true && ev.userid != currentUser?.userid
                }

        return response.copy(
                eventscursor = response.eventscursor?.copy(
                        events = filteredEvents
                )
        )
    }

    override suspend fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse {
        val response = chatService.joinRoomByCustomId(
                chatRoomCustomId = chatRoomCustomId,
                request = request
        )
                .also { resp ->
                    // Set current user
                    _currentUser = resp.user
                    // Set current chat room
                    _currentRoom = resp.room
                    // Reset execute command throttle
                    _lastExecuteCommandMessage = null
                    _lastExecuteCommandTimestamp = 0L

                    // Internally store chatroom event cursor
                    resp.room?.id?.let { roomId ->
                        val cursor = resp.eventscursor?.cursor ?: ""
                        setChatRoomEventUpdateCursor(
                            forRoomId = roomId,
                            cursor = cursor
                        )
                    }

                    // Clear Pre-rendered events
                    preRenderedMessages.clear()
                }

        val filteredEvents = (response.eventscursor?.events ?: listOf())
                // Filter out shadowban events for shadowbanned user
                .filterNot { ev ->
                    ev.shadowban == true && ev.userid != currentUser?.userid
                }

        return response.copy(
                eventscursor = response.eventscursor?.copy(
                        events = filteredEvents
                )
        )
    }

    override suspend fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): ListChatRoomParticipantsResponse =
            chatService.listRoomParticipants(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun listUserSubscribedRooms(
        userid: String,
        limit: Int?,
        cursor: String?
    ): ListUserSubscribedRoomsResponse =
        chatService.listUserSubscribedRooms(
            userid = userid,
            limit = limit,
            cursor = cursor
        )

    override suspend fun exitRoom(chatRoomId: String, userId: String) =
            chatService.exitRoom(
                    chatRoomId = chatRoomId,
                    userId = userId
            )
                    .also { resp ->
                        // Unset current user
                        _currentUser = null
                        // Unset currently active chat room
                        _currentRoom = null
                        // Reset execute command throttle
                        _lastExecuteCommandMessage = null
                        _lastExecuteCommandTimestamp = 0L

                        // Remove internally stored event cursor
                        clearChatRoomEventUpdateCursor(fromRoomId = chatRoomId)
                        // Clear Pre-rendered events
                        preRenderedMessages.clear()
                    }

    override suspend fun getUpdates(chatRoomId: String, limit: Int?, cursor: String?): GetUpdatesResponse =
            try {
                val response = chatService.getUpdates(
                        chatRoomId = chatRoomId,
                        limit = limit,
                        cursor = cursor
                )

                response.copy(
                        events = response.events
                                // Filter out shadowban events for shadowbanned user
                                .filterNot { ev ->
                                    ev.shadowban == true && ev.userid != currentUser?.userid
                                }
                )
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun messageIsReported(which: ChatEvent, userid: String): Boolean =
            chatService.messageIsReported(which, userid)

    override suspend fun messageIsReactedTo(which: ChatEvent, userid: String, reaction: String): Boolean =
            chatService.messageIsReactedTo(which, userid, reaction)

    override suspend fun listPreviousEvents(chatRoomId: String, limit: Int?, cursor: String?): ListEvents {
        val response = chatService.listPreviousEvents(
                chatRoomId = chatRoomId,
                limit = limit,
                cursor = cursor
        )

        return response.copy(
                events = response.events
                        // Filter out shadowban events for shadowbanned user
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
        )
    }

    override suspend fun getEventById(chatRoomId: String, eventId: String): ChatEvent =
            chatService.getEventById(
                    chatRoomId = chatRoomId,
                    eventId = eventId
            )

    override suspend fun reportUserInRoom(chatRoomId: String, userid: String, reporterid: String, reporttype: String): ChatRoom =
            chatService.reportUserInRoom(chatRoomId, userid, reporterid, reporttype)

    override suspend fun listEventsHistory(chatRoomId: String, limit: Int?, cursor: String?): ListEvents {
        val response = chatService.listEventsHistory(
                chatRoomId = chatRoomId,
                limit = limit,
                cursor = cursor
        )

        return response.copy(
                events = response.events
                        // Filter out shadowban events for shadowbanned user
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
        )
    }

    override suspend fun listEventsByType(chatRoomId: String, eventType: String, customtype: String?, limit: Int?, cursor: String?): ListEvents {
        val response = chatService.listEventsByType(
                chatRoomId = chatRoomId,
                eventType = eventType,
                customtype = customtype,
                limit = limit,
                cursor = cursor
        )

        return response.copy(
                events = response.events
                        // Filter out shadowban events for shadowbanned user
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
        )
    }

    override suspend fun listEventsByTimestamp(chatRoomId: String, timestamp: Long, limitolder: Int?, limitnewer: Int?): ListEventsByTimestamp {
        val response = chatService.listEventsByTimestamp(
                chatRoomId = chatRoomId,
                timestamp = timestamp,
                limitolder = limitolder,
                limitnewer = limitnewer
        )

        return response.copy(
                events = response.events
                        // Filter out shadowban events for shadowbanned user
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
        )
    }

    override suspend fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): ExecuteChatCommandResponse =
            if(_lastExecuteCommandMessage != request.command.trim()
                    || Math.abs(System.currentTimeMillis() - _lastExecuteCommandTimestamp) > DURATION_EXECUTE_COMMAND) {

                _lastExecuteCommandMessage = request.command.trim()
                _lastExecuteCommandTimestamp = System.currentTimeMillis()

                try {
                    chatService.executeChatCommand(
                            chatRoomId = chatRoomId,
                            request = request
                    ).also { execCommandResponse ->
                        // [Anti-flood Feature] Add to preRenderedMessages
                        execCommandResponse.speech?.let { chatEvent ->
                            // Emit/Trigger Event Update
                            _chatEventsEmitter.emit(listOf(chatEvent))
                            // Add to Pre-Rendered Messages
                            chatEvent.id?.let { id -> preRenderedMessages.add(id) }
                        }
                    }
                } catch (err: Throwable) {
                    // Bypass anti-flood feature if API or Internal error encountered
                    _lastExecuteCommandMessage = null
                    throw err
                }

            } else {
                throw SportsTalkException(
                        code = 418,
                        message = "418 - Not Allowed. Please wait to send this message again."
                )
            }

    override suspend fun sendThreadedReply(chatRoomId: String, replyTo: String, request: SendThreadedReplyRequest): ChatEvent =
            if(_lastExecuteCommandMessage != request.body.trim()
                    || Math.abs(System.currentTimeMillis() - _lastExecuteCommandTimestamp) > DURATION_EXECUTE_COMMAND) {

                _lastExecuteCommandMessage = request.body.trim()
                _lastExecuteCommandTimestamp = System.currentTimeMillis()

                try {
                    chatService.sendThreadedReply(
                            chatRoomId = chatRoomId,
                            replyTo = replyTo,
                            request = request
                    )
                } catch (err: Throwable) {
                    // Bypass anti-flood feature if API or Internal error encountered
                    _lastExecuteCommandMessage = null
                    throw err
                }
            } else {
                throw SportsTalkException(
                        code = 418,
                        message = "418 - Not Allowed. Please wait to send this message again."
                )
            }

    override suspend fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): ChatEvent =
            if(_lastExecuteCommandMessage != request.body.trim()
                    || Math.abs(System.currentTimeMillis() - _lastExecuteCommandTimestamp) > DURATION_EXECUTE_COMMAND) {

                _lastExecuteCommandMessage = request.body.trim()
                _lastExecuteCommandTimestamp = System.currentTimeMillis()

                try {
                    chatService.sendQuotedReply(
                            chatRoomId = chatRoomId,
                            replyTo = replyTo,
                            request = request
                    )
                } catch (err: Throwable) {
                    // Bypass anti-flood feature if API or Internal error encountered
                    _lastExecuteCommandMessage = null
                    throw err
                }
            } else {
                throw SportsTalkException(
                        code = 418,
                        message = "418 - Not Allowed. Please wait to send this message again."
                )
            }

    override suspend fun listMessagesByUser(chatRoomId: String, userId: String, limit: Int?, cursor: String?): ListMessagesByUser =
            chatService.listMessagesByUser(
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun bounceUser(chatRoomId: String, request: BounceUserRequest): BounceUserResponse =
            chatService.bounceUser(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override suspend fun searchEventHistory(request: SearchEventHistoryRequest): SearchEventHistoryResponse {
        val response = chatService.searchEventHistory(request)

        return response.copy(
                events = response.events
                        // Filter out shadowban events for shadowbanned user
                        .filterNot { ev ->
                            ev.shadowban == true && ev.userid != currentUser?.userid
                        }
        )
    }

    override suspend fun updateChatMessage(chatRoomId: String, eventId: String, request: UpdateChatMessageRequest): ChatEvent =
            chatService.updateChatMessage(chatRoomId, eventId, request)

    override suspend fun permanentlyDeleteEvent(chatRoomId: String, eventId: String, userid: String): DeleteEventResponse =
            chatService.permanentlyDeleteEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid
            )

    override suspend fun flagEventLogicallyDeleted(chatRoomId: String, eventId: String, userid: String, deleted: Boolean, permanentifnoreplies: Boolean?): DeleteEventResponse =
            chatService.flagEventLogicallyDeleted(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = deleted,
                    permanentifnoreplies = permanentifnoreplies
            )

    override suspend fun reportMessage(chatRoomId: String, eventId: String, request: ReportMessageRequest): ChatEvent =
            chatService.reportMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override suspend fun reactToEvent(chatRoomId: String, eventId: String, request: ReactToAMessageRequest): ChatEvent =
            chatService.reactToEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override suspend fun shadowBanUser(chatRoomId: String, userid: String, applyeffect: Boolean, expireseconds: Long?): ChatRoom =
            chatService.shadowBanUser(chatRoomId, userid, applyeffect, expireseconds)

    override suspend fun muteUser(chatRoomId: String, userid: String, applyeffect: Boolean, expireseconds: Long?): ChatRoom =
            chatService.muteUser(chatRoomId, userid, applyeffect, expireseconds)

    override suspend fun approveMessage(eventId: String, approve: Boolean): ChatEvent =
            moderationService.approveMessage(
                    eventId = eventId,
                    approve = approve
            )

    override suspend fun listMessagesNeedingModeration(roomId: String?, limit: Int?, cursor: String?): ListMessagesNeedingModerationResponse =
            moderationService.listMessagesNeedingModeration(
                    roomId = roomId,
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun purgeUserMessages(chatRoomId: String, userId: String, byUserId: String) =
        moderationService.purgeUserMessages(
            chatRoomId = chatRoomId,
            userId = userId,
            byUserId = byUserId,
        )

    companion object {
        private const val DURATION_EXECUTE_COMMAND = 20_000L
    }
}