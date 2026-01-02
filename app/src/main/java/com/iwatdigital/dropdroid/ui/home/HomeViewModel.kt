package com.iwatdigital.dropdroid.ui.home

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iwatdigital.dropdroid.core.model.DeviceProfile
import com.iwatdigital.dropdroid.core.metadata.MetadataBuilder
import com.iwatdigital.dropdroid.core.metadata.SelectedFileInput
import com.iwatdigital.dropdroid.core.model.NearbyDevice
import com.iwatdigital.dropdroid.core.model.TransferMetadata
import com.iwatdigital.dropdroid.core.model.TransferState
import com.iwatdigital.dropdroid.data.repository.HistoryRepository
import com.iwatdigital.dropdroid.transport.nearby.NearbyTransportManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isReceiving: Boolean = true,
    val deviceProfile: DeviceProfile = DeviceProfile(
        deviceId = "unknown",
        deviceName = android.os.Build.MODEL,
        appVersion = "0.1.0"
    ),
    val devices: List<NearbyDevice> = emptyList(),
    val status: String = "Scanning…",
    val selectedFiles: List<Uri> = emptyList(),
    val transferState: TransferState = TransferState.Idle
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val nearbyTransportManager: NearbyTransportManager,
    private val historyRepository: HistoryRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        populateDeviceProfile()
        observeTransport()
        startAdvertisingAndDiscovery()
    }

    private fun populateDeviceProfile() {
        val id = android.provider.Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown"
        _uiState.value = _uiState.value.copy(
            deviceProfile = _uiState.value.deviceProfile.copy(deviceId = id)
        )
    }

    private fun observeTransport() {
        viewModelScope.launch {
            nearbyTransportManager.state.collect { state ->
                val devices = if (state is TransferState.DevicesFound) state.devices else _uiState.value.devices
                _uiState.value = _uiState.value.copy(transferState = state, devices = devices)
            }
        }
        viewModelScope.launch {
            nearbyTransportManager.incomingRequests.collect { incoming ->
                _uiState.value = _uiState.value.copy(
                    transferState = TransferState.ReceivingRequest(incoming)
                )
            }
        }
    }

    private fun startAdvertisingAndDiscovery() {
        val profile = _uiState.value.deviceProfile
        nearbyTransportManager.startAdvertising(SERVICE_ID, profile)
        nearbyTransportManager.startDiscovery(SERVICE_ID, profile)
    }

    fun toggleReceiving() {
        val updated = !_uiState.value.isReceiving
        _uiState.value = _uiState.value.copy(isReceiving = updated)
        if (updated) {
            startAdvertisingAndDiscovery()
        } else {
            nearbyTransportManager.stopAll()
        }
    }

    fun onDeviceSelected(device: NearbyDevice) {
        _uiState.value = _uiState.value.copy(status = "Preparing transfer to ${device.name}")
    }

    fun onFilesPicked(uris: List<Uri>) {
        _uiState.value = _uiState.value.copy(selectedFiles = uris)
    }

    fun buildMetadata(contentResolver: android.content.ContentResolver): TransferMetadata {
        val files = _uiState.value.selectedFiles.mapNotNull { uri ->
            val name = uri.lastPathSegment ?: return@mapNotNull null
            val type = contentResolver.getType(uri) ?: "application/octet-stream"
            val size = contentResolver.openAssetFileDescriptor(uri, "r")?.length ?: 0
            SelectedFileInput(
                name = name,
                mimeType = type,
                sizeBytes = size,
                sha256 = "pending"
            )
        }
        return MetadataBuilder.buildTransferMetadata(_uiState.value.deviceProfile, files)
    }

    fun handleShareIntent(intent: Intent?) {
        val action = intent?.action ?: return
        if (Intent.ACTION_SEND == action) {
            (intent.getParcelableExtra(Intent.EXTRA_STREAM) as? Uri)?.let { uri ->
                onFilesPicked(listOf(uri))
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action) {
            val extras = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
            if (!extras.isNullOrEmpty()) {
                onFilesPicked(extras)
            }
        }
    }

    companion object {
        const val SERVICE_ID = "com.iwatdigital.dropdroid.SERVICE"
    }
}
