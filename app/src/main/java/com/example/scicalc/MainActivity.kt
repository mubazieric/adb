package com.example.scicalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scicalc.ads.AdsInitializer
import com.example.scicalc.data.SettingsRepository
import com.example.scicalc.ui.SciCalcApp
import com.example.scicalc.ui.theme.SciCalcTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        AdsInitializer.initialize(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val settingsRepository = SettingsRepository(applicationContext)

        setContent {
            SciCalcTheme {
                SciCalcApp(settingsRepository = settingsRepository)
            }
        }
    }
}
