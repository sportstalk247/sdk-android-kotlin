package com.sportstalk.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.api.service.ChatService
import com.sportstalk.impl.restapi.retrofit.services.ChatRetrofitService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.chat.*
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.CompletableFuture

class ChatRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        mRetrofit: Retrofit
): ChatService {

    private val service: ChatRetrofitService = mRetrofit.create()

    override val roomSubscriptions: MutableSet<String> = mutableSetOf()

    override fun startEventUpdates(forRoomId: String) {
        roomSubscriptions.add(forRoomId)
    }

    override fun stopEventUpdates(forRoomId: String) {
        roomSubscriptions.remove(forRoomId)
    }

    override fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.createRoom(
                    appId = appId,
                    request = request
            )

    override fun getRoomDetails(chatRoomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            service.getRoomDetails(
                    appId = appId,
                    chatRoomId = chatRoomId
            )

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): CompletableFuture<ApiResponse<ChatRoom>> =
            service.getRoomDetailsByCustomId(
                    appId = appId,
                    chatRoomCustomId = chatRoomCustomId
            )

    override fun deleteRoom(chatRoomId: String): CompletableFuture<ApiResponse<DeleteChatRoomResponse>> =
            service.deleteRoom(
                    appId = appId,
                    chatRoomId = chatRoomId
            )

    override fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): CompletableFuture<ApiResponse<ChatRoom>> =
            service.updateRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listRooms(limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListRoomsResponse>> =
            service.listRooms(
                    appId = appId,
                    limit = limit,
                    cursor = cursor
            )

    override fun joinRoom(request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = request.roomid!!,
                    request = request
            )

    override fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomIdOrLabel
            )

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): CompletableFuture<ApiResponse<JoinChatRoomResponse>> =
            service.joinRoomByCustomId(
                    appId = appId,
                    chatRoomCustomId = chatRoomCustomId,
                    request = request
            )

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): CompletableFuture<ApiResponse<ListChatRoomParticipantsResponse>> =
            service.listRoomParticipants(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )

    override fun exitRoom(chatRoomId: String, userId: String): CompletableFuture<ApiResponse<ExitChatRoomResponse>> =
            service.exitRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = ExitChatRoomRequest(userid = userId)
            )

    override fun getUpdates(
            chatRoomId: String,
            cursor: String?
    ): CompletableFuture<ApiResponse<GetUpdatesResponse>> =
            service.getUpdates(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    cursor = cursor
            )

    override fun executeChatCommand(
            chatRoomId: String,
            request: ExecuteChatCommandRequest
    ): CompletableFuture<ApiResponse<ExecuteChatCommandResponse>> =
            service.executeChatCommand(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )

    override fun listMessagesByUser(
            chatRoomId: String,
            userId: String,
            limit: Int?,
            cursor: String?
    ): CompletableFuture<ApiResponse<ListMessagesByUser>> =
            service.listMessagesByUser(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )

    override fun reportMessage(
            chatRoomId: String,
            eventId: String,
            request: ReportMessageRequest
    ): CompletableFuture<ApiResponse<ChatEvent>> =
            service.reportMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )

    override fun reactToAMessage(
            chatRoomId: String,
            eventId: String,
            request: ReactToAMessageRequest
    ): CompletableFuture<ApiResponse<ChatEvent>> =
            service.reactMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
}