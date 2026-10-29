package com.krzyzowski.focusguard.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.krzyzowski.focusguard.words.UnlockWord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.random.Random

class WordOfUnlockTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + Job())

    private val words = listOf(
        UnlockWord("puddle", "kałuża"),
        UnlockWord("creek", "potok"),
        UnlockWord("boon", "dobrodziejstwo"),
    )

    private fun wordOfUnlock(seed: Int) = WordOfUnlock(
        PreferenceDataStoreFactory.create(scope = dataStoreScope) {
            File(folder.root, "focus.preferences_pb")
        },
        words,
        Random(seed),
    )

    @After
    fun closeDataStore() {
        dataStoreScope.cancel()
    }

    @Test
    fun `the first card shows a word from the list`() = runTest {
        val word = wordOfUnlock(seed = 1).nextForCard()

        assertTrue(word in words)
    }

    @Test
    fun `the same word is shown on ten cards in a row`() = runTest {
        val wordOfUnlock = wordOfUnlock(seed = 1)

        val shown = List(10) { wordOfUnlock.nextForCard() }

        assertEquals(1, shown.distinct().size)
    }

    @Test
    fun `the eleventh card shows a different word`() = runTest {
        val wordOfUnlock = wordOfUnlock(seed = 1)
        val first = List(10) { wordOfUnlock.nextForCard() }.last()

        val eleventh = wordOfUnlock.nextForCard()

        assertNotEquals(first, eleventh)
    }

    @Test
    fun `a new word is kept for its own ten cards`() = runTest {
        val wordOfUnlock = wordOfUnlock(seed = 7)
        repeat(10) { wordOfUnlock.nextForCard() }

        val nextRun = List(10) { wordOfUnlock.nextForCard() }

        assertEquals(1, nextRun.distinct().size)
    }

    @Test
    fun `every word in the list can come up`() = runTest {
        val wordOfUnlock = wordOfUnlock(seed = 3)

        val seen = List(300) { wordOfUnlock.nextForCard() }.toSet()

        assertEquals(words.toSet(), seen)
    }
}
