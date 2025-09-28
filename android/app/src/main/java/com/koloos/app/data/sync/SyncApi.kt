package com.koloos.app.data.sync

import retrofit2.http.Body
import retrofit2.http.POST

data class SyncPullRequest(val lastVersion: Long)
data class SyncPushRequest(val events: List<SyncEventDto>)
data class SyncEventDto(val type: String, val payload: Map<String, Any?>, val operation: String)
data class SyncPullResponse(val data: List<RemoteSyncEvent>)
data class RemoteSyncEvent(val id: Long, val entity_type: String, val entity_id: String, val payload: String, val version: Long)
data class ApiResponse<T>(val data: T)

interface SyncApi {
    @POST("sync/pull")
    suspend fun pull(@Body body: SyncPullRequest): ApiResponse<List<RemoteSyncEvent>>

    @POST("sync/push")
    suspend fun push(@Body body: SyncPushRequest): ApiResponse<PushResponse>
}

data class PushResponse(val synced: Int)
