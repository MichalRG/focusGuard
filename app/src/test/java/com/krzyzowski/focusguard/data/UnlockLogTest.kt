package com.krzyzowski.focusguard.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class UnlockLogTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val dataStoreScope = CoroutineScope(Dispatchers.IO + Job())

    private val unlockLog by lazy {
        UnlockLog(
            PreferenceDataStoreFactory.create(scope = dataStoreScope) {
                File(folder.root, "focus.preferences_pb")
            },
            ZoneOffset.UTC,
        )
    }

    @After
    fun closeDataStore() {
        dataStoreScope.cancel()
    }

    @Test
    fun `the first unlock has no previous unlock and becomes the last unlock`() = runTest {
        val firstUnlock = Instant.parse("2026-10-29T08:00:00Z")

        val recorded = unlockLog.record(firstUnlock)

        assertNull(recorded.previousUnlock)
        assertEquals(firstUnlock, unlockLog.lastUnlock.first())
    }

    @Test
    fun `a later unlock reports the earlier one as previous`() = runTest {
        val firstUnlock = Instant.parse("2026-10-29T08:00:00Z")
        val secondUnlock = Instant.parse("2026-10-29T10:05:00Z")
        unlockLog.record(firstUnlock)

        val recorded = unlockLog.record(secondUnlock)

        assertEquals(firstUnlock, recorded.previousUnlock)
        assertEquals(secondUnlock, unlockLog.lastUnlock.first())
    }

    @Test
    fun `no unlock is reported before any unlock was recorded`() = runTest {
        assertNull(unlockLog.lastUnlock.first())
    }

    @Test
    fun `an unlock soon after the previous one is within the prompt window`() = runTest {
        unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))

        val recorded = unlockLog.record(Instant.parse("2026-10-29T09:30:00Z"))

        assertTrue(recorded.needsPrompt(Duration.ofHours(2)))
    }

    @Test
    fun `an unlock exactly at the window edge is still within it`() = runTest {
        unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))

        val recorded = unlockLog.record(Instant.parse("2026-10-29T10:00:00Z"))

        assertTrue(recorded.needsPrompt(Duration.ofHours(2)))
    }

    @Test
    fun `an unlock long after the previous one is outside the prompt window`() = runTest {
        unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))

        val recorded = unlockLog.record(Instant.parse("2026-10-29T10:01:00Z"))

        assertFalse(recorded.needsPrompt(Duration.ofHours(2)))
    }

    @Test
    fun `the very first unlock is outside any prompt window`() = runTest {
        val recorded = unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))

        assertFalse(recorded.needsPrompt(Duration.ofHours(8)))
    }

    @Test
    fun `with no time limit even a long gap asks`() = runTest {
        unlockLog.record(Instant.parse("2026-10-28T08:00:00Z"))

        val recorded = unlockLog.record(Instant.parse("2026-10-29T20:00:00Z"))

        assertTrue(recorded.needsPrompt(null))
    }

    @Test
    fun `with no time limit the very first unlock asks`() = runTest {
        val recorded = unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))

        assertTrue(recorded.needsPrompt(null))
    }

    @Test
    fun `each unlock of the day gets the next number`() = runTest {
        unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))
        unlockLog.record(Instant.parse("2026-10-29T09:00:00Z"))

        val third = unlockLog.record(Instant.parse("2026-10-29T10:00:00Z"))

        assertEquals(3, third.unlocksToday)
    }

    @Test
    fun `the count starts again from one on a new day`() = runTest {
        unlockLog.record(Instant.parse("2026-10-28T22:00:00Z"))
        unlockLog.record(Instant.parse("2026-10-28T23:59:00Z"))

        val firstOfNewDay = unlockLog.record(Instant.parse("2026-10-29T00:01:00Z"))

        assertEquals(1, firstOfNewDay.unlocksToday)
        assertEquals(2, unlockLog.dailyCounts.first().countOn(LocalDate.parse("2026-10-28")))
    }

    @Test
    fun `days older than four weeks are forgotten`() = runTest {
        unlockLog.record(Instant.parse("2026-10-01T12:00:00Z"))
        unlockLog.record(Instant.parse("2026-10-02T12:00:00Z"))

        unlockLog.record(Instant.parse("2026-10-29T12:00:00Z"))

        val counts = unlockLog.dailyCounts.first()
        assertEquals(0, counts.countOn(LocalDate.parse("2026-10-01")))
        assertEquals(1, counts.countOn(LocalDate.parse("2026-10-02")))
    }

    @Test
    fun `the last seven days include days without unlocks`() = runTest {
        unlockLog.record(Instant.parse("2026-10-23T12:00:00Z"))
        unlockLog.record(Instant.parse("2026-10-29T08:00:00Z"))
        unlockLog.record(Instant.parse("2026-10-29T09:00:00Z"))

        val week = unlockLog.dailyCounts.first().lastDays(LocalDate.parse("2026-10-29"), 7)

        assertEquals(
            listOf(
                LocalDate.parse("2026-10-23") to 1,
                LocalDate.parse("2026-10-24") to 0,
                LocalDate.parse("2026-10-25") to 0,
                LocalDate.parse("2026-10-26") to 0,
                LocalDate.parse("2026-10-27") to 0,
                LocalDate.parse("2026-10-28") to 0,
                LocalDate.parse("2026-10-29") to 2,
            ),
            week,
        )
    }
}
