package com.iwatdigital.dropdroid.data.repository

import com.iwatdigital.dropdroid.data.db.DropDroidDao
import com.iwatdigital.dropdroid.data.db.TrustedPeerEntity
import kotlinx.coroutines.flow.Flow

class TrustedPeersRepository(
    private val dao: DropDroidDao
){
    fun observeTrustedPeers(): Flow<List<TrustedPeerEntity>> = dao.observeTrustedPeers()

    suspend fun markTrusted(deviceId: String, displayName: String, alwaysAllow: Boolean) {
        dao.upsertTrustedPeer(
            TrustedPeerEntity(
                deviceId = deviceId,
                displayName = displayName,
                alwaysAllow = alwaysAllow,
                lastSeen = System.currentTimeMillis()
            )
        )
    }

    suspend fun isTrusted(deviceId: String): Boolean =
        dao.findTrustedPeer(deviceId)?.alwaysAllow == true
}
