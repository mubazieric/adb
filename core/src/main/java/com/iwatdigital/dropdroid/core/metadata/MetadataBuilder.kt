package com.iwatdigital.dropdroid.core.metadata

import com.iwatdigital.dropdroid.core.model.DeviceProfile
import com.iwatdigital.dropdroid.core.model.FileMetadata
import com.iwatdigital.dropdroid.core.model.TransferMetadata

data class SelectedFileInput(
    val name: String,
    val mimeType: String,
    val sizeBytes: Long,
    val sha256: String
)

object MetadataBuilder {
    fun buildTransferMetadata(
        profile: DeviceProfile,
        files: List<SelectedFileInput>
    ): TransferMetadata {
        val metaFiles = files.map {
            FileMetadata(
                name = it.name,
                mimeType = it.mimeType,
                sizeBytes = it.sizeBytes,
                sha256 = it.sha256
            )
        }
        val totalBytes = metaFiles.sumOf { it.sizeBytes }
        return TransferMetadata(
            sender = profile,
            files = metaFiles,
            totalBytes = totalBytes
        )
    }
}
