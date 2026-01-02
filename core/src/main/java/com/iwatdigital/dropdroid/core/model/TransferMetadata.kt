package com.iwatdigital.dropdroid.core.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class TransferMetadata(
    val transferId: String = UUID.randomUUID().toString(),
    val sender: DeviceProfile,
    val recipientName: String? = null,
    val files: List<FileMetadata>,
    val totalBytes: Long,
    val createdAt: Long = System.currentTimeMillis()
)
