package com.iwatdigital.dropdroid.transport.wifidirect

import android.net.wifi.p2p.WifiP2pDevice
import com.iwatdigital.dropdroid.core.model.NearbyDevice
import com.iwatdigital.dropdroid.core.model.TransportType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Skeleton fallback manager for Wi-Fi Direct + QR pairing.
 * Real socket handling will be expanded in Milestone E.
 */
class WifiDirectTransportManager {
    private val _devices = MutableStateFlow<List<NearbyDevice>>(emptyList())
    val devices: StateFlow<List<NearbyDevice>> = _devices

    fun onDeviceDiscovered(device: WifiP2pDevice) {
        val mapped = NearbyDevice(
            endpointId = device.deviceAddress,
            name = device.deviceName,
            transport = TransportType.WIFI_DIRECT
        )
        _devices.value = _devices.value + mapped
    }

    fun clear() {
        _devices.value = emptyList()
    }
}
