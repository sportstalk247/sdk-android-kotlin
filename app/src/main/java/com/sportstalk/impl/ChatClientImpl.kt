package com.sportstalk.impl

import androidx.annotation.RestrictTo
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.ChatClient
import com.sportstalk.api.service.ChatModerationService
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.chat.*
import com.sportstalk.models.chat.moderation.ListMessagesNeedingModerationResponse
import java.util.concurrent.CompletableFuture


class ChatClientImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val config: ClientConfig
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

    override fun startListeningToChatUpdates(forRoomId: String) =
            chatService.startListeningToChatUpdates(forRoomId)

    override fun stopListeningToChatUpdates(forRoomId: String) =
            chatService.stopListeningToChatUpdates(forRoomId)

    override fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ChatRoom> =
            chatService.createRoom(request = request)

    override fun getRoomDetails(chatRoomId: String): CompletableFuture<ChatRoom> =
            chatService.getRoomDetails(chatRoomId = chatRoomId)

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): CompletableFuture<ChatRoom> =
            chatService.getRoomDetailsByCustomId(chatRoomCustomId = chatRoomCustomId)

    override fun deleteRoom(chatRoomId: String): CompletableFuture<DeleteChatRoomResponse> =
            chatService.deleteRoom(chatRoomId = chatRoomId)

    override fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): CompletableFuture<ChatRoom> =
            chatService.updateRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listRooms(limit: Int?, cursor: String?): CompletableFuture<ListRoomsResponse> =
            chatService.listRooms(
                    limit = limit,
                    cursor = cursor
            )

    override fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): CompletableFuture<JoinChatRoomResponse> =
            chatService.joinRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<JoinChatRoomResponse> =
            chatService.joinRoom(
                    chatRoomIdOrLabel = chatRoomIdOrLabel
            )

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): CompletableFuture<JoinChatRoomResponse> =
            chatService.joinRoomByCustomId(
                    chatRoomCustomId = chatRoomCustomId,
                    request = request
            )

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): CompletableFuture<ListChatRoomParticipantsResponse> =
            chatService.listRoomParticipants(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override fun exitRoom(chatRoomId: String, userId: String): CompletableFuture<Any> =
            chatService.exitRoom(
                    chatRoomId = chatRoomId,
                    userId = userId
            )

    override fun getUpdates(chatRoomId: String, cursor: String?): CompletableFuture<GetUpdatesResponse> =
            chatService.getUpdates(
                    chatRoomId = chatRoomId,
                    cursor = cursor
            )

    override fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): CompletableFuture<ExecuteChatCommandResponse> =
            chatService.executeChatCommand(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun sendQuotedReply(chatRoomId: String, replyTo: String, request: ExecuteChatCommandRequest): CompletableFuture<ExecuteChatCommandResponse> =
            chatService.sendQuotedReply(
                    chatRoomId = chatRoomId,
                    replyTo = replyTo,
                    request = request
            )

    override fun listMessagesByUser(chatRoomId: String, userId: String, limit: Int?, cursor: String?): CompletableFuture<ListMessagesByUser> =
            chatService.listMessagesByUser(
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )

    override fun removeEvent(chatRoomId: String, eventId: String, userid: String, deleted: Boolean, permanentifnoreplies: Boolean?): CompletableFuture<ChatEvent> =
            chatService.removeEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    deleted = deleted,
                    userid = userid,
                    permanentifnoreplies = permanentifnoreplies
            )

    override fun permanentlyDeleteEvent(chatRoomId: String, eventId: String, userid: String, permanentifnoreplies: Boolean?): CompletableFuture<ChatEvent> =
            chatService.permanentlyDeleteEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    permanentifnoreplies = permanentifnoreplies
            )

    override fun flagEventLogicallyDeleted(chatRoomId: String, eventId: String, userid: String, permanentifnoreplies: Boolean?): CompletableFuture<ChatEvent> =
            chatService.flagEventLogicallyDeleted(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    permanentifnoreplies = permanentifnoreplies
            )

    override fun reportMessage(chatRoomId: String, eventId: String, request: ReportMessageRequest): CompletableFuture<ChatEvent> =
            chatService.reportMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun reactToEvent(chatRoomId: String, eventId: String, request: ReactToAMessageRequest): CompletableFuture<ChatEvent> =
            chatService.reactToEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun approveMessage(eventId: String, approve: Boolean): CompletableFuture<ChatEvent> =
            moderationService.approveMessage(
                    eventId = eventId,
                    approve = approve
            )

    override fun listMessagesNeedingModeration(roomId: String?, limit: Int?, cursor: String?): CompletableFuture<ListMessagesNeedingModerationResponse> =
            moderationService.listMessagesNeedingModeration(
                    roomId = roomId,
                    limit = limit,
                    cursor = cursor
            )
}