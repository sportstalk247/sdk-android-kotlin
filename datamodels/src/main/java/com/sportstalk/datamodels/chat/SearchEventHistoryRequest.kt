
package com.sportstalk.datamodels.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchEventHistoryRequest(
        val fromuserid: String? = null,
        val fromhandle: String? = null,
        val roomid: String? = null,
        val body: String? = null,
        /** If not provided, Backend API defaults to 50. Max is 200 */
        val limit: Int? = null,
        val cursor: String? = null,
        /** [Direction.Forward] or [Direction.Backward] */
        val direction: Direction? = null,
        /** Any [EventType] constant */
        val types: List<String>? = null
) {

        @Serializable
        enum class Direction(val rawValue: String) {
                @SerialName("forward")
                Forward("forward"),

                @SerialName("backward")
                Backward("backward"),
        }

}