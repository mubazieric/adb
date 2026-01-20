package com.example.scicalc.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.historyStore by preferencesDataStore(name = "history")

class HistoryRepository(private val context: Context) {
    private val key = stringPreferencesKey("history_json")
    private val json = Json { ignoreUnknownKeys = true }

    val history: Flow<List<HistoryItem>> = context.historyStore.data.map { prefs ->
        val raw = prefs[key].orEmpty()
        if (raw.isBlank()) emptyList() else runCatching {
            json.decodeFromString<List<HistoryItem>>(raw)
        }.getOrElse { emptyList() }
    }

    suspend fun addItem(item: HistoryItem) {
        context.historyStore.edit { prefs ->
            val current = prefs[key].orEmpty()
            val items = if (current.isBlank()) emptyList() else runCatching {
                json.decodeFromString<List<HistoryItem>>(current)
            }.getOrElse { emptyList() }
            val updated = (listOf(item) + items).take(50)
            prefs[key] = json.encodeToString(updated)
        }
    }

    suspend fun clear() {
        context.historyStore.edit { it[key] = "" }
    }
}
