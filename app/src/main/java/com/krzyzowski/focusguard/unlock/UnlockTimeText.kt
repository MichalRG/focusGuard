package com.krzyzowski.focusguard.unlock

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class UnlockTimeText(private val zone: ZoneId, locale: Locale) {
    private val timeOfDay = DateTimeFormatter.ofPattern("HH:mm", locale)
    private val dayAndTime = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm", locale)

    fun describe(unlockedAt: Instant, now: Instant): String =
        "${clockTime(unlockedAt, now)} · ${elapsed(unlockedAt, now)}"

    private fun clockTime(unlockedAt: Instant, now: Instant): String {
        val unlocked = unlockedAt.atZone(zone)
        val sameDay = unlocked.toLocalDate() == now.atZone(zone).toLocalDate()
        return (if (sameDay) timeOfDay else dayAndTime).format(unlocked)
    }

    private fun elapsed(unlockedAt: Instant, now: Instant): String {
        val totalMinutes = Duration.between(unlockedAt, now).toMinutes().coerceAtLeast(0)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        val days = hours / 24
        return when {
            totalMinutes == 0L -> "just now"
            hours == 0L -> "$minutes min ago"
            days == 1L -> "1 day ago"
            days > 1L -> "$days days ago"
            minutes == 0L -> "$hours h ago"
            else -> "$hours h $minutes min ago"
        }
    }
}
