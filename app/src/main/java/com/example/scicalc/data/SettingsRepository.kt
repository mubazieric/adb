package com.example.scicalc.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.scicalc.engine.AngleMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val angleMode = stringPreferencesKey("angle_mode")
        val fullscreen = booleanPreferencesKey("fullscreen")
        val haptics = booleanPreferencesKey("haptics")
        val showAds = booleanPreferencesKey("show_ads")
        val sound = booleanPreferencesKey("sound")
        val keepScreenOn = booleanPreferencesKey("keep_screen_on")
    }

    val settings: Flow<SettingsData> = context.dataStore.data.map { prefs ->
        SettingsData(
            angleMode = AngleMode.valueOf(prefs[Keys.angleMode] ?: AngleMode.DEGREES.name),
            fullscreen = prefs[Keys.fullscreen] ?: false,
            haptics = prefs[Keys.haptics] ?: true,
            showAds = prefs[Keys.showAds] ?: true,
            sound = prefs[Keys.sound] ?: false,
            keepScreenOn = prefs[Keys.keepScreenOn] ?: false
        )
    }

    suspend fun updateAngleMode(mode: AngleMode) {
        context.dataStore.edit { it[Keys.angleMode] = mode.name }
    }

    suspend fun updateFullscreen(enabled: Boolean) {
        context.dataStore.edit { it[Keys.fullscreen] = enabled }
    }

    suspend fun updateHaptics(enabled: Boolean) {
        context.dataStore.edit { it[Keys.haptics] = enabled }
    }

    suspend fun updateShowAds(enabled: Boolean) {
        context.dataStore.edit { it[Keys.showAds] = enabled }
    }

    suspend fun updateSound(enabled: Boolean) {
        context.dataStore.edit { it[Keys.sound] = enabled }
    }

    suspend fun updateKeepScreenOn(enabled: Boolean) {
        context.dataStore.edit { it[Keys.keepScreenOn] = enabled }
    }
}

data class SettingsData(
    val angleMode: AngleMode,
    val fullscreen: Boolean,
    val haptics: Boolean,
    val showAds: Boolean,
    val sound: Boolean,
    val keepScreenOn: Boolean
)
