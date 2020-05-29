package com.sportstalk.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.api.service.ChatService
import com.sportstalk.impl.handleSdkResponse
import com.sportstalk.impl.restapi.retrofit.services.ChatRetrofitService
import com.sportstalk.models.ApiResponse
import com.sportstalk.models.Kind
import com.sportstalk.models.SportsTalkException
import com.sportstalk.models.chat.*
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create
import java.net.URLEncoder
import java.util.concurrent.CompletableFuture

class ChatRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
        private val appId: String,
        private val json: Json,
        mRetrofit: Retrofit
): ChatService {

    private val service: ChatRetrofitService = mRetrofit.create()

    override val roomSubscriptions: MutableSet<String> = mutableSetOf()

    override fun startListeningToChatUpdates(forRoomId: String) {
        roomSubscriptions.add(forRoomId)
    }

    override fun stopListeningToChatUpdates(forRoomId: String) {
        roomSubscriptions.remove(forRoomId)
    }

    override fun createRoom(request: CreateChatRoomRequest): CompletableFuture<ChatRoom> =
            service.createRoom(
                    appId = appId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun getRoomDetails(chatRoomId: String): CompletableFuture<ChatRoom> =
            service.getRoomDetails(
                    appId = appId,
                    chatRoomId = chatRoomId
            )
                    .handleSdkResponse(json)

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): CompletableFuture<ChatRoom> =
            service.getRoomDetailsByCustomId(
                    appId = appId,
                    chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override fun deleteRoom(chatRoomId: String): CompletableFuture<DeleteChatRoomResponse> =
            service.deleteRoom(
                    appId = appId,
                    chatRoomId = URLEncoder.encode(chatRoomId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): CompletableFuture<ChatRoom> =
            service.updateRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun listRooms(limit: Int?, cursor: String?): CompletableFuture<ListRoomsResponse> =
            service.listRooms(
                    appId = appId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): CompletableFuture<JoinChatRoomResponse> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun joinRoom(chatRoomIdOrLabel: String): CompletableFuture<JoinChatRoomResponse> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomIdOrLabel
            )
                    .handleSdkResponse(json)

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): CompletableFuture<JoinChatRoomResponse> =
            service.joinRoomByCustomId(
                    appId = appId,
                    chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name()),
                    request = request
            )
                    .handleSdkResponse(json)

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): CompletableFuture<ListChatRoomParticipantsResponse> =
            service.listRoomParticipants(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun exitRoom(chatRoomId: String, userId: String): CompletableFuture<Any> =
            service.exitRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = ExitChatRoomRequest(userid = userId)
            )
                    .handle { resp, err ->
                        if (err != null) {
                            throw SportsTalkException(message = err.message, err = err)
                        } else {
                            if (resp.isSuccessful) {
                                Any()
                            } else {
                                throw resp.errorBody()?.string()?.let { errBodyStr ->
                                    json.parse(SportsTalkException.serializer(), errBodyStr)
                                }
                                        ?: SportsTalkException(
                                                kind = Kind.API,
                                                message = resp.message(),
                                                code = resp.code()
                                        )
                            }
                        }
                    }

    override fun getUpdates(
            chatRoomId: String,
            cursor: String?
    ): CompletableFuture<GetUpdatesResponse> =
            service.getUpdates(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    cursor = cursor
            )
                    .handle { resp, err ->
                        if (err != null) {
                            throw SportsTalkException(message = err.message, err = err)
                        } else {
                            val respBody = resp.body()
                            if (resp.isSuccessful && respBody?.data != null) {
                                respBody.data
                            } else {
                                throw resp.errorBody()?.string()?.let { errBodyStr ->
                                    json.parse(SportsTalkException.serializer(), errBodyStr)
                                }
                                        ?: SportsTalkException(
                                                kind = respBody?.kind ?: Kind.API,
                                                message = respBody?.message ?: resp.message(),
                                                code = respBody?.code ?: resp.code()
                                        )
                            }
                        }
                    }

    override fun executeChatCommand(
            chatRoomId: String,
            request: ExecuteChatCommandRequest
    ): CompletableFuture<ExecuteChatCommandResponse> =
            service.executeChatCommand(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): CompletableFuture<ChatEvent> =
            service.sendQuotedReply(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    replyto = replyTo,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun listMessagesByUser(
            chatRoomId: String,
            userId: String,
            limit: Int?,
            cursor: String?
    ): CompletableFuture<ListMessagesByUser> =
            service.listMessagesByUser(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun removeEvent(
            chatRoomId: String,
            eventId: String,
            userid: String,
            deleted: Boolean,
            permanentifnoreplies: Boolean?
    ): CompletableFuture<ChatEvent> =
            service.setMessageAsDeleted(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = deleted,
                    permanentifnoreplies = permanentifnoreplies
            )
                    .handleSdkResponse(json)

    override fun permanentlyDeleteEvent(chatRoomId: String, eventId: String, userid: String, permanentifnoreplies: Boolean?): CompletableFuture<ChatEvent> =
            removeEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = true,
                    permanentifnoreplies = permanentifnoreplies
            )

    override fun flagEventLogicallyDeleted(chatRoomId: String, eventId: String, userid: String, permanentifnoreplies: Boolean?): CompletableFuture<ChatEvent> =
            removeEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = false,
                    permanentifnoreplies = permanentifnoreplies
            )

    override fun reportMessage(
            chatRoomId: String,
            eventId: String,
            request: ReportMessageRequest
    ): CompletableFuture<ChatEvent> =
            service.reportMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun reactToEvent(
            chatRoomId: String,
            eventId: String,
            request: ReactToAMessageRequest
    ): CompletableFuture<ChatEvent> =
            service.reactMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
                    .handleSdkResponse(json)
}