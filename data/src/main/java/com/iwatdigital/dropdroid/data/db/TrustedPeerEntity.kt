package com.iwatdigital.dropdroid.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trusted_peers")
data class TrustedPeerEntity(
    @PrimaryKey val deviceId: String,
    val displayName: String,
    val alwaysAllow: Boolean = false,
    val lastSeen: Long = System.currentTimeMillis()
)
