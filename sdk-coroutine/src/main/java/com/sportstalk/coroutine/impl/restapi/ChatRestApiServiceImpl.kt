package com.sportstalk.coroutine.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.coroutine.service.ChatService
import com.sportstalk.coroutine.impl.handleSdkResponse
import com.sportstalk.coroutine.impl.restapi.retrofit.services.ChatRetrofitService
import com.sportstalk.datamodels.*
import com.sportstalk.datamodels.chat.*
import com.sportstalk.datamodels.users.User
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
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
) : ChatService {

    private val service: ChatRetrofitService = mRetrofit.create()

    private var _currentUser: User? = null
    override var currentUser: User?
        get() = _currentUser
        set(value) {
            _currentUser = value
        }
    private val roomSubscriptions: MutableSet<String> = mutableSetOf()
    private val chatRoomEventUpdateCursor: HashMap<String, String> = hashMapOf()

    /**
     * Only used if event smoothing is enabled.
     * Keeps a list of messages we already rendered so we can ignore them in getUpdates
     */
    override var preRenderedMessages: MutableSet<String> = mutableSetOf()

    internal var _chatEventEmitter = BroadcastChannel<List<ChatEvent>>(Channel.BUFFERED)
    override var chatEventsEmitter: Flow<List<ChatEvent>>
        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
        get() = _chatEventEmitter.asFlow()
        set(value) {}

    override fun roomSubscriptions(): Set<String> = roomSubscriptions

    override fun getChatRoomEventUpdateCursor(forRoomId: String): String? =
            if(chatRoomEventUpdateCursor.contains(forRoomId)) chatRoomEventUpdateCursor[forRoomId]
            else null

    override fun setChatRoomEventUpdateCursor(forRoomId: String, cursor: String) {
        chatRoomEventUpdateCursor[forRoomId] = cursor
    }

    override fun clearChatRoomEventUpdateCursor(fromRoomId: String) {
        chatRoomEventUpdateCursor.remove(fromRoomId)
    }

    override fun startListeningToChatUpdates(forRoomId: String) {
        roomSubscriptions.add(forRoomId)
    }

    override fun stopListeningToChatUpdates(forRoomId: String) {
        roomSubscriptions.remove(forRoomId)
    }

    override suspend fun createRoom(request: CreateChatRoomRequest): ChatRoom =
            try {
                service.createRoom(
                        appId = appId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun getRoomDetails(chatRoomId: String): ChatRoom =
            try {
                service.getRoomDetails(
                        appId = appId,
                        chatRoomId = chatRoomId
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun getRoomDetailsExtendedBatch(entityTypes: List<RoomDetailEntityType>, roomIds: List<String>, customIds: List<String>): GetRoomDetailsExtendedBatchResponse =
            try {
                service.getRoomDetailsExtendedBatch(
                        appId = appId,
                        entityTypes = entityTypes.map { it.keyword },
                        roomIds = roomIds,
                        customIds = customIds
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun getRoomDetailsByCustomId(chatRoomCustomId: String): ChatRoom =
            try {
                service.getRoomDetailsByCustomId(
                        appId = appId,
                        chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name())
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun deleteRoom(chatRoomId: String): DeleteChatRoomResponse =
            try {
                service.deleteRoom(
                        appId = appId,
                        chatRoomId = URLEncoder.encode(chatRoomId, Charsets.UTF_8.name())
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun updateRoom(chatRoomId: String, request: UpdateChatRoomRequest): ChatRoom =
            try {
                service.updateRoom(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun touchSession(chatRoomId: String, userId: String) =
            try {
                val response = service.touchSession(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        userId = userId
                )

                if (response.isSuccessful) {

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
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listRooms(limit: Int?, cursor: String?): ListRoomsResponse =
            try {
                service.listRooms(
                        appId = appId,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun joinRoom(chatRoomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse =
            try {
                service.joinRoom(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        request = request
                )
                        .handleSdkResponse(json)
                        .also { resp ->
                            _currentUser = resp.user
                            // Internally store chatroom event cursor
                            val cursor = resp.eventscursor?.cursor ?: ""
                            setChatRoomEventUpdateCursor(
                                    forRoomId = chatRoomId,
                                    cursor = cursor
                            )
                        }
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun joinRoom(chatRoomIdOrLabel: String): JoinChatRoomResponse =
            try {
                service.joinRoom(
                        appId = appId,
                        chatRoomId = chatRoomIdOrLabel,
                        request = JoinChatRoomRequest(userid = "")
                )
                        .handleSdkResponse(json)
                        .also { resp ->
                            _currentUser = resp.user
                            val roomId = resp.room?.id ?: return@also
                            // Internally store chatroom event cursor
                            val cursor = resp.eventscursor?.cursor ?: ""
                            setChatRoomEventUpdateCursor(
                                    forRoomId = roomId,
                                    cursor = cursor
                            )
                        }
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun joinRoomByCustomId(chatRoomCustomId: String, request: JoinChatRoomRequest): JoinChatRoomResponse =
            try {
                service.joinRoomByCustomId(
                        appId = appId,
                        chatRoomCustomId = URLEncoder.encode(chatRoomCustomId, Charsets.UTF_8.name()),
                        request = request
                )
                        .handleSdkResponse(json)
                        .also { resp ->
                            _currentUser = resp.user
                            val cursor = resp.eventscursor?.cursor ?: ""
                            // Internally store chatroom event cursor
                            setChatRoomEventUpdateCursor(
                                    forRoomId = chatRoomCustomId,
                                    cursor = cursor
                            )
                        }
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listRoomParticipants(chatRoomId: String, limit: Int?, cursor: String?): ListChatRoomParticipantsResponse =
            try {
                service.listRoomParticipants(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listUserSubscribedRooms(
        userid: String,
        limit: Int?,
        cursor: String?
    ): ListUserSubscribedRoomsResponse =
        try {
            service.listUserSubscribedRooms(
                appId = appId,
                userid = userid,
                limit = limit,
                cursor = cursor
            )
                .handleSdkResponse(json)
        } catch (err: SportsTalkException) {
            throw err
        } catch (err: Throwable) {
            throw SportsTalkException(
                message = err.message,
                err = err
            )
        }

    override suspend fun exitRoom(chatRoomId: String, userId: String) {
        try {
            val response = service.exitRoom(
                    appId = appId,
                    chatRoomId = chatRoomId,
                    request = ExitChatRoomRequest(userid = userId)
            )

            if (response.isSuccessful) {
                _currentUser = null
                // Remove internally stored event cursor
                clearChatRoomEventUpdateCursor(fromRoomId = chatRoomId)
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
        } catch (err: SportsTalkException) {
            throw err
        } catch (err: Throwable) {
            throw SportsTalkException(
                    message = err.message,
                    err = err
            )
        }
    }

    override suspend fun getUpdates(chatRoomId: String, limit: Int?, cursor: String?): GetUpdatesResponse =
            try {
                val response = service.getUpdates(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        limit = limit,
                        cursor = cursor
                )

                val respBody = response.body()
                if (response.isSuccessful && respBody?.data != null) {
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
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun messageIsReported(which: ChatEvent, userid: String): Boolean =
            which.reports.any { _report -> _report.userid == userid }

    override suspend fun messageIsReactedTo(which: ChatEvent, userid: String, reaction: String): Boolean =
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

    override suspend fun listPreviousEvents(chatRoomId: String, limit: Int?, cursor: String?): ListEvents =
            try {
                service.listPreviousEvents(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun getEventById(chatRoomId: String, eventId: String): ChatEvent =
            try {
                service.getEventById(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventId = eventId
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun reportUserInRoom(chatRoomId: String, userid: String, reporterid: String, reporttype: String): ChatRoom =
            try {
                val response = service.reportUserInRoom(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        userId = userid,
                        request = ReportUserInRoomRequest(
                                reporterid = reporterid,
                                reporttype = reporttype
                        )
                )

                val code = response.code()
                val message = response.message()
                val body = response.body()
                when {
                    code == 200 && body != null -> {
                        body!!.data!!
                    }
                    code == 200 && body == null -> {
                        throw SportsTalkException(
                                kind = Kind.API,
                                message = message,
                                code = code
                        )
                    }
                    else -> {
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
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listEventsHistory(chatRoomId: String, limit: Int?, cursor: String?): ListEvents =
            try {
                service.listEventsHistory(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listEventsByType(chatRoomId: String, eventType: String, customtype: String?, limit: Int?, cursor: String?): ListEvents =
            try {
                service.listEventsByType(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventtype = eventType,
                        customtype = customtype,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listEventsByTimestamp(chatRoomId: String, timestamp: Long, limitolder: Int?, limitnewer: Int?): ListEventsByTimestamp =
            try {
                service.listEventsByTimestamp(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        timestamp = timestamp,
                        limitolder = limitolder,
                        limitnewer = limitnewer
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun executeChatCommand(
            chatRoomId: String,
            request: ExecuteChatCommandRequest
    ): ExecuteChatCommandResponse =
            try {
                if(request.command.startsWith("*purge")) {
                    val response = service.executeChatCommand(
                            appId = appId,
                            chatRoomId = chatRoomId,
                            request = request
                    )
                    if (response.isSuccessful && response.body() != null) {
                        ExecuteChatCommandResponse(
                                kind = response.body()?.kind,
                                message = response.body()?.message
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
                } else {
                    service.executeChatCommand(
                            appId = appId,
                            chatRoomId = chatRoomId,
                            request = request
                    )
                            .handleSdkResponse(json)!!
                }
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun sendThreadedReply(chatRoomId: String, replyTo: String, request: SendThreadedReplyRequest): ChatEvent =
            try {
                service.sendThreadedReply(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        replyto = replyTo,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun sendQuotedReply(chatRoomId: String, replyTo: String, request: SendQuotedReplyRequest): ChatEvent =
            try {
                service.sendQuotedReply(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        replyto = replyTo,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun listMessagesByUser(
            chatRoomId: String,
            userId: String,
            limit: Int?,
            cursor: String?
    ): ListMessagesByUser =
            try {
                service.listMessagesByUser(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        userId = userId,
                        limit = limit,
                        cursor = cursor
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun bounceUser(chatRoomId: String, request: BounceUserRequest): BounceUserResponse =
            try {
                service.bounceUser(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun searchEventHistory(request: SearchEventHistoryRequest): SearchEventHistoryResponse =
            try {
                service.searchEventHistory(
                        appId = appId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun updateChatMessage(chatRoomId: String, eventId: String, request: UpdateChatMessageRequest): ChatEvent =
            try {
                service.updateChatMessage(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventId = eventId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun permanentlyDeleteEvent(
            chatRoomId: String,
            eventId: String,
            userid: String
    ): DeleteEventResponse =
            try {
                service.permanentlyDeleteEvent(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventId = eventId,
                        userid = userid
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun flagEventLogicallyDeleted(chatRoomId: String, eventId: String, userid: String, deleted: Boolean, permanentifnoreplies: Boolean?): DeleteEventResponse =
            try {
                service.flagEventLogicallyDeleted(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventId = eventId,
                        userid = userid,
                        deleted = deleted,
                        permanentifnoreplies = permanentifnoreplies
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun reportMessage(
            chatRoomId: String,
            eventId: String,
            request: ReportMessageRequest
    ): ChatEvent =
            try {
                service.reportMessage(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventId = eventId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun reactToEvent(
            chatRoomId: String,
            eventId: String,
            request: ReactToAMessageRequest
    ): ChatEvent =
            try {
                service.reactMessage(
                        appId = appId,
                        chatRoomId = chatRoomId,
                        eventId = eventId,
                        request = request
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun shadowBanUser(chatRoomId: String, userid: String, applyeffect: Boolean, expireseconds: Long?): ChatRoom =
            try {
                service.shadowBanUser(
                        appId = appId,
                        chatroomId = chatRoomId,
                        request = ShadowBanUserInRoomRequest(
                                userid = userid,
                                applyeffect = applyeffect,
                                expireseconds = expireseconds
                        )
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }

    override suspend fun muteUser(chatRoomId: String, userid: String, applyeffect: Boolean, expireseconds: Long?): ChatRoom =
            try {
                service.muteUser(
                        appId = appId,
                        chatroomId = chatRoomId,
                        request = MuteUserInRoomRequest(
                                userid = userid,
                                applyeffect = applyeffect,
                                expireseconds = expireseconds
                        )
                )
                        .handleSdkResponse(json)
            } catch (err: SportsTalkException) {
                throw err
            } catch (err: Throwable) {
                throw SportsTalkException(
                        message = err.message,
                        err = err
                )
            }
}