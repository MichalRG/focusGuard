package com.krzyzowski.focusguard.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.krzyzowski.focusguard.words.UnlockWord
import kotlin.random.Random

class WordOfUnlock(
    private val dataStore: DataStore<Preferences>,
    private val words: List<UnlockWord>,
    private val random: Random,
) {
    suspend fun nextForCard(): UnlockWord {
        var index = 0
        dataStore.edit {
            val current = it[WORD_INDEX]?.takeIf { stored -> stored in words.indices }
            val timesShown = it[WORD_TIMES_SHOWN] ?: 0
            if (current != null && timesShown < CARDS_PER_WORD) {
                index = current
                it[WORD_TIMES_SHOWN] = timesShown + 1
            } else {
                index = otherIndexThan(current)
                it[WORD_INDEX] = index
                it[WORD_TIMES_SHOWN] = 1
            }
        }
        return words[index]
    }

    private fun otherIndexThan(current: Int?): Int {
        if (current == null || words.size == 1) return random.nextInt(words.size)
        val pick = random.nextInt(words.size - 1)
        return if (pick >= current) pick + 1 else pick
    }

    companion object {
        const val CARDS_PER_WORD = 10
        private val WORD_INDEX = intPreferencesKey("word_index")
        private val WORD_TIMES_SHOWN = intPreferencesKey("word_times_shown")
    }
}
