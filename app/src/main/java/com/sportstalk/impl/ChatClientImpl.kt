package com.sportstalk.impl

import androidx.annotation.RestrictTo
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.ChatClient
import com.sportstalk.api.service.ChatModerationService
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.SportsTalkException
import com.sportstalk.models.chat.*
import com.sportstalk.models.chat.moderation.ListMessagesNeedingModerationResponse


class ChatClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        config: ClientConfig
) : ChatClient {

    private val chatService: ChatService = ServiceFactory.RestApi.Chat.get(config)
    private val moderationService: ChatModerationService = ServiceFactory.RestApi.ChatModeration.get(config)

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

    override val roomSubscriptions: MutableSet<String>
        get() = chatService.roomSubscriptions

    override val chatRoomEventCursor: HashMap<String, String>
        get() = chatService.chatRoomEventCursor

    override fun startListeningToChatUpdates(forRoomId: String) =
            chatService.startListeningToChatUpdates(forRoomId)

    override fun stopListeningToChatUpdates(forRoomId: String) =
            chatService.stopListeningToChatUpdates(forRoomId)

    override suspend fun createRoom(request: CreateChatRoomRequest): ChatRoom =
            chatService.createRoom(request = request)

    override suspend fun getRoomDetails(chatRoomId: String): ChatRoom =
            chatService.getRoomDetails(chatRoomId = chatRoomId)

    override suspend fun getRoomDetailsByCustomId(chatRoomCustomId: String): ChatRoom =
            chatService.getRoomDetailsByCustomId(chatRoomCustomId = chatRoomCustomId)

    override suspend fun deleteRoom(chatRoomId: String): DeleteChatRoomResponse =
            chatService.deleteRoom(chatRoomId = chatRoomId)

    override suspend fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): ChatRoom =
            chatService.updateRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override suspend fun listRooms(limit: Int?, cursor: String?): ListRoomsResponse =
            chatService.listRooms(
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse =
            chatService.joinRoom(
                    chatRoomId = chatRoomId,
                    request = request
            ).also { resp ->
                _currentRoom = resp.room
            }

    override suspend fun joinRoom(chatRoomIdOrLabel: String): JoinChatRoomResponse =
            chatService.joinRoom(
                    chatRoomIdOrLabel = chatRoomIdOrLabel
            )
                    .also { resp ->
                        // Set current chat room
                        _currentRoom = resp.room
                    }

    override suspend fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse =
            chatService.joinRoomByCustomId(
                    chatRoomCustomId = chatRoomCustomId,
                    request = request
            )
                    .also { resp ->
                        // Set current chat room
                        _currentRoom = resp.room
                    }

    override suspend fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): ListChatRoomParticipantsResponse =
            chatService.listRoomParticipants(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun exitRoom(chatRoomId: String, userId: String) =
            chatService.exitRoom(
                    chatRoomId = chatRoomId,
                    userId = userId
            )
                    .also { resp ->
                        // Unset currently active chat room
                        _currentRoom = null
                    }

    override suspend fun getUpdates(chatRoomId: String, cursor: String?): GetUpdatesResponse =
            try {
                chatService.getUpdates(
                        chatRoomId = chatRoomId,
                        cursor = cursor
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

    override suspend fun listPreviousEvents(chatRoomId: String, limit: Int?, cursor: String?): ListEvents =
            chatService.listPreviousEvents(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun getEventById(chatRoomId: String, eventId: String): ChatEvent =
            chatService.getEventById(
                    chatRoomId = chatRoomId,
                    eventId = eventId
            )

    override suspend fun listEventsHistory(chatRoomId: String, limit: Int?, cursor: String?): ListEvents =
            chatService.listEventsHistory(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override suspend fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): ExecuteChatCommandResponse =
            chatService.executeChatCommand(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override suspend fun sendThreadedReply(chatRoomId: String, replyTo: String, request: SendThreadedReplyRequest): ExecuteChatCommandResponse =
            chatService.sendThreadedReply(
                    chatRoomId = chatRoomId,
                    replyTo = replyTo,
                    request = request
            )

    override suspend fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): ChatEvent =
            chatService.sendQuotedReply(
                    chatRoomId = chatRoomId,
                    replyTo = replyTo,
                    request = request
            )

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

    override suspend fun deleteEvent(chatRoomId: String, eventId: String, userid: String): DeleteEventResponse =
            chatService.deleteEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid
            )

    override suspend fun setMessageAsDeleted(chatRoomId: String, eventId: String, userid: String, deleted: Boolean, permanentifnoreplies: Boolean?): DeleteEventResponse =
            chatService.setMessageAsDeleted(
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
}