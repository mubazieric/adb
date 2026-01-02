package com.iwatdigital.dropdroid.core.model

import kotlinx.serialization.Serializable

@Serializable
data class DeviceProfile(
    val deviceId: String,
    val deviceName: String,
    val appVersion: String,
    val avatarEmoji: String = "\uD83D\uDCBB"
)
