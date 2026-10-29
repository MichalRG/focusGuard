package com.krzyzowski.focusguard.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Duration

class FocusSettings(private val dataStore: DataStore<Preferences>) {
    val enabled: Flow<Boolean> = dataStore.data.map { it[FOCUS_ENABLED] ?: false }

    // Null means no limit: the card shows on every unlock.
    val promptWindow: Flow<Duration?> = dataStore.data.map { prefs ->
        when (val minutes = prefs[PROMPT_WINDOW_MINUTES]) {
            null -> DEFAULT_PROMPT_WINDOW
            NO_LIMIT_MINUTES -> null
            else -> Duration.ofMinutes(minutes)
        }
    }

    suspend fun setEnabled(enabled: Boolean) {
        dataStore.edit { it[FOCUS_ENABLED] = enabled }
    }

    suspend fun setPromptWindow(window: Duration?) {
        dataStore.edit { it[PROMPT_WINDOW_MINUTES] = window?.toMinutes() ?: NO_LIMIT_MINUTES }
    }

    companion object {
        val DEFAULT_PROMPT_WINDOW: Duration = Duration.ofHours(2)
        private const val NO_LIMIT_MINUTES = 0L
        private val FOCUS_ENABLED = booleanPreferencesKey("focus_enabled")
        private val PROMPT_WINDOW_MINUTES = longPreferencesKey("prompt_window_minutes")
    }
}
