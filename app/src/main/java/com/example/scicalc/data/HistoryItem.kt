package com.example.scicalc.data

import kotlinx.serialization.Serializable

@Serializable
data class HistoryItem(
    val expression: String,
    val result: String,
    val timestamp: Long
)
