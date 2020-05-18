package com.sportstalk.impl

import androidx.annotation.RestrictTo
import com.sportstalk.ServiceFactory
import com.sportstalk.api.service.ChatService
import com.sportstalk.api.ChatClient
import com.sportstalk.api.service.ChatModerationService
import com.sportstalk.models.ApiResponse
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

    override fun startEventUpdates(forRoomId: String) =
            chatService.startEventUpdates(forRoomId)

    override fun stopEventUpdates(forRoomId: String) =
            chatService.stopEventUpdates(forRoomId)

    override fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            chatService.createRoom(request = request)

    override fun getRoomDetails(chatRoomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            chatService.getRoomDetails(chatRoomId = chatRoomId)

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            chatService.getRoomDetailsByCustomId(chatRoomCustomId = chatRoomCustomId)

    override fun deleteRoom(chatRoomId: String): CompletableFuture<ApiResponse<DeleteChatRoomResponse>> =
            chatService.deleteRoom(chatRoomId = chatRoomId)

    override fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            chatService.updateRoom(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listRooms(limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListRoomsResponse>> =
            chatService.listRooms(
                    limit = limit,
                    cursor = cursor
            )

    override fun joinRoom(request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            chatService.joinRoom(
                    request = request
            )

    override fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            chatService.joinRoom(
                    chatRoomIdOrLabel = chatRoomIdOrLabel
            )

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            chatService.joinRoomByCustomId(
                    chatRoomCustomId = chatRoomCustomId,
                    request = request
            )

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListChatRoomParticipantsResponse>> =
            chatService.listRoomParticipants(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override fun exitRoom(chatRoomId: String, userId: String): CompletableFuture<ApiResponse<ExitChatRoomResponse>> =
            chatService.exitRoom(
                    chatRoomId = chatRoomId,
                    userId = userId
            )

    override fun getUpdates(chatRoomId: String, cursor: String?): CompletableFuture<ApiResponse<GetUpdatesResponse>> =
            chatService.getUpdates(
                    chatRoomId = chatRoomId,
                    cursor = cursor
            )

    override fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): CompletableFuture<ApiResponse<ExecuteChatCommandResponse>> =
            chatService.executeChatCommand(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listMessagesByUser(chatRoomId: String, userId: String, limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListMessagesByUser>> =
            chatService.listMessagesByUser(
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )

    override fun reportMessage(chatRoomId: String, eventId: String, request: ReportMessageRequest): CompletableFuture<ApiResponse<ChatEvent>> =
            chatService.reportMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun reactToAMessage(chatRoomId: String, eventId: String, request: ReactToAMessageRequest): CompletableFuture<ApiResponse<ChatEvent>> =
            chatService.reactToAMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun approveMessage(eventId: String, approve: Boolean): CompletableFuture<ApiResponse<ChatEvent>> =
            moderationService.approveMessage(
                    eventId = eventId,
                    approve = approve
            )

    override fun listMessagesNeedingModeration(): CompletableFuture<ApiResponse<ListMessagesNeedingModerationResponse>> =
            moderationService.listMessagesNeedingModeration()
}