package com.example.scicalc.ui

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scicalc.data.HistoryRepository
import com.example.scicalc.data.SettingsRepository
import com.example.scicalc.ui.screens.CalculatorScreen
import com.example.scicalc.ui.screens.HistoryScreen
import com.example.scicalc.ui.screens.SettingsScreen

@Composable
fun SciCalcApp(settingsRepository: SettingsRepository) {
    val context = LocalContext.current
    val historyRepository = rememberHistoryRepository(context)
    val viewModel: CalculatorViewModel = viewModel(
        factory = CalculatorViewModelFactory(settingsRepository, historyRepository)
    )
    val settings by viewModel.settingsState.collectAsState()
    val navController = rememberNavController()

    HandleSystemUi(settings)

    NavHost(
        navController = navController,
        startDestination = "calculator",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("calculator") {
            CalculatorScreen(viewModel = viewModel, navController = navController)
        }
        composable("settings") {
            SettingsScreen(viewModel = viewModel, navController = navController)
        }
        composable("history") {
            HistoryScreen(viewModel = viewModel, navController = navController)
        }
    }
}

@Composable
private fun HandleSystemUi(settings: SettingsState) {
    val view = LocalView.current
    val context = LocalContext.current
    val activity = context.findActivity() ?: return
    val window = activity.window

    SideEffect {
        if (settings.keepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(settings.fullscreen) {
        WindowCompat.setDecorFitsSystemWindows(window, !settings.fullscreen)
        val controller = WindowInsetsControllerCompat(window, view)
        if (settings.fullscreen) {
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
private fun rememberHistoryRepository(context: Context): HistoryRepository =
    androidx.compose.runtime.remember(context) { HistoryRepository(context) }

class CalculatorViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(settingsRepository, historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
