package com.iwatdigital.dropdroid.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iwatdigital.dropdroid.core.model.TransportType

@Entity(tableName = "transfer_history")
data class TransferHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transferId: String,
    val direction: String,
    val peerName: String,
    val peerDeviceId: String,
    val filesJson: String,
    val status: String,
    val totalBytes: Long,
    val completedBytes: Long,
    val createdAt: Long,
    val transport: TransportType
)
