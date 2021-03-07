package com.sportstalk.reactive.rx2.impl

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.ClientConfig
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.users.User
import com.sportstalk.reactive.rx2.ServiceFactory
import com.sportstalk.reactive.rx2.api.ChatClient
import com.sportstalk.reactive.rx2.service.ChatService
import io.reactivex.Completable
import io.reactivex.Single

class ChatClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        config: ClientConfig
): ChatClient {

    private val chatService: ChatService = ServiceFactory.Chat.get(config)

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

    override fun createRoom(request: CreateChatRoomRequest): Single<ChatRoom> =
            chatService.createRoom(request = request)

    override fun getRoomDetails(chatRoomId: String): Single<ChatRoom> =
            chatService.getRoomDetails(chatRoomId = chatRoomId)

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): Single<ChatRoom> =
            chatService.getRoomDetailsByCustomId(chatRoomCustomId = chatRoomCustomId)

    override fun deleteRoom(chatRoomId: String): Single<DeleteChatRoomResponse> =
            chatService.deleteRoom(chatRoomId = chatRoomId)

    override fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): Single<ChatRoom> =
            chatService.updateRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listRooms(limit: Int?, cursor: String?): Single<ListRoomsResponse> =
            chatService.listRooms(
                    limit = limit,
                    cursor = cursor
            )

    override fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): Single<JoinChatRoomResponse> =
            chatService.joinRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .doOnSuccess { resp ->
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
                    }
                    .map { response ->
                        val filteredEvents = (response.eventscursor?.events ?: listOf())
                                // Filter out shadowban events for shadowbanned user
                                .filterNot { ev ->
                                    ev.shadowban == true && ev.userid != currentUser?.userid
                                }

                        response.copy(
                                eventscursor = response.eventscursor?.copy(
                                        events = filteredEvents
                                )
                        )
                    }

    override fun joinRoom(chatRoomIdOrLabel: String): Single<JoinChatRoomResponse> =
            chatService.joinRoom(
                    chatRoomIdOrLabel = chatRoomIdOrLabel
            )
                    .doOnSuccess { resp ->
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
                    }
                    .map { response ->
                        val filteredEvents = (response.eventscursor?.events ?: listOf())
                                // Filter out shadowban events for shadowbanned user
                                .filterNot { ev ->
                                    ev.shadowban == true && ev.userid != currentUser?.userid
                                }

                        response.copy(
                                eventscursor = response.eventscursor?.copy(
                                        events = filteredEvents
                                )
                        )
                    }

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): Single<JoinChatRoomResponse> =
            chatService.joinRoomByCustomId(
                    chatRoomCustomId = chatRoomCustomId,
                    request = request
            )
                    .doOnSuccess { resp ->
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
                                forRoomId = chatRoomCustomId,
                                cursor = cursor
                        )
                    }
                    .map { response ->
                        val filteredEvents = (response.eventscursor?.events ?: listOf())
                                // Filter out shadowban events for shadowbanned user
                                .filterNot { ev ->
                                    ev.shadowban == true && ev.userid != currentUser?.userid
                                }

                        response.copy(
                                eventscursor = response.eventscursor?.copy(
                                        events = filteredEvents
                                )
                        )
                    }

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): Single<ListChatRoomParticipantsResponse> =
            chatService.listRoomParticipants(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override fun exitRoom(chatRoomId: String, userId: String): Completable =
            chatService.exitRoom(
                    chatRoomId = chatRoomId,
                    userId = userId
            )
                    .doOnComplete {
                        // Unset current user
                        _currentUser = null
                        // Unset currently active chat room
                        _currentRoom = null
                        // Reset execute command throttle
                        _lastExecuteCommandMessage = null
                        _lastExecuteCommandTimestamp = 0L

                        // Remove internally stored event cursor
                        clearChatRoomEventUpdateCursor(fromRoomId = chatRoomId)
                    }

    override fun getUpdates(chatRoomId: String, limit: Int?, cursor: String?): Single<GetUpdatesResponse> =
            chatService.getUpdates(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .map { response ->
                        response.copy(
                                events = response.events
                                        // Filter out shadowban events for shadowbanned user
                                        .filterNot { ev ->
                                            ev.shadowban == true && ev.userid != currentUser?.userid
                                        }
                        )
                    }

    override fun messageIsReported(which: ChatEvent, userid: String): Boolean =
            chatService.messageIsReported(which, userid)

    override fun messageIsReactedTo(which: ChatEvent, userid: String, reaction: String): Boolean =
            chatService.messageIsReactedTo(which, userid, reaction)

    override fun listPreviousEvents(chatRoomId: String, limit: Int?, cursor: String?): Single<ListEvents> =
            chatService.listPreviousEvents(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .map { response ->
                        response.copy(
                                events = response.events
                                        // Filter out shadowban events for shadowbanned user
                                        .filterNot { ev ->
                                            ev.shadowban == true && ev.userid != currentUser?.userid
                                        }
                        )
                    }

    override fun getEventById(chatRoomId: String, eventId: String): Single<ChatEvent> =
            chatService.getEventById(
                    chatRoomId = chatRoomId,
                    eventId = eventId
            )

    override fun reportUserInRoom(chatRoomId: String, userid: String, reporterid: String, reporttype: String): Single<ChatRoom> =
            chatService.reportUserInRoom(
                    chatRoomId = chatRoomId,
                    userid = userid,
                    reporterid = reporterid,
                    reporttype = reporttype
            )

    override fun listEventsHistory(chatRoomId: String, limit: Int?, cursor: String?): Single<ListEvents> =
            chatService.listEventsHistory(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .map { response ->
                        response.copy(
                                events = response.events
                                        // Filter out shadowban events for shadowbanned user
                                        .filterNot { ev ->
                                            ev.shadowban == true && ev.userid != currentUser?.userid
                                        }
                        )
                    }

    override fun listEventsByType(chatRoomId: String, eventtype: String, customtype: String?, limit: Int?, cursor: String?): Single<ListEvents> =
            chatService.listEventsByType(
                    chatRoomId = chatRoomId,
                    eventtype = eventtype,
                    customtype = customtype,
                    limit = limit,
                    cursor = cursor
            )
                    .map { response ->
                        response.copy(
                                events = response.events
                                        // Filter out shadowban events for shadowbanned user
                                        .filterNot { ev ->
                                            ev.shadowban == true && ev.userid != currentUser?.userid
                                        }
                        )
                    }

    override fun listEventsByTimestamp(chatRoomId: String, timestamp: Long, limitolder: Int?, limitnewer: Int?): Single<ListEventsByTimestamp> =
            chatService.listEventsByTimestamp(
                    chatRoomId = chatRoomId,
                    timestamp = timestamp,
                    limitolder = limitolder,
                    limitnewer = limitnewer
            )
                    .map { response ->
                        response.copy(
                                events = response.events
                                        // Filter out shadowban events for shadowbanned user
                                        .filterNot { ev ->
                                            ev.shadowban == true && ev.userid != currentUser?.userid
                                        }
                        )
                    }

    override fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): Single<ExecuteChatCommandResponse> =
            if(_lastExecuteCommandMessage != request.command
                    || Math.abs(System.currentTimeMillis() - _lastExecuteCommandTimestamp) > DURATION_EXECUTE_COMMAND) {

                _lastExecuteCommandMessage = request.command
                _lastExecuteCommandTimestamp = System.currentTimeMillis()

                chatService.executeChatCommand(
                        chatRoomId = chatRoomId,
                        request = request
                )
            } else {
                Single.error<ExecuteChatCommandResponse>(
                        SportsTalkException(
                                code = 418,
                                message = "418 - Not Allowed. Please wait to send this message again."
                        )
                )
            }

    override fun sendThreadedReply(chatRoomId: String, replyTo: String, request: SendThreadedReplyRequest): Single<ChatEvent> =
            if(_lastExecuteCommandMessage != request.body
                    || Math.abs(System.currentTimeMillis() - _lastExecuteCommandTimestamp) > DURATION_EXECUTE_COMMAND) {

                _lastExecuteCommandMessage = request.body
                _lastExecuteCommandTimestamp = System.currentTimeMillis()

                chatService.sendThreadedReply(
                        chatRoomId = chatRoomId,
                        replyTo = replyTo,
                        request = request
                )
            } else {
                Single.error<ChatEvent>(
                        SportsTalkException(
                                code = 418,
                                message = "418 - Not Allowed. Please wait to send this message again."
                        )
                )
            }

    override fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): Single<ChatEvent> =
            if(_lastExecuteCommandMessage != request.body
                    || Math.abs(System.currentTimeMillis() - _lastExecuteCommandTimestamp) > DURATION_EXECUTE_COMMAND) {

                _lastExecuteCommandMessage = request.body
                _lastExecuteCommandTimestamp = System.currentTimeMillis()

                chatService.sendQuotedReply(
                        chatRoomId = chatRoomId,
                        replyTo = replyTo,
                        request = request
                )
            } else {
                Single.error<ChatEvent>(
                        SportsTalkException(
                                code = 418,
                                message = "418 - Not Allowed. Please wait to send this message again."
                        )
                )
            }

    override fun listMessagesByUser(chatRoomId: String, userId: String, limit: Int?, cursor: String?): Single<ListMessagesByUser> =
            chatService.listMessagesByUser(
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )

    override fun bounceUser(chatRoomId: String, request: BounceUserRequest): Single<BounceUserResponse> =
            chatService.bounceUser(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun searchEventHistory(request: SearchEventHistoryRequest): Single<SearchEventHistoryResponse> =
            chatService.searchEventHistory(request)
                    .map { response ->
                        response.copy(
                                events = response.events
                                        // Filter out shadowban events for shadowbanned user
                                        .filterNot { ev ->
                                            ev.shadowban == true && ev.userid != currentUser?.userid
                                        }
                        )
                    }

    override fun updateChatMessage(chatRoomId: String, eventId: String, request: UpdateChatMessageRequest): Single<ChatEvent> =
            chatService.updateChatMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun permanentlyDeleteEvent(chatRoomId: String, eventId: String, userid: String): Single<DeleteEventResponse> =
            chatService.permanentlyDeleteEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid
            )

    override fun flagEventLogicallyDeleted(chatRoomId: String, eventId: String, userid: String, deleted: Boolean, permanentifnoreplies: Boolean?): Single<DeleteEventResponse> =
            chatService.flagEventLogicallyDeleted(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = deleted,
                    permanentifnoreplies = permanentifnoreplies
            )

    override fun reportMessage(chatRoomId: String, eventId: String, request: ReportMessageRequest): Single<ChatEvent> =
            chatService.reportMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun reactToEvent(chatRoomId: String, eventId: String, request: ReactToAMessageRequest): Single<ChatEvent> =
            chatService.reactToEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun shadowBanUser(chatRoomId: String, userid: String, applyeffect: Boolean, expireseconds: Long?): Single<ChatRoom> =
            chatService.shadowBanUser(chatRoomId, userid, applyeffect, expireseconds)

    override fun muteUser(chatRoomId: String, userid: String, applyeffect: Boolean, expireseconds: Long?): Single<ChatRoom> =
            chatService.muteUser(chatRoomId, userid, applyeffect, expireseconds)

    companion object {
        private const val DURATION_EXECUTE_COMMAND = 20_000L
    }
}