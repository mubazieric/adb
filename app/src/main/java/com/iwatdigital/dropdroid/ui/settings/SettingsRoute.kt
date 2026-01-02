package com.iwatdigital.dropdroid.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsRoute(onBack: () -> Unit) {
    val receiving = remember { mutableStateOf(true) }
    val contactsOnly = remember { mutableStateOf(false) }
    val discoveryAggression = remember { mutableStateOf(0.5f) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Device name and diagnostics are configured in future milestone.")
            RowSetting(title = "Receiving enabled", description = "Allow others to see your device") {
                Switch(checked = receiving.value, onCheckedChange = { receiving.value = it })
            }
            RowSetting(title = "Approve-only mode", description = "Require prompt before receiving") {
                Switch(checked = contactsOnly.value, onCheckedChange = { contactsOnly.value = it })
            }
            Text("Discovery intensity")
            Slider(
                value = discoveryAggression.value,
                onValueChange = { discoveryAggression.value = it }
            )
        }
    }
}

@Composable
private fun RowSetting(
    title: String,
    description: String,
    trailing: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(title)
                Text(description)
            }
            trailing()
        }
    }
}
