package com.iwatdigital.dropdroid.ui

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.iwatdigital.dropdroid.ui.home.HomeRoute
import com.iwatdigital.dropdroid.ui.home.HomeViewModel
import com.iwatdigital.dropdroid.ui.history.HistoryRoute
import com.iwatdigital.dropdroid.ui.settings.SettingsRoute
import com.iwatdigital.dropdroid.ui.theme.DropDroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        homeViewModel.handleShareIntent(intent)
        setContent {
            DropDroidTheme {
                window.statusBarColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.toArgb()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        val state by homeViewModel.uiState
                        HomeRoute(
                            state = state,
                            onToggleReceiving = homeViewModel::toggleReceiving,
                            onOpenHistory = { navController.navigate("history") },
                            onOpenSettings = { navController.navigate("settings") },
                            onDeviceSelected = homeViewModel::onDeviceSelected,
                            onFilesPicked = homeViewModel::onFilesPicked
                        )
                    }
                    composable("history") { HistoryRoute(onBack = { navController.popBackStack() }) }
                    composable("settings") { SettingsRoute(onBack = { navController.popBackStack() }) }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        homeViewModel.handleShareIntent(intent)
    }
}
