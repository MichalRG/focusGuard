package com.krzyzowski.focusguard.unlock

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.ZoneOffset
import java.util.Locale

class UnlockTimeTextTest {
    private val text = UnlockTimeText(ZoneOffset.UTC, Locale.UK)

    @Test
    fun `an unlock under a minute ago reads as just now`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-29T14:32:00Z"),
            now = Instant.parse("2026-10-29T14:32:59Z"),
        )

        assertEquals("14:32 · just now", described)
    }

    @Test
    fun `an unlock earlier today shows the clock time and minutes ago`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-29T14:32:00Z"),
            now = Instant.parse("2026-10-29T15:31:00Z"),
        )

        assertEquals("14:32 · 59 min ago", described)
    }

    @Test
    fun `an unlock exactly whole hours ago omits the minutes`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-29T12:00:00Z"),
            now = Instant.parse("2026-10-29T14:00:00Z"),
        )

        assertEquals("12:00 · 2 h ago", described)
    }

    @Test
    fun `an unlock hours ago shows hours and minutes`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-29T12:27:00Z"),
            now = Instant.parse("2026-10-29T14:32:00Z"),
        )

        assertEquals("12:27 · 2 h 5 min ago", described)
    }

    @Test
    fun `an unlock on an earlier day shows the date`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-28T23:50:00Z"),
            now = Instant.parse("2026-10-29T00:10:00Z"),
        )

        assertEquals("Wed 28 Oct, 23:50 · 20 min ago", described)
    }

    @Test
    fun `an unlock a day ago reads as one day ago`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-28T09:00:00Z"),
            now = Instant.parse("2026-10-29T10:00:00Z"),
        )

        assertEquals("Wed 28 Oct, 09:00 · 1 day ago", described)
    }

    @Test
    fun `an unlock several days ago reads in days`() {
        val described = text.describe(
            unlockedAt = Instant.parse("2026-10-26T09:00:00Z"),
            now = Instant.parse("2026-10-29T10:00:00Z"),
        )

        assertEquals("Mon 26 Oct, 09:00 · 3 days ago", described)
    }
}
