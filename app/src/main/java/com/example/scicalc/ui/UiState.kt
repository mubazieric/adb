package com.example.scicalc.ui

import com.example.scicalc.data.HistoryItem
import com.example.scicalc.engine.AngleMode


data class CalculatorState(
    val expression: String = "",
    val result: String = "",
    val isError: Boolean = false,
    val memoryValue: Double = 0.0,
    val isSecond: Boolean = false,
    val angleMode: AngleMode = AngleMode.DEGREES
)

data class HistoryState(
    val items: List<HistoryItem> = emptyList()
)

data class SettingsState(
    val angleMode: AngleMode = AngleMode.DEGREES,
    val fullscreen: Boolean = false,
    val haptics: Boolean = true,
    val showAds: Boolean = true,
    val sound: Boolean = false,
    val keepScreenOn: Boolean = false
)
