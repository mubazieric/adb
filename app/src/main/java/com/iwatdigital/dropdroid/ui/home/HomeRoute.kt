package com.iwatdigital.dropdroid.ui.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.iwatdigital.dropdroid.core.model.NearbyDevice
import com.iwatdigital.dropdroid.core.model.TransferState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeRoute(
    state: HomeUiState,
    onToggleReceiving: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onDeviceSelected: (NearbyDevice) -> Unit,
    onFilesPicked: (List<android.net.Uri>) -> Unit
) {
    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentMultiple(),
        onResult = { uris -> onFilesPicked(uris) }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DropDroid Nearby") },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Outlined.History, contentDescription = "History")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (state.isReceiving) "Receiving: On" else "Receiving: Off",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = state.deviceProfile.deviceName, color = Color.Gray)
                }
                Switch(checked = state.isReceiving, onCheckedChange = { onToggleReceiving() })
            }

            androidx.compose.material3.Button(onClick = { pickLauncher.launch(arrayOf("*/*")) }) {
                Text("Pick files to send")
            }

            StatusRow(state.transferState)

            Text(
                text = "Nearby devices",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.devices, key = { it.endpointId }) { device ->
                    DeviceCard(device = device, onClick = { onDeviceSelected(device) })
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(device: NearbyDevice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = device.avatarEmoji, style = MaterialTheme.typography.headlineMedium)
            Text(text = device.name, fontWeight = FontWeight.SemiBold)
            Text(text = device.transport.name, color = Color.Gray)
        }
    }
}

@Composable
private fun StatusRow(state: TransferState) {
    val (icon, text) = when (state) {
        TransferState.Discovering -> Icons.Outlined.Bluetooth to "Scanning for devices…"
        is TransferState.DevicesFound -> Icons.Outlined.Bluetooth to "Devices nearby: ${state.devices.size}"
        is TransferState.InProgress -> Icons.Outlined.Bluetooth to "Transferring ${state.progress.percentage}%"
        is TransferState.Error -> Icons.Outlined.CloudOff to "Error: ${state.message}"
        else -> Icons.Outlined.Bluetooth to "Idle"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null)
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}
