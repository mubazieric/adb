package com.iwatdigital.dropdroid.transport.nearby

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.iwatdigital.dropdroid.core.crypto.CryptoEngine
import com.iwatdigital.dropdroid.core.model.DeviceProfile
import com.iwatdigital.dropdroid.core.model.IncomingRequest
import com.iwatdigital.dropdroid.core.model.NearbyDevice
import com.iwatdigital.dropdroid.core.model.TransferMetadata
import com.iwatdigital.dropdroid.core.model.TransferState
import com.iwatdigital.dropdroid.core.model.TransportType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyTransportManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val connectionsClient: ConnectionsClient by lazy { Nearby.getConnectionsClient(context) }

    private val _state = MutableStateFlow<TransferState>(TransferState.Idle)
    val state: StateFlow<TransferState> = _state

    private val _incomingRequests = MutableSharedFlow<IncomingRequest>()
    val incomingRequests: SharedFlow<IncomingRequest> = _incomingRequests

    fun startDiscovery(serviceId: String, localProfile: DeviceProfile) {
        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        connectionsClient.startDiscovery(
            serviceId,
            endpointDiscoveryCallback(localProfile),
            options
        )
        _state.value = TransferState.Discovering
    }

    fun startAdvertising(serviceId: String, localProfile: DeviceProfile) {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
        connectionsClient.startAdvertising(
            localProfile.deviceName,
            serviceId,
            connectionLifecycleCallback(localProfile),
            options
        )
    }

    fun stopAll() {
        connectionsClient.stopAdvertising()
        connectionsClient.stopDiscovery()
        connectionsClient.stopAllEndpoints()
        _state.value = TransferState.Idle
    }

    private fun endpointDiscoveryCallback(localProfile: DeviceProfile) =
        object : com.google.android.gms.nearby.connection.EndpointDiscoveryCallback() {
            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
                val device = NearbyDevice(
                    endpointId = endpointId,
                    name = info.endpointName,
                    transport = TransportType.NEARBY
                )
                _state.value = TransferState.DevicesFound(listOf(device))
            }

            override fun onEndpointLost(endpointId: String) {
                if (_state.value is TransferState.DevicesFound) {
                    _state.value = TransferState.Discovering
                }
            }
        }

    private fun connectionLifecycleCallback(localProfile: DeviceProfile) =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                val handshake = CryptoEngine.generateKeyPair()
                val metadata = TransferMetadata(
                    sender = localProfile,
                    files = emptyList(),
                    totalBytes = 0
                )
                val payloadBytes = json.encodeToString(metadata).toByteArray()
                connectionsClient.acceptConnection(endpointId, payloadCallback(endpointId))
                connectionsClient.sendPayload(endpointId, Payload.fromBytes(payloadBytes))
                _state.value = TransferState.ReceivingRequest(
                    IncomingRequest(
                        from = NearbyDevice(
                            endpointId = endpointId,
                            name = connectionInfo.endpointName,
                            transport = TransportType.NEARBY
                        ),
                        metadata = metadata
                    )
                )
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                // Surface result to UI for future phases
            }

            override fun onDisconnected(endpointId: String) {
                _state.value = TransferState.Error("Disconnected from $endpointId")
            }
        }

    private fun payloadCallback(endpointId: String) = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let { bytes ->
                scope.launch {
                    runCatching {
                        val metadata = json.decodeFromString(
                            TransferMetadata.serializer(),
                            bytes.decodeToString()
                        )
                        _incomingRequests.emit(
                            IncomingRequest(
                                from = NearbyDevice(
                                    endpointId = endpointId,
                                    name = metadata.sender.deviceName,
                                    transport = TransportType.NEARBY
                                ),
                                metadata = metadata
                            )
                        )
                    }.onFailure {
                        _state.value = TransferState.Error("Failed to parse metadata", it)
                    }
                }
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            val progress = TransferState.InProgress(
                com.iwatdigital.dropdroid.core.model.Progress(
                    transferId = update.payloadId.toString(),
                    totalBytes = update.totalBytes,
                    transferredBytes = update.bytesTransferred
                )
            )
            _state.value = progress
        }
    }
}
