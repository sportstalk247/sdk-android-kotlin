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

    override val chatRoomEventCursor: HashMap<String, String> = hashMapOf()

    override fun startListeningToChatUpdates(forRoomId: String) {
        roomSubscriptions.add(forRoomId)
    }

    override fun stopListeningToChatUpdates(forRoomId: String) {
        roomSubscriptions.remove(forRoomId)
    }

    override suspend fun createRoom(request: CreateChatRoomRequest): ChatRoom =
            service.createRoom(
                    appId = appId,
                    request = request
            )
                    .handleSdkResponse(json)

    override suspend fun getRoomDetails(chatRoomId: String): ChatRoom =
            service.getRoomDetails(
                    appId = appId,
                    chatRoomId = chatRoomId
            )
                    .handleSdkResponse(json)

    override suspend fun getRoomDetailsByCustomId(chatRoomCustomId: String): ChatRoom =
            service.getRoomDetailsByCustomId(
                    appId = appId,
                    chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override suspend fun deleteRoom(chatRoomId: String): DeleteChatRoomResponse =
            service.deleteRoom(
                    appId = appId,
                    chatRoomId = URLEncoder.encode(chatRoomId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override suspend fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): ChatRoom =
            service.updateRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override suspend fun listRooms(limit: Int?, cursor: String?): ListRoomsResponse =
            service.listRooms(
                    appId = appId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override suspend fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)
                    .also { resp ->
                        // Internally store chatroom event cursor
                        val cursor = resp.eventscursor?.cursor ?: ""
                        chatRoomEventCursor[chatRoomId] = cursor
                    }

    override suspend fun joinRoom(chatRoomIdOrLabel: String): JoinChatRoomResponse =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomIdOrLabel
            )
                    .handleSdkResponse(json)
                    .also { resp ->
                        // Internally store chatroom event cursor
                        val roomId = resp.room?.id ?: return@also
                        val cursor = resp.eventscursor?.cursor ?: ""
                        chatRoomEventCursor[roomId] = cursor
                    }

    override suspend fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse =
            service.joinRoomByCustomId(
                    appId = appId,
                    chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name()),
                    request = request
            )
                    .handleSdkResponse(json)
                    .also { resp ->
                        // Internally store chatroom event cursor
                        val cursor = resp.eventscursor?.cursor ?: ""
                        chatRoomEventCursor[chatRoomCustomId] = cursor
                    }

    override suspend fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): ListChatRoomParticipantsResponse =
            service.listRoomParticipants(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override suspend fun exitRoom(chatRoomId: String, userId: String) {
        val response = service.exitRoom(
                appId = appId,
                chatRoomId = chatRoomId,
                request = ExitChatRoomRequest(userid = userId)
        )

        if (response.isSuccessful) {
            // Remove internally stored event cursor
            chatRoomEventCursor.remove(chatRoomId)
        } else {
            throw response.errorBody()?.string()?.let { errBodyStr ->
                json.parse(SportsTalkException.serializer(), errBodyStr)
            }
                    ?: SportsTalkException(
                            kind = Kind.API,
                            message = response.message(),
                            code = response.code()
                    )
        }
    }

    override suspend fun getUpdates(
            chatRoomId: String,
            cursor: String?
    ): GetUpdatesResponse {
        val response = service.getUpdates(
                appId = appId,
                chatRoomId = chatRoomId,
                cursor = cursor
        )

        val respBody = response.body()
        return if (response.isSuccessful && respBody?.data != null) {
            respBody.data
        } else {
            throw response.errorBody()?.string()?.let { errBodyStr ->
                json.parse(SportsTalkException.serializer(), errBodyStr)
            }
                    ?: SportsTalkException(
                            kind = respBody?.kind ?: Kind.API,
                            message = respBody?.message ?: response.message(),
                            code = respBody?.code ?: response.code()
                    )
        }
    }

    override suspend fun listPreviousEvents(chatRoomId: String, limit: Int?, cursor: String?): ListEvents =
            service.listPreviousEvents(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override suspend fun listEventsHistory(chatRoomId: String, limit: Int?, cursor: String?): ListEvents =
            service.listEventsHistory(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override suspend fun executeChatCommand(
            chatRoomId: String,
            request: ExecuteChatCommandRequest
    ): ExecuteChatCommandResponse =
            service.executeChatCommand(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override suspend fun sendThreadedReply(chatRoomId: String, replyTo: String, request: SendThreadedReplyRequest): ExecuteChatCommandResponse =
            service.sendThreadedReply(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    replyto = replyTo,
                    request = request
            )
                    .handleSdkResponse(json)

    override suspend fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): ChatEvent =
            service.sendQuotedReply(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    replyto = replyTo,
                    request = request
            )
                    .handleSdkResponse(json)

    override suspend fun listMessagesByUser(
            chatRoomId: String,
            userId: String,
            limit: Int?,
            cursor: String?
    ): ListMessagesByUser =
            service.listMessagesByUser(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override suspend fun removeEvent(
            chatRoomId: String,
            eventId: String,
            userid: String,
            deleted: Boolean,
            permanentifnoreplies: Boolean?
    ): DeleteEventResponse =
            service.setMessageAsDeleted(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = deleted,
                    permanentifnoreplies = permanentifnoreplies
            )
                    .handleSdkResponse(json)

    override suspend fun permanentlyDeleteEvent(chatRoomId: String, eventId: String, userid: String, permanentifnoreplies: Boolean?): DeleteEventResponse =
            removeEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = true,
                    permanentifnoreplies = permanentifnoreplies
            )

    override suspend fun flagEventLogicallyDeleted(chatRoomId: String, eventId: String, userid: String, permanentifnoreplies: Boolean?): DeleteEventResponse =
            removeEvent(
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = false,
                    permanentifnoreplies = permanentifnoreplies
            )

    override suspend fun reportMessage(
            chatRoomId: String,
            eventId: String,
            request: ReportMessageRequest
    ): ChatEvent =
            service.reportMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
                    .handleSdkResponse(json)

    override suspend fun reactToEvent(
            chatRoomId: String,
            eventId: String,
            request: ReactToAMessageRequest
    ): ChatEvent =
            service.reactMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
                    .handleSdkResponse(json)
}