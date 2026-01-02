package com.iwatdigital.dropdroid.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DropDroidDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHistory(entity: TransferHistoryEntity)

    @Query("SELECT * FROM transfer_history ORDER BY createdAt DESC")
    fun observeHistory(): Flow<List<TransferHistoryEntity>>

    @Query("DELETE FROM transfer_history WHERE createdAt < :cutoffMillis")
    suspend fun purgeOld(cutoffMillis: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTrustedPeer(entity: TrustedPeerEntity)

    @Query("SELECT * FROM trusted_peers WHERE deviceId = :deviceId LIMIT 1")
    suspend fun findTrustedPeer(deviceId: String): TrustedPeerEntity?

    @Query("SELECT * FROM trusted_peers ORDER BY lastSeen DESC")
    fun observeTrustedPeers(): Flow<List<TrustedPeerEntity>>
}
