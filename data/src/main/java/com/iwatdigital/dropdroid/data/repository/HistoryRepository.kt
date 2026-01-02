package com.iwatdigital.dropdroid.data.repository

import com.iwatdigital.dropdroid.core.model.FileMetadata
import com.iwatdigital.dropdroid.core.model.TransferResult
import com.iwatdigital.dropdroid.core.model.TransferState
import com.iwatdigital.dropdroid.core.model.TransportType
import com.iwatdigital.dropdroid.data.db.DropDroidDao
import com.iwatdigital.dropdroid.data.db.TransferHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HistoryRepository(
    private val dao: DropDroidDao,
    private val json: Json
) {
    fun observeHistory(): Flow<List<TransferHistoryEntity>> = dao.observeHistory()

    suspend fun recordState(
        transferId: String,
        direction: String,
        peerName: String,
        peerDeviceId: String,
        files: List<FileMetadata>,
        status: String,
        totalBytes: Long,
        completedBytes: Long,
        transport: TransportType
    ) {
        val filesJson = json.encodeToString(files)
        val entity = TransferHistoryEntity(
            transferId = transferId,
            direction = direction,
            peerName = peerName,
            peerDeviceId = peerDeviceId,
            filesJson = filesJson,
            status = status,
            totalBytes = totalBytes,
            completedBytes = completedBytes,
            createdAt = System.currentTimeMillis(),
            transport = transport
        )
        dao.upsertHistory(entity)
    }

    suspend fun finalizeResult(
        transferId: String,
        result: TransferResult,
        peerName: String,
        peerDeviceId: String,
        files: List<FileMetadata>,
        transport: TransportType
    ) {
        val status = if (result.success) "completed" else "failed"
        recordState(
            transferId = transferId,
            direction = if (result.savedUris.isEmpty()) "sent" else "received",
            peerName = peerName,
            peerDeviceId = peerDeviceId,
            files = files,
            status = status,
            totalBytes = result.savedUris.size.toLong(),
            completedBytes = result.savedUris.size.toLong(),
            transport = transport
        )
    }

    fun observeUiHistory(): Flow<List<TransferState.Completed>> = observeHistory().map { history ->
        history.map {
            TransferState.Completed(
                result = TransferResult(
                    transferId = it.transferId,
                    success = it.status == "completed"
                )
            )
        }
    }
}
