package com.sportstalk.reactive.rx2.impl.restapi

import androidx.annotation.RestrictTo
import com.sportstalk.datamodels.Kind
import com.sportstalk.datamodels.SportsTalkException
import com.sportstalk.datamodels.chat.ChatEvent
import com.sportstalk.datamodels.chat.moderation.ApproveMessageRequest
import com.sportstalk.datamodels.chat.moderation.ListMessagesNeedingModerationResponse
import com.sportstalk.datamodels.chat.moderation.PurgeUserMessagesRequest
import com.sportstalk.reactive.rx2.impl.handleSdkResponse
import com.sportstalk.reactive.rx2.impl.restapi.retrofit.services.ChatModerationRetrofitService
import com.sportstalk.reactive.rx2.service.ChatModerationService
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import retrofit2.create

class ChatModerationRestApiServiceImpl
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
constructor(
    private val appId: String,
    private val json: Json,
    mRetrofit: Retrofit
) : ChatModerationService {

    private val service: ChatModerationRetrofitService = mRetrofit.create()

    override fun approveMessage(eventId: String, approve: Boolean): Single<ChatEvent> =
        service.approveMessage(
            appId = appId,
            eventId = eventId,
            request = ApproveMessageRequest(approve)
        )
            .handleSdkResponse(json)

    override fun listMessagesNeedingModeration(
        roomId: String?,
        limit: Int?,
        cursor: String?
    ): Single<ListMessagesNeedingModerationResponse> =
        service.listMessagesNeedingModeration(
            appId = appId,
            roomId = roomId,
            limit = limit,
            cursor = cursor
        )
            .handleSdkResponse(json)

    override fun purgeUserMessages(
        chatRoomId: String,
        userId: String,
        byUserId: String
    ): Completable {
        return service.purgeUserMessages(
            appId = appId,
            roomId = chatRoomId,
            request = PurgeUserMessagesRequest(
                userid = userId,
                byuserid = byUserId,
            ),
        )
            .flatMapCompletable { response ->
                return@flatMapCompletable if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val code = result.code ?: response.code()

                    if (code !in 200..399) {
                        val kind = result.kind
                        val message = result.message ?: response.message()
                        Completable.error(
                            SportsTalkException(
                                kind = kind,
                                message = message,
                                code = code,
                            )
                        )
                    } else {
                        Completable.complete()
                    }
                } else {
                    Completable.error(
                        response.errorBody()?.string()?.trim()?.takeIf { it.isNotEmpty() }
                            ?.let { errBodyStr ->
                                json.decodeFromString(SportsTalkException.serializer(), errBodyStr)
                            }
                            ?: SportsTalkException(
                                kind = Kind.API,
                                code = response.code(),
                                message = response.message(),
                            )
                    )
                }
            }
    }
}