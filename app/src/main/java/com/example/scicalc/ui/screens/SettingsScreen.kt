package com.example.scicalc.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.scicalc.billing.BillingManager
import com.example.scicalc.engine.AngleMode
import com.example.scicalc.ui.CalculatorViewModel

private const val DonationProductId = "donation_tier1"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: CalculatorViewModel, navController: NavHostController) {
    val settings by viewModel.settingsState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val billingManager = remember { BillingManager(context) }
    val billingState by billingManager.billingState.collectAsState()

    LaunchedEffect(Unit) {
        billingManager.startConnection()
        billingManager.queryDonationProduct(DonationProductId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Angle mode", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingChip(label = "DEG", selected = settings.angleMode == AngleMode.DEGREES) {
                    viewModel.setAngleMode(AngleMode.DEGREES)
                }
                SettingChip(label = "RAD", selected = settings.angleMode == AngleMode.RADIANS) {
                    viewModel.setAngleMode(AngleMode.RADIANS)
                }
                SettingChip(label = "GRAD", selected = settings.angleMode == AngleMode.GRADIANS) {
                    viewModel.setAngleMode(AngleMode.GRADIANS)
                }
            }
            SettingToggle("Fullscreen", settings.fullscreen, viewModel::updateFullscreen)
            SettingToggle("Keep screen on", settings.keepScreenOn, viewModel::updateKeepScreenOn)
            SettingToggle("Haptics", settings.haptics, viewModel::updateHaptics)
            SettingToggle("Sound", settings.sound, viewModel::updateSound)
            SettingToggle("Show ads", settings.showAds, viewModel::updateShowAds)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Support developer", style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Donate to support development. Configure Billing in Play Console for production.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (activity != null && billingState.isReady) {
                                    billingManager.launchPurchase(activity, billingState.productDetails)
                                }
                            }
                        ) {
                            Text("Donate")
                        }
                        Button(onClick = { billingManager.restorePurchases() }) {
                            Text("Restore purchases")
                        }
                    }
                    if (!billingState.isReady || billingState.productDetails == null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Billing not configured", color = MaterialTheme.colorScheme.error)
                    }
                    if (billingState.message.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(billingState.message)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("About", style = MaterialTheme.typography.titleMedium)
            Text("SciCalc v1.0.0")
        }
    }
}

@Composable
private fun SettingToggle(label: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

@Composable
private fun SettingChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(text = label)
    }
}
