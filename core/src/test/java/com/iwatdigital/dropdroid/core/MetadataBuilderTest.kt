package com.iwatdigital.dropdroid.core

import com.iwatdigital.dropdroid.core.metadata.MetadataBuilder
import com.iwatdigital.dropdroid.core.metadata.SelectedFileInput
import com.iwatdigital.dropdroid.core.model.DeviceProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class MetadataBuilderTest {
    @Test
    fun `metadata aggregates totals`() {
        val profile = DeviceProfile("device-123", "Pixel", "0.1.0")
        val files = listOf(
            SelectedFileInput("a.jpg", "image/jpeg", 1024, "hash-a"),
            SelectedFileInput("b.mp4", "video/mp4", 2048, "hash-b")
        )

        val result = MetadataBuilder.buildTransferMetadata(profile, files)

        assertEquals(2, result.files.size)
        assertEquals(3072, result.totalBytes)
        assertEquals(profile.deviceId, result.sender.deviceId)
    }
}
