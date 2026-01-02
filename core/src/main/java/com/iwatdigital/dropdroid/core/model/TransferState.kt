package com.iwatdigital.dropdroid.core.model

sealed class TransferState {
    object Idle : TransferState()
    object Discovering : TransferState()
    data class DevicesFound(val devices: List<NearbyDevice>) : TransferState()
    data class Requesting(val target: NearbyDevice) : TransferState()
    data class ReceivingRequest(val request: IncomingRequest) : TransferState()
    data class InProgress(val progress: Progress) : TransferState()
    data class Completed(val result: TransferResult) : TransferState()
    data class Error(val message: String, val cause: Throwable? = null) : TransferState()
}

data class NearbyDevice(
    val endpointId: String,
    val name: String,
    val avatarEmoji: String = "\uD83D\uDCF1",
    val distanceMeters: Float? = null,
    val transport: TransportType
)

enum class TransportType {
    NEARBY, WIFI_DIRECT, QR
}

data class IncomingRequest(
    val from: NearbyDevice,
    val metadata: TransferMetadata
)

data class Progress(
    val transferId: String,
    val totalBytes: Long,
    val transferredBytes: Long,
    val currentFileName: String? = null,
    val percentage: Int = ((transferredBytes.toDouble() / totalBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
)

data class TransferResult(
    val transferId: String,
    val success: Boolean,
    val savedUris: List<String> = emptyList(),
    val errorMessage: String? = null
)
