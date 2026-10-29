package com.krzyzowski.focusguard.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId

class UnlockLog(private val dataStore: DataStore<Preferences>, private val zone: () -> ZoneId) {
    val lastUnlock: Flow<Instant?> = dataStore.data.map { it.lastUnlock() }

    val dailyCounts: Flow<DailyUnlockCounts> = dataStore.data.map { it.dailyCounts() }

    suspend fun record(at: Instant): RecordedUnlock {
        lateinit var recorded: RecordedUnlock
        dataStore.edit {
            val today = at.atZone(zone()).toLocalDate()
            val counts = it.dailyCounts().recordUnlockOn(today)
            recorded = RecordedUnlock(at = at, previousUnlock = it.lastUnlock(), unlocksToday = counts.countOn(today))
            it[LAST_UNLOCK_EPOCH_MS] = at.toEpochMilli()
            it[DAILY_UNLOCK_COUNTS] = counts.encode()
        }
        return recorded
    }

    private fun Preferences.lastUnlock(): Instant? = this[LAST_UNLOCK_EPOCH_MS]?.let(Instant::ofEpochMilli)

    private fun Preferences.dailyCounts(): DailyUnlockCounts =
        this[DAILY_UNLOCK_COUNTS]?.let(DailyUnlockCounts::decode) ?: DailyUnlockCounts.NONE

    private companion object {
        val LAST_UNLOCK_EPOCH_MS = longPreferencesKey("last_unlock_epoch_ms")
        val DAILY_UNLOCK_COUNTS = stringPreferencesKey("daily_unlock_counts")
    }
}
