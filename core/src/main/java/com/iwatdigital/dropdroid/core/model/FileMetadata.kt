package com.iwatdigital.dropdroid.core.model

import kotlinx.serialization.Serializable

@Serializable
data class FileMetadata(
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val sha256: String
)
