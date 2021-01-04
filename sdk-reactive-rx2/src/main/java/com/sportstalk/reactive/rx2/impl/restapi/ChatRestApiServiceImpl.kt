package com.sportstalk.reactive.rx2.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.*
import com.sportstalk.reactive.rx2.impl.handleSdkResponse
import com.sportstalk.reactive.rx2.impl.restapi.retrofit.services.ChatRetrofitService
import com.sportstalk.reactive.rx2.service.ChatService
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create
import java.net.URLEncoder

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

    override fun createRoom(request: CreateChatRoomRequest): Single<ChatRoom> =
            service.createRoom(
                    appId = appId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun getRoomDetails(chatRoomId: String): Single<ChatRoom> =
            service.getRoomDetails(
                    appId = appId,
                    chatRoomId = chatRoomId
            )
                    .handleSdkResponse(json)

    override fun getRoomDetailsByCustomId(chatRoomCustomId: String): Single<ChatRoom> =
            service.getRoomDetailsByCustomId(
                    appId = appId,
                    chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override fun deleteRoom(chatRoomId: String): Single<DeleteChatRoomResponse> =
            service.deleteRoom(
                    appId = appId,
                    chatRoomId = URLEncoder.encode(chatRoomId, Charsets.UTF_8.name())
            )
                    .handleSdkResponse(json)

    override fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): Single<ChatRoom> =
            service.updateRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun listRooms(limit: Int?, cursor: String?): Single<ListRoomsResponse> =
            service.listRooms(
                    appId = appId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): Single<JoinChatRoomResponse> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)
                    .doOnSuccess { resp ->
                        // Internally store chatroom event cursor
                        val cursor = resp.eventscursor?.cursor ?: ""
                        chatRoomEventCursor[chatRoomId] = cursor
                    }

    override fun joinRoom(chatRoomIdOrLabel: String): Single<JoinChatRoomResponse> =
            service.joinRoom(
                    appId = appId,
                    chatRoomId = chatRoomIdOrLabel,
                    request = JoinChatRoomRequest(userid = "")
            )
                    .handleSdkResponse(json)
                    .doOnSuccess { resp ->
                        // Internally store chatroom event cursor
                        val roomId = resp.room?.id ?: return@doOnSuccess
                        val cursor = resp.eventscursor?.cursor ?: ""
                        chatRoomEventCursor[roomId] = cursor
                    }

    override fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): Single<JoinChatRoomResponse> =
            service.joinRoomByCustomId(
                    appId = appId,
                    chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name()),
                    request = request
            )
                    .handleSdkResponse(json)
                    .doOnSuccess { resp ->
                        // Internally store chatroom event cursor
                        val cursor = resp.eventscursor?.cursor ?: ""
                        chatRoomEventCursor[chatRoomCustomId] = cursor
                    }

    override fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): Single<ListChatRoomParticipantsResponse> =
            service.listRoomParticipants(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun exitRoom(chatRoomId: String, userId: String): Completable =
            service.exitRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = ExitChatRoomRequest(userid = userId)
            )
                    .handleSdkResponse(json)
                    .doOnSuccess {
                        // Remove internally stored event cursor
                        chatRoomEventCursor.remove(chatRoomId)
                    }
                    .ignoreElement()

    override fun getUpdates(chatRoomId: String, cursor: String?): Single<GetUpdatesResponse> =
            service.getUpdates(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    cursor = cursor
            )
                    .map { response ->
                        val respBody = response.body()
                        return@map if (response.isSuccessful && respBody?.data != null) {
                            respBody.data!!
                        } else {
                            throw response.errorBody()?.string()?.let { errBodyStr ->
                                json.decodeFromString(SportsTalkException.serializer(), errBodyStr)
                            }
                                    ?: SportsTalkException(
                                            kind = respBody?.kind ?: Kind.API,
                                            message = respBody?.message ?: response.message(),
                                            code = respBody?.code ?: response.code()
                                    )
                        }
                    }

    override fun messageIsReported(which: ChatEvent, userid: String): Boolean =
            which.reports.any { _report -> _report.userid == userid }

    override fun messageIsReactedTo(which: ChatEvent, userid: String, reaction: String): Boolean =
            if(which.replyto != null) {
                which.reactions.any { _reaction ->
                    _reaction.type == reaction
                            && _reaction.users.find { _usr -> _usr.userid == userid } != null
                }
                        || messageIsReactedTo(which.replyto!!, userid, reaction)
            } else {
                which.reactions.any { _reaction ->
                    _reaction.type == reaction
                            && _reaction.users.find { _usr -> _usr.userid == userid } != null
                }
            }

    override fun listPreviousEvents(chatRoomId: String, limit: Int?, cursor: String?): Single<ListEvents> =
            service.listPreviousEvents(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun getEventById(chatRoomId: String, eventId: String): Single<ChatEvent> =
            service.getEventById(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId
            )
                    .handleSdkResponse(json)

    override fun listEventsHistory(chatRoomId: String, limit: Int?, cursor: String?): Single<ListEvents> =
            service.listEventsHistory(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun executeChatCommand(chatRoomId: String, request: ExecuteChatCommandRequest): Single<ExecuteChatCommandResponse> =
            if(request.command.contains("purge")) {
                service.executeChatCommand(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        request = request
                )
                        .map { response ->
                            val body = response.body()
                            if(response.isSuccessful && body != null) {
                                ExecuteChatCommandResponse(
                                        kind = body.kind,
                                        message = body.message
                                )
                            } else {
                                throw response.errorBody()?.string()?.let { errBodyStr ->
                                    json.decodeFromString(SportsTalkException.serializer(), errBodyStr)
                                }
                                        ?: SportsTalkException(
                                                kind = Kind.API,
                                                message = response.message(),
                                                code = response.code()
                                        )
                            }
                        }
            } else {
                service.executeChatCommand(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        request = request
                )
                        .handleSdkResponse(json)
                        .map { it }
            }

    override fun sendThreadedReply(chatRoomId: String, replyTo: String, request: SendThreadedReplyRequest): Single<ExecuteChatCommandResponse> =
            service.sendThreadedReply(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    replyto = replyTo,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): Single<ChatEvent> =
            service.sendQuotedReply(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    replyto = replyTo,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun listMessagesByUser(chatRoomId: String, userId: String, limit: Int?, cursor: String?): Single<ListMessagesByUser> =
            service.listMessagesByUser(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    userId = userId,
                    limit = limit,
                    cursor = cursor
            )
                    .handleSdkResponse(json)

    override fun bounceUser(chatRoomId: String, request: BounceUserRequest): Single<BounceUserResponse> =
            service.bounceUser(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun deleteEvent(chatRoomId: String, eventId: String, userid: String): Single<DeleteEventResponse> =
            service.deleteEvent(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid
            )
                    .handleSdkResponse(json)

    override fun setMessageAsDeleted(chatRoomId: String, eventId: String, userid: String, deleted: Boolean, permanentifnoreplies: Boolean?): Single<DeleteEventResponse> =
            service.setMessageAsDeleted(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    userid = userid,
                    deleted = deleted,
                    permanentifnoreplies = permanentifnoreplies
            )
                    .handleSdkResponse(json)

    override fun reportMessage(chatRoomId: String, eventId: String, request: ReportMessageRequest): Single<ChatEvent> =
            service.reportMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
                    .handleSdkResponse(json)

    override fun reactToEvent(chatRoomId: String, eventId: String, request: ReactToAMessageRequest): Single<ChatEvent> =
            service.reactMessage(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    eventId = eventId,
                    request = request
            )
                    .handleSdkResponse(json)
}