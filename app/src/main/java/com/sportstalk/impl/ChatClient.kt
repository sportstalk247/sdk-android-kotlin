package com.sportstalk.impl

import com.sportstalk.ServiceFactory
import com.sportstalk.api.ChatService
import com.sportstalk.api.IChatClient
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.ClientConfig
import com.sportstalk.models.chat.*
import com.sportstalk.models.users.User
import java.util.concurrent.CompletableFuture

class ChatClient(
        private val config: ClientConfig
) : IChatClient {

    private val service: ChatService = ServiceFactory.RestApi.Chat.get(config)

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
        get() = service.roomSubscriptions

    override fun startEventUpdates(forRoomId: String) =
            service.startEventUpdates(forRoomId)

    override fun stopEventUpdates(forRoomId: String) =
            service.stopEventUpdates(forRoomId)

    override fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.createRoom(request = request)

    override fun getRoomDetails(chatRoomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            service.getRoomDetails(chatRoomId = chatRoomId)

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            service.getRoomDetailsByCustomId(chatRoomCustomId = chatRoomCustomId)

    override fun deleteRoom(chatRoomId: String): CompletableFuture<ApiResponse<DeleteChatRoomResponse>> =
            service.deleteRoom(chatRoomId = chatRoomId)

    override fun updateRoom(request: UpdateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.updateRoom(request = request)

    override fun listRooms(limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListRoomsResponse>> =
            service.listRooms(
                    limit = limit,
                    cursor = cursor
            )

    override fun joinRoom(request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoom(
                    request = request
            )

    override fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoom(
                    chatRoomIdOrLabel = chatRoomIdOrLabel
            )

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoomByCustomId(
                    chatRoomCustomId = chatRoomCustomId,
                    request = request
            )

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListChatRoomParticipantsResponse>> =
            service.listRoomParticipants(
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override fun exitRoom(chatRoomId: String, userId: String): CompletableFuture<ApiResponse<ExitChatRoomResponse>> =
            service.exitRoom(
                    chatRoomId = chatRoomId,
                    userId = userId
            )

    override fun getUpdates(chatRoomId: String, cursor: String?): CompletableFuture<ApiResponse<GetUpdatesResponse>> =
            service.getUpdates(
                    chatRoomId = chatRoomId,
                    cursor = cursor
            )

    override fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): CompletableFuture<ApiResponse<ExecuteChatCommandResponse>> =
            service.executeChatCommand(
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listMessagesByUser(chatRoomId: String, userId: String, limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListMessagesByUser>> =
            service.listMessagesByUser(
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )

    override fun reportMessage(chatRoomId: String, eventId: String, request: ReportMessageRequest): CompletableFuture<ApiResponse<ChatEvent>> =
            service.reportMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun reactToAMessage(chatRoomId: String, eventId: String, request: ReactToAMessageRequest): CompletableFuture<ApiResponse<ChatEvent>> =
            service.reactToAMessage(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
}