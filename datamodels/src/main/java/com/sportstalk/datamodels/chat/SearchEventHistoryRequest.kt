
package com.sportstalk.datamodels.chat

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
        /** [Direction.FORWARD] or [Direction.BACKWARD] */
        val direction: String? = null,
        /** Any [EventType] constant */
        val types: List<String>? = null
) {
    object Direction {
        const val FORWARD = "forward"
        const val BACKWARD = "backward"
    }
}